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

package net.dreamlu.iot.mqtt.spring.client;

import net.dreamlu.iot.mqtt.core.client.IMqttClientConnectListener;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import net.dreamlu.iot.mqtt.core.client.MqttClientCreator;
import net.dreamlu.iot.mqtt.core.client.MqttWillMessage;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;

/**
 * mqtt client 配置
 *
 * @author L.cm
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
	prefix = MqttClientProperties.PREFIX,
	name = "enabled",
	havingValue = "true"
)
@EnableConfigurationProperties(MqttClientProperties.class)
public class MqttClientConfiguration {

	@Bean
	public MqttClientCreator mqttClientCreator(MqttClientProperties properties,
											   ObjectProvider<IMqttClientConnectListener> clientConnectListenerObjectProvider,
											   ObjectProvider<MqttClientCustomizer> customizers) {
		MqttClientCreator clientCreator = MqttClient.create()
			.name(properties.getName())
			.ip(properties.getIp())
			.port(properties.getPort())
			.username(properties.getUserName())
			.password(properties.getPassword())
			.clientId(properties.getClientId())
			.readBufferSize(properties.getReadBufferSize())
			.maxBytesInMessage(properties.getMaxBytesInMessage())
			.maxClientIdLength(properties.getMaxClientIdLength())
			.keepAliveSecs(properties.getKeepAliveSecs())
			.reconnect(properties.isReconnect())
			.version(properties.getVersion())
			.cleanSession(properties.isCleanSession())
			.bufferAllocator(properties.getBufferAllocator());
		Integer timeout = properties.getTimeout();
		if (timeout != null && timeout > 0) {
			clientCreator.timeout(timeout);
		}
		Long reInterval1 = properties.getReInterval();
		if (reInterval1 != null && reInterval1 > 0) {
			clientCreator.reInterval(reInterval1);
		}
		// 构造遗嘱消息
		MqttClientProperties.WillMessage willMessage = properties.getWillMessage();
		if (willMessage != null && StringUtils.hasText(willMessage.getTopic())) {
			clientCreator.willMessage(builder -> {
				builder.topic(willMessage.getTopic())
					.qos(willMessage.getQos())
					.retain(willMessage.isRetain());
				if (StringUtils.hasText(willMessage.getMessage())) {
					builder.message(willMessage.getMessage().getBytes(StandardCharsets.UTF_8));
				}
			});
		}
		// 配置客户端链接监听器
		clientConnectListenerObjectProvider.ifAvailable(clientCreator::connectListener);
		// 自定义处理
		customizers.ifAvailable((customizer) -> customizer.customize(clientCreator));
		return clientCreator;
	}

	@Bean
	public MqttClientTemplate mqttClientTemplate(MqttClientCreator mqttClientCreator) {
		return new MqttClientTemplate(mqttClientCreator);
	}

	@Bean
	public MqttClientSubscribeDetector mqttClientSubscribeDetector(MqttClientTemplate clientTemplate) {
		return new MqttClientSubscribeDetector(clientTemplate);
	}

}
