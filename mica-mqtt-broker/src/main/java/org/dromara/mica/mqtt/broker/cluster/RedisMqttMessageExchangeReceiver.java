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

package org.dromara.mica.mqtt.broker.cluster;

import net.dreamlu.mica.redis.stream.MessageModel;
import net.dreamlu.mica.redis.stream.RStreamListener;
import org.dromara.mica.mqtt.broker.enums.RedisKeys;
import org.dromara.mica.mqtt.core.server.MqttServer;
import org.dromara.mica.mqtt.core.server.cluster.MqttClusterMessageListener;
import org.dromara.mica.mqtt.core.server.model.Message;
import org.dromara.mica.mqtt.core.server.serializer.IMessageSerializer;
import org.springframework.data.redis.connection.stream.MapRecord;

import java.util.Map;

/**
 * 监听集群消息，上行和内部集群通道
 *
 * @author L.cm
 */
public class RedisMqttMessageExchangeReceiver {
	private final IMessageSerializer messageSerializer;
	private final MqttClusterMessageListener clusterMessageListener;

	public RedisMqttMessageExchangeReceiver(IMessageSerializer messageSerializer,
											MqttServer mqttServer) {
		this.messageSerializer = messageSerializer;
		this.clusterMessageListener = new MqttClusterMessageListener(mqttServer);
	}

	@RStreamListener(
		name = RedisKeys.REDIS_CHANNEL_EXCHANGE_KEY,
		messageModel = MessageModel.BROADCASTING,
		readRawBytes = true
	)
	public void mqttMessageUpReceiver(MapRecord<String, String, byte[]> mapRecord) {
		// 手动序列化和反序列化，避免 redis 序列化不一致问题
		Map<String, byte[]> recordValue = mapRecord.getValue();
		recordValue.forEach((key, messageBody) -> {
			// 手动序列化和反序列化，避免 redis 序列化不一致问题
			Message mqttMessage = messageSerializer.deserialize(messageBody);
			if (mqttMessage == null) {
				return;
			}
			clusterMessageListener.onMessage(mqttMessage);
		});
	}

}
