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

import net.dreamlu.iot.mqtt.core.server.*;
import net.dreamlu.iot.mqtt.core.server.dispatcher.AbstractMqttMessageDispatcher;
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
import net.dreamlu.iot.mqtt.core.server.support.DefaultMqttServerProcessor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tio.core.ssl.SslConfig;
import org.tio.core.stat.IpStatListener;
import org.tio.server.ServerTioConfig;
import org.tio.server.TioServer;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.thread.pool.DefaultThreadFactory;

import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;

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
			.maxClientIdLength(properties.getMaxClientIdLength());
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
		IMqttMessageListener messageListener = messageListenerObjectProvider.getIfAvailable();
		// 消息监听器不能为 null
		Objects.requireNonNull(messageListener, "Mqtt server IMqttMessageListener Bean not found.");
		serverCreator.messageListener(messageListener);

		IMqttServerAuthHandler authHandler = authHandlerObjectProvider.getIfAvailable(DefaultMqttServerAuthHandler::new);
		serverCreator.authHandler(authHandler);

		IMqttMessageDispatcher messageDispatcher = messageDispatcherObjectProvider.getIfAvailable(DefaultMqttMessageDispatcher::new);
		serverCreator.messageDispatcher(messageDispatcher);

		IMqttMessageStore messageStore = messageStoreObjectProvider.getIfAvailable(InMemoryMqttMessageStore::new);
		serverCreator.messageStore(messageStore);

		IMqttSessionManager sessionManager = sessionManagerObjectProvider.getIfAvailable(InMemoryMqttSessionManager::new);
		serverCreator.sessionManager(sessionManager);

		IMqttConnectStatusListener connectStatusListener = connectStatusListenerObjectProvider.getIfAvailable(DefaultMqttConnectStatusListener::new);
		serverCreator.connectStatusListener(connectStatusListener);

		IpStatListener ipStatListener = ipStatListenerObjectProvider.getIfAvailable();
		serverCreator.ipStatListener(ipStatListener);
		// 自定义处理
		customizers.ifAvailable((customizer) -> customizer.customize(serverCreator));
		return serverCreator;
	}

	@Bean
	public MqttServer mqttServer(MqttServerCreator mqttServerCreator) {
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, DefaultThreadFactory.getInstance("MqttServer"));
		DefaultMqttServerProcessor serverProcessor = new DefaultMqttServerProcessor(mqttServerCreator, executor);
		// 1. 处理消息
		ServerAioHandler handler = new MqttServerAioHandler(mqttServerCreator, serverProcessor);
		// 2. t-io 监听
		ServerAioListener listener = new MqttServerAioListener(mqttServerCreator);
		// 2. t-io 配置
		ServerTioConfig tioConfig = new ServerTioConfig(mqttServerCreator.getName(), handler, listener);
		// 4. 设置 t-io 心跳 timeout
		Long heartbeatTimeout = mqttServerCreator.getHeartbeatTimeout();
		if (heartbeatTimeout != null && heartbeatTimeout > 0) {
			tioConfig.setHeartbeatTimeout(heartbeatTimeout);
		}
		IpStatListener ipStatListener = mqttServerCreator.getIpStatListener();
		if (ipStatListener != null) {
			tioConfig.setIpStatListener(ipStatListener);
		}
		SslConfig sslConfig = mqttServerCreator.getSslConfig();
		if (sslConfig != null) {
			tioConfig.setSslConfig(sslConfig);
		}
		if (mqttServerCreator.isDebug()) {
			tioConfig.debug = true;
		}
		// 5. mqtt 消息最大长度
		tioConfig.setReadBufferSize(mqttServerCreator.getReadBufferSize());
		TioServer tioServer = new TioServer(tioConfig);
		// 6. 不校验版本号，社区版设置无效
		tioServer.setCheckLastVersion(false);
		MqttServer mqttServer = new MqttServer(tioServer, mqttServerCreator, executor);
		IMqttMessageDispatcher messageDispatcher = mqttServerCreator.getMessageDispatcher();
		// 7. 如果是默认的消息转发器，设置 mqttServer
		if (messageDispatcher instanceof AbstractMqttMessageDispatcher) {
			((AbstractMqttMessageDispatcher) messageDispatcher).config(mqttServer);
		}
		return mqttServer;
	}

	@Bean
	public MqttServerLauncher mqttServerLauncher(MqttServerCreator serverCreator,
												 MqttServer mqttServer) {
		return new MqttServerLauncher(serverCreator, mqttServer);
	}

	@Bean
	public MqttServerTemplate mqttServerTemplate(MqttServer mqttServer) {
		return new MqttServerTemplate(mqttServer);
	}

}
