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

package net.dreamlu.iot.mqtt.broker.config;

import net.dreamlu.iot.mqtt.broker.cluster.RedisMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.broker.cluster.RedisMqttMessageReceiver;
import net.dreamlu.iot.mqtt.broker.cluster.RedisMqttMessageStore;
import net.dreamlu.iot.mqtt.broker.enums.RedisKeys;
import net.dreamlu.iot.mqtt.broker.listener.MqttBrokerConnectListener;
import net.dreamlu.iot.mqtt.broker.listener.MqttBrokerMessageListener;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.core.server.store.IMqttMessageStore;
import net.dreamlu.mica.redis.cache.MicaRedisCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mica mqtt broker 配置
 *
 * @author L.cm
 */
@Configuration(proxyBeanMethods = false)
public class MqttBrokerConfiguration {

	@Bean
	public IMqttConnectStatusListener mqttBrokerConnectListener(MicaRedisCache redisCache) {
		return new MqttBrokerConnectListener(redisCache, RedisKeys.CONNECT_STATUS.getKey());
	}

	@Bean
	public IMqttMessageStore mqttMessageStore(MicaRedisCache redisCache) {
		return new RedisMqttMessageStore(redisCache);
	}

	@Bean
	public RedisMqttMessageReceiver mqttMessageReceiver(MicaRedisCache redisCache,
														MqttServer mqttServer) {
		return new RedisMqttMessageReceiver(redisCache, RedisKeys.REDIS_CHANNEL.getKey(), mqttServer);
	}

	@Bean
	public IMqttMessageDispatcher mqttMessageDispatcher(MicaRedisCache redisCache) {
		return new RedisMqttMessageDispatcher(redisCache, RedisKeys.REDIS_CHANNEL.getKey());
	}

	@Bean
	public MqttBrokerMessageListener brokerMessageListener(IMqttMessageDispatcher mqttMessageDispatcher) {
		return new MqttBrokerMessageListener(mqttMessageDispatcher);
	}

}
