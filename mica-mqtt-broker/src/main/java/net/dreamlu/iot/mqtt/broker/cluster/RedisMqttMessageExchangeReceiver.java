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

import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.cluster.MqttClusterMessageListener;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.iot.mqtt.core.server.serializer.IMessageSerializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Objects;

/**
 * 监听集群消息，上行和内部集群通道
 *
 * @author L.cm
 */
public class RedisMqttMessageExchangeReceiver implements MessageListener, InitializingBean {
	private final RedisTemplate<String, Object> redisTemplate;
	private final IMessageSerializer messageSerializer;
	private final String channel;
	private final MqttClusterMessageListener clusterMessageListener;

	public RedisMqttMessageExchangeReceiver(RedisTemplate<String, Object> redisTemplate,
											IMessageSerializer messageSerializer,
											String channel,
											MqttServer mqttServer) {
		this.redisTemplate = redisTemplate;
		this.messageSerializer = messageSerializer;
		this.channel = Objects.requireNonNull(channel, "Redis pub/sub channel is null.");
		this.clusterMessageListener = new MqttClusterMessageListener(mqttServer);
	}

	@Override
	public void onMessage(org.springframework.data.redis.connection.Message message, byte[] bytes) {
		byte[] messageBody = message.getBody();
		// 手动序列化和反序列化，避免 redis 序列化不一致问题
		Message mqttMessage = messageSerializer.deserialize(messageBody);
		if (mqttMessage == null) {
			return;
		}
		clusterMessageListener.onMessage(mqttMessage);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		byte[] channelBytes = RedisSerializer.string().serialize(channel);
		redisTemplate.execute((RedisCallback<Void>) connection -> {
			connection.subscribe(RedisMqttMessageExchangeReceiver.this, channelBytes);
			return null;
		});
	}
}
