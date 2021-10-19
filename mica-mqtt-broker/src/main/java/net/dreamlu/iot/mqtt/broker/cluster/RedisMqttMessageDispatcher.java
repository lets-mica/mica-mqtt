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

import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.mica.core.utils.JsonUtil;
import net.dreamlu.mica.redis.cache.MicaRedisCache;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.util.Objects;

/**
 * redis 消息转发器
 *
 * @author L.cm
 */
public class RedisMqttMessageDispatcher implements IMqttMessageDispatcher {
	private final RedisTemplate<String, Object> redisTemplate;
	private final byte[] channelBytes;

	public RedisMqttMessageDispatcher(MicaRedisCache redisCache, String channel) {
		this.redisTemplate = redisCache.getRedisTemplate();
		this.channelBytes = RedisSerializer.string().serialize(Objects.requireNonNull(channel, "Redis pub/sub channel is null."));
	}

	@Override
	public boolean send(Message message) {
		// 手动序列化和反序列化，避免 redis 序列化不一致问题
		final byte[] messageBytes = JsonUtil.toJsonAsBytes(message);
		redisTemplate.execute((RedisCallback<Long>) connection -> connection.publish(channelBytes, messageBytes));
		return true;
	}

	@Override
	public boolean send(String clientId, Message message) {
		message.setToClientId(clientId);
		return send(message);
	}
}
