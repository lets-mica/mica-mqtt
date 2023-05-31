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

package net.dreamlu.iot.mqtt.broker.cluster;

import lombok.extern.slf4j.Slf4j;
import net.dreamlu.iot.mqtt.broker.enums.RedisKeys;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.iot.mqtt.core.server.serializer.IMessageSerializer;
import net.dreamlu.mica.core.utils.StringUtil;
import net.dreamlu.mica.redis.cache.MicaRedisCache;
import net.dreamlu.mica.redis.stream.MessageModel;
import net.dreamlu.mica.redis.stream.RStreamListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.stream.MapRecord;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

/**
 * 下行消息，发送到设备
 *
 * @author L.cm
 */
@Slf4j
public class RedisMqttMessageDownReceiver {
	private final IMessageSerializer messageSerializer;
	private final MqttServer mqttServer;

	public RedisMqttMessageDownReceiver(IMessageSerializer messageSerializer,
										MqttServer mqttServer) {
		this.messageSerializer = messageSerializer;
		this.mqttServer = mqttServer;
	}

	@RStreamListener(
		name = RedisKeys.REDIS_CHANNEL_DOWN_KEY,
		messageModel = MessageModel.BROADCASTING,
		readRawBytes = true
	)
	public void mqttMessageDownReceiver(MapRecord<String, String, byte[]> mapRecord) {
		// 手动序列化和反序列化，避免 redis 序列化不一致问题
		Map<String, byte[]> recordValue = mapRecord.getValue();
		recordValue.forEach((key, messageBody) -> {
			// 手动序列化和反序列化，避免 redis 序列化不一致问题
			Message mqttMessage = messageSerializer.deserialize(messageBody);
			if (mqttMessage == null) {
				return;
			}
			// 下行消息，发送到设备
			String topic = mqttMessage.getTopic();
			if (StringUtil.isBlank(topic)) {
				log.error("Mqtt down stream topic is blank.");
				return;
			}
			String clientId = mqttMessage.getClientId();
			byte[] payload = mqttMessage.getPayload();
			MqttQoS mqttQoS = MqttQoS.valueOf(mqttMessage.getQos());
			boolean retain = mqttMessage.isRetain();
			if (StringUtil.isBlank(clientId)) {
				mqttServer.publishAll(topic, payload, mqttQoS, retain);
			} else {
				mqttServer.publish(clientId, topic, payload, mqttQoS, retain);
			}
		});
	}

}
