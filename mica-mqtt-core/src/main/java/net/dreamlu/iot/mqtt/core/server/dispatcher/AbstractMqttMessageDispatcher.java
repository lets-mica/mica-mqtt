/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & dreamlu.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dreamlu.iot.mqtt.core.server.dispatcher;

import net.dreamlu.iot.mqtt.codec.MqttMessageType;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.server.ServerTioConfig;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * 内部消息转发抽象
 *
 * @author L.cm
 */
public abstract class AbstractMqttMessageDispatcher implements IMqttMessageDispatcher {
	protected MqttServer mqttServer;
	protected IMqttSessionManager sessionManager;

	public void config(MqttServer mqttServer) {
		this.mqttServer = mqttServer;
		this.sessionManager = mqttServer.getServerCreator().getSessionManager();
	}

	/**
	 * 转发到所有订阅了该 topic 的设备
	 *
	 * @param message Message
	 * @return 是否成功
	 */
	abstract public boolean sendAll(Message message);

	/**
	 * 转发消息到设备，如果 clientId 就在本服务，会自行消化
	 *
	 * @param clientId clientId
	 * @param message  Message
	 * @return 是否成功
	 */
	abstract public boolean sendTo(String clientId, Message message);

	@Override
	public boolean send(Message message) {
		Objects.requireNonNull(mqttServer, "MqttServer require not Null.");
		// 1. 先发送到本服务
		MqttMessageType messageType = MqttMessageType.valueOf(message.getMessageType());
		if (MqttMessageType.PUBLISH == messageType) {
			ByteBuffer payload = ByteBuffer.wrap(message.getPayload());
			MqttQoS qoS = MqttQoS.valueOf(message.getQos());
			mqttServer.publishAll(message.getTopic(), payload, qoS, message.isRetain());
		} else if (MqttMessageType.SUBSCRIBE == messageType) {
			sessionManager.addSubscribe(message.getTopic(), message.getFromClientId(), message.getQos());
		} else if (MqttMessageType.UNSUBSCRIBE == messageType) {
			sessionManager.removeSubscribe(message.getTopic(), message.getFromClientId());
		}
		return sendAll(message);
	}

	@Override
	public boolean send(String clientId, Message message) {
		Objects.requireNonNull(mqttServer, "MqttServer require not Null.");
		// 1. 判断如果 clientId 就在本服务，存在则发送
		ServerTioConfig config = this.mqttServer.getServerConfig();
		ChannelContext context = Tio.getByBsId(config, clientId);
		if (context != null) {
			ByteBuffer payload = ByteBuffer.wrap(message.getPayload());
			MqttQoS qoS = MqttQoS.valueOf(message.getQos());
			return this.mqttServer.publish(context, clientId, message.getTopic(), payload, qoS, false);
		}
		return this.sendTo(clientId, message);
	}

}
