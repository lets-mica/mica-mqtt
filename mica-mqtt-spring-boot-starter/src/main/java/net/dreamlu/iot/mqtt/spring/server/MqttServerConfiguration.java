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

import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerAuthHandler;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.MqttServerCreator;
import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerSubscribeValidator;
import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import net.dreamlu.iot.mqtt.core.server.session.InMemoryMqttSessionManager;
import net.dreamlu.iot.mqtt.core.server.store.IMqttMessageStore;
import net.dreamlu.iot.mqtt.core.server.store.InMemoryMqttMessageStore;
import net.dreamlu.iot.mqtt.core.server.support.DefaultMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.core.server.support.DefaultMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.support.DefaultMqttServerAuthHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tio.core.stat.IpStatListener;
import org.tio.utils.hutool.StrUtil;

import java.util.Objects;

/**
 * mqtt server 配置
 *
 * @author L.cm
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(
	prefix = MqttServerProperties.PREFIX,
	name = "enabled",
	havingValue = "true",
	matchIfMissing = true
)
@EnableConfigurationProperties(MqttServerProperties.class)
public class MqttServerConfiguration {

	@Bean
	public MqttServerCreator mqttServerCreator(MqttServerProperties properties,
											   ObjectProvider<IMqttServerAuthHandler> authHandlerObjectProvider,
											   ObjectProvider<IMqttServerSubscribeValidator> subscribeValidatorObjectProvider,
											   ObjectProvider<IMqttMessageDispatcher> messageDispatcherObjectProvider,
											   ObjectProvider<IMqttMessageStore> messageStoreObjectProvider,
											   ObjectProvider<IMqttSessionManager> sessionManagerObjectProvider,
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
			.bufferAllocator(properties.getBufferAllocator())
			.maxClientIdLength(properties.getMaxClientIdLength())
			.webPort(properties.getWebPort())
			.websocketEnable(properties.isWebsocketEnable())
			.httpEnable(properties.isHttpEnable());
		if (properties.isDebug()) {
			serverCreator.debug();
		}
		MqttServerProperties.HttpBasicAuth httpBasicAuth = properties.getHttpBasicAuth();
		if (serverCreator.isHttpEnable() && httpBasicAuth.isEnable()) {
			serverCreator.httpBasicAuth(httpBasicAuth.getUsername(), httpBasicAuth.getPassword());
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
		IMqttMessageListener messageListener = messageListenerObjectProvider.getIfAvailable();
		// 消息监听器不能为 null
		Objects.requireNonNull(messageListener, "Mqtt server IMqttMessageListener Bean not found.");
		serverCreator.messageListener(messageListener);
		// 认证处理器
		IMqttServerAuthHandler authHandler = authHandlerObjectProvider.getIfAvailable(DefaultMqttServerAuthHandler::new);
		serverCreator.authHandler(authHandler);
		// 订阅校验
		subscribeValidatorObjectProvider.ifAvailable(serverCreator::subscribeValidator);
		// 消息转发
		IMqttMessageDispatcher messageDispatcher = messageDispatcherObjectProvider.getIfAvailable(DefaultMqttMessageDispatcher::new);
		serverCreator.messageDispatcher(messageDispatcher);
		// 消息存储
		IMqttMessageStore messageStore = messageStoreObjectProvider.getIfAvailable(InMemoryMqttMessageStore::new);
		serverCreator.messageStore(messageStore);
		// session 管理
		IMqttSessionManager sessionManager = sessionManagerObjectProvider.getIfAvailable(InMemoryMqttSessionManager::new);
		serverCreator.sessionManager(sessionManager);
		// 状态监听
		IMqttConnectStatusListener connectStatusListener = connectStatusListenerObjectProvider.getIfAvailable(DefaultMqttConnectStatusListener::new);
		serverCreator.connectStatusListener(connectStatusListener);
		// ip 状态监听
		IpStatListener ipStatListener = ipStatListenerObjectProvider.getIfAvailable();
		serverCreator.ipStatListener(ipStatListener);
		// 自定义处理
		customizers.ifAvailable((customizer) -> customizer.customize(serverCreator));
		return serverCreator;
	}

	@Bean
	public MqttServer mqttServer(MqttServerCreator mqttServerCreator) {
		return mqttServerCreator.build();
	}

	@Bean
	public MqttServerLauncher mqttServerLauncher(MqttServer mqttServer) {
		return new MqttServerLauncher(mqttServer);
	}

	@Bean
	public MqttServerTemplate mqttServerTemplate(MqttServer mqttServer) {
		return new MqttServerTemplate(mqttServer);
	}

}
