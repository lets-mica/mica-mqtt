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

import net.dreamlu.iot.mqtt.core.server.IMqttServerAuthHandler;
import net.dreamlu.iot.mqtt.core.server.IMqttServerSubscribeManager;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.MqttServerCreator;
import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import net.dreamlu.iot.mqtt.core.server.store.IMqttMessageStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tio.core.stat.IpStatListener;
import org.tio.utils.hutool.StrUtil;

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
											   ObjectProvider<IMqttServerAuthHandler> authHandlerObjectProvider,
											   ObjectProvider<IMqttMessageDispatcher> messageDispatcherObjectProvider,
											   ObjectProvider<IMqttMessageStore> messageStoreObjectProvider,
											   ObjectProvider<IMqttSessionManager> sessionManagerObjectProvider,
											   ObjectProvider<IMqttServerSubscribeManager> subscribeManagerObjectProvider,
											   ObjectProvider<IMqttMessageListener> messageListenerObjectProvider,
											   ObjectProvider<IMqttConnectStatusListener> connectStatusListenerObjectProvider,
											   ObjectProvider<IpStatListener> ipStatListenerObjectProvider,
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
		MqttServerProperties.Ssl ssl = properties.getSsl();
		String keyStorePath = ssl.getKeyStorePath();
		String trustStorePath = ssl.getTrustStorePath();
		String password = ssl.getPassword();
		// ssl 配置
		if (StrUtil.isNotBlank(keyStorePath) && StrUtil.isNotBlank(trustStorePath) && StrUtil.isNotBlank(password)) {
			serverCreator.useSsl(keyStorePath, trustStorePath, password);
		}
		// bean 初始化
		authHandlerObjectProvider.ifAvailable(serverCreator::authHandler);
		messageDispatcherObjectProvider.ifAvailable(serverCreator::messageDispatcher);
		messageStoreObjectProvider.ifAvailable(serverCreator::messageStore);
		sessionManagerObjectProvider.ifAvailable(serverCreator::sessionManager);
		subscribeManagerObjectProvider.ifAvailable(serverCreator::subscribeManager);
		messageListenerObjectProvider.ifAvailable(serverCreator::messageListener);
		connectStatusListenerObjectProvider.ifAvailable(serverCreator::connectStatusListener);
		ipStatListenerObjectProvider.ifAvailable(serverCreator::ipStatListener);
		// 自定义处理
		customizers.ifAvailable((customizer) -> customizer.customize(serverCreator));
		return serverCreator;
	}

	@Bean
	public MqttServerLauncher mqttServerLauncher(MqttServerCreator serverCreator) {
		return new MqttServerLauncher(serverCreator);
	}

}
