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

package net.dreamlu.iot.mqtt.core.server.broker;

import net.dreamlu.iot.mqtt.codec.*;
import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import org.tio.core.ChannelContext;
import org.tio.core.Node;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * broker 消息监听转发
 *
 * @author L.cm
 */
public class MqttBrokerMessageListener implements IMqttMessageListener {
	private final IMqttMessageDispatcher dispatcher;

	public MqttBrokerMessageListener(IMqttMessageDispatcher dispatcher) {
		this.dispatcher = Objects.requireNonNull(dispatcher, "MqttMessageDispatcher is null.");
	}

	@Override
	public void onMessage(ChannelContext context, String clientId, MqttPublishMessage publishMessage) {
		MqttFixedHeader fixedHeader = publishMessage.fixedHeader();
		MqttPublishVariableHeader variableHeader = publishMessage.variableHeader();
		// messageId
		int packetId = variableHeader.packetId();
		// topic
		String topic = variableHeader.topicName();
		// qos
		MqttQoS mqttQoS = fixedHeader.qosLevel();
		// payload
		ByteBuffer payload = publishMessage.payload();
		// message
		Message message = new Message();
		message.setId(packetId);
		// 注意：broker 消息转发是不需要设置 toClientId 而是应该按 topic 找到订阅的客户端进行发送
		message.setFromClientId(clientId);
		message.setTopic(topic);
		message.setQos(mqttQoS.value());
		if (payload != null) {
			message.setPayload(payload.array());
		}
		message.setMessageType(MqttMessageType.PUBLISH.value());
		message.setRetain(fixedHeader.isRetain());
		message.setDup(fixedHeader.isDup());
		message.setTimestamp(System.currentTimeMillis());
		Node clientNode = context.getClientNode();
		// 客户端 ip:端口
		message.setPeerHost(clientNode.getIp() + ':' + clientNode.getPort());
		dispatcher.send(message);
	}

}
