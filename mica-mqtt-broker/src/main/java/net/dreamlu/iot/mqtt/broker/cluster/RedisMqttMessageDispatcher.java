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
import net.dreamlu.iot.mqtt.core.server.serializer.IMessageSerializer;
import net.dreamlu.mica.redis.cache.MicaRedisCache;

import java.util.Objects;

/**
 * redis 消息转发器
 *
 * @author L.cm
 */
public class RedisMqttMessageDispatcher implements IMqttMessageDispatcher {
	private final MicaRedisCache redisCache;
	private final IMessageSerializer messageSerializer;
	private final String channel;

	public RedisMqttMessageDispatcher(MicaRedisCache redisCache,
									  IMessageSerializer messageSerializer,
									  String channel) {
		this.redisCache = redisCache;
		this.messageSerializer = messageSerializer;
		this.channel = Objects.requireNonNull(channel, "Redis pub/sub channel is null.");
	}

	@Override
	public boolean send(Message message) {
		// 手动序列化和反序列化，避免 redis 序列化不一致问题
		redisCache.publish(channel, message, messageSerializer::serialize);
		return true;
	}

}
