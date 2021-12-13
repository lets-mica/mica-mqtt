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

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.enums.MessageType;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import org.tio.utils.hutool.StrUtil;

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

	@Override
	public boolean send(Message message) {
		Objects.requireNonNull(mqttServer, "MqttServer require not Null.");
		// 1. 先发送到本服务
		MessageType messageType = message.getMessageType();
		if (MessageType.SUBSCRIBE == messageType) {
			sessionManager.addSubscribe(message.getTopic(), message.getFromClientId(), message.getQos());
		} else if (MessageType.UNSUBSCRIBE == messageType) {
			sessionManager.removeSubscribe(message.getTopic(), message.getFromClientId());
		} else if (MessageType.UP_STREAM == messageType) {
			sendToClient(message.getTopic(), message);
		} else if (MessageType.DOWN_STREAM == messageType) {
			sendToClient(message.getTopic(), message);
		}
		return sendAll(message);
	}

	@Override
	public boolean send(String clientId, Message message) {
		Objects.requireNonNull(mqttServer, "MqttServer require not Null.");
		message.setClientId(clientId);
		return send(message);
	}

	/**
	 * 发送消息到客户端
	 *
	 * @param topic   topic
	 * @param message Message
	 */
	private void sendToClient(String topic, Message message) {
		// 客户端id
		String clientId = message.getClientId();
		MqttQoS mqttQoS = MqttQoS.valueOf(message.getQos());
		if (StrUtil.isBlank(clientId)) {
			mqttServer.publishAll(topic, message.getPayload(), mqttQoS, message.isRetain());
		} else {
			mqttServer.publish(clientId, topic, message.getPayload(), mqttQoS, message.isRetain());
		}
	}

}
