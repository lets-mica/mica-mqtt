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

package net.dreamlu.iot.mqtt.spring.server;

import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.MqttServerCreator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * mqtt server 配置
 *
 * @author L.cm
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MqttServerProperties.class)
public class MqttServerConfiguration {

	@Bean
	public MqttServerCreator mqttServerCreator(MqttServerProperties properties,
											   ObjectProvider<MqttServerCustomizer> customizers) {
		MqttServerCreator serverCreator = MqttServer.create()
			.name(properties.getName())
			.ip(properties.getIp())
			.port(properties.getPort())
			.heartbeatTimeout(properties.getHeartbeatTimeout())
			.readBufferSize(properties.getReadBufferSize())
			.maxBytesInMessage(properties.getMaxBytesInMessage())
			.bufferAllocator(properties.getBufferAllocator());
		if (properties.isDebug()) {
			serverCreator.debug();
		}
		// 自定义处理
		customizers.ifAvailable((customizer) -> {
			customizer.customize(serverCreator);
		});
		return serverCreator;
	}

	@Bean
	public MqttServerLauncher mqttServerLauncher(MqttServerCreator serverCreator) {
		return new MqttServerLauncher(serverCreator);
	}

}
