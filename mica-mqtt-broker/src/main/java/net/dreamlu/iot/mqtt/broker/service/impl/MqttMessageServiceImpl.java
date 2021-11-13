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

package net.dreamlu.iot.mqtt.broker.service.impl;

import net.dreamlu.iot.mqtt.broker.service.IMqttMessageService;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.mica.core.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

/**
 * mqtt 消息服务，用于自定义业务
 *
 * @author L.cm
 */
@Service
public class MqttMessageServiceImpl implements IMqttMessageService {
	@Autowired
	private MqttServer mqttServer;

	@Override
	public void publishProcessing(Message message) {
		String topic = message.getTopic();
		MqttQoS mqttQoS = MqttQoS.valueOf(message.getQos());
		boolean retain = message.isRetain();
		// 消息需要发送到的客户端
		String clientId = message.getClientId();
		// TODO L.cm 待添加处理逻辑 https://gitee.com/596392912/mica-mqtt/issues/I4ECEO
		// TODO L.cm 示例是将消息转发给订阅的客户端，可对接规则引擎
		if (StringUtil.isBlank(clientId)) {
			mqttServer.publishAll(topic, ByteBuffer.wrap(message.getPayload()), mqttQoS, retain);
		} else {
			mqttServer.publish(clientId, topic, ByteBuffer.wrap(message.getPayload()), mqttQoS, retain);
		}
	}

}
