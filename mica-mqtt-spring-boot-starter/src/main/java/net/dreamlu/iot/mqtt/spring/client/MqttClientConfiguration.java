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

import net.dreamlu.iot.mqtt.core.client.MqttClient;
import net.dreamlu.iot.mqtt.core.client.MqttClientCreator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mqtt client 配置
 *
 * @author L.cm
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MqttClientProperties.class)
public class MqttClientConfiguration {

	@Bean
	public MqttClientCreator mqttClientCreator(MqttClientProperties properties) {
		MqttClientCreator mqttClientCreator = MqttClient.create()
			.name(properties.getName())
			.ip(properties.getIp())
			.port(properties.getPort())
			.username(properties.getUserName())
			.password(properties.getPassword());
		return mqttClientCreator;
	}

	@Bean
	public MqttClientTemplate mqttClientTemplate(MqttClientCreator mqttClientCreator) {
		return new MqttClientTemplate(mqttClientCreator);
	}

}
