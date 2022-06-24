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

package net.dreamlu.iot.mqtt.spring.server.config;

import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.MqttServerCreator;
import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerAuthHandler;
import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerPublishPermission;
import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerSubscribeValidator;
import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerUniqueIdService;
import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.event.IMqttSessionListener;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import net.dreamlu.iot.mqtt.core.server.store.IMqttMessageStore;
import net.dreamlu.iot.mqtt.core.server.support.DefaultMqttServerAuthHandler;
import net.dreamlu.iot.mqtt.spring.server.MqttServerCustomizer;
import net.dreamlu.iot.mqtt.spring.server.MqttServerTemplate;
import net.dreamlu.iot.mqtt.spring.server.event.SpringEventMqttConnectStatusListener;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.tio.core.stat.IpStatListener;
import org.tio.utils.hutool.StrUtil;

/**
 * mqtt server 配置
 *
 * @author L.cm
 */
@AutoConfiguration
@ConditionalOnProperty(
	prefix = MqttServerProperties.PREFIX,
	name = "enabled",
	havingValue = "true",
	matchIfMissing = true
)
@EnableConfigurationProperties(MqttServerProperties.class)
public class MqttServerConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public IMqttConnectStatusListener springEventMqttConnectStatusListener(ApplicationEventPublisher eventPublisher) {
		return new SpringEventMqttConnectStatusListener(eventPublisher);
	}

	@Bean
	public MqttServerCreator mqttServerCreator(MqttServerProperties properties,
											   ObjectProvider<IMqttServerAuthHandler> authHandlerObjectProvider,
											   ObjectProvider<IMqttServerUniqueIdService> uniqueIdServiceObjectProvider,
											   ObjectProvider<IMqttServerSubscribeValidator> subscribeValidatorObjectProvider,
											   ObjectProvider<IMqttServerPublishPermission> publishPermissionObjectProvider,
											   ObjectProvider<IMqttMessageDispatcher> messageDispatcherObjectProvider,
											   ObjectProvider<IMqttMessageStore> messageStoreObjectProvider,
											   ObjectProvider<IMqttSessionManager> sessionManagerObjectProvider,
											   ObjectProvider<IMqttSessionListener> sessionListenerObjectProvider,
											   ObjectProvider<IMqttMessageListener> messageListenerObjectProvider,
											   ObjectProvider<IMqttConnectStatusListener> connectStatusListenerObjectProvider,
											   ObjectProvider<IpStatListener> ipStatListenerObjectProvider,
											   ObjectProvider<MqttServerCustomizer> customizers) {
		MqttServerCreator serverCreator = MqttServer.create()
			.name(properties.getName())
			.ip(properties.getIp())
			.port(properties.getPort())
			.heartbeatTimeout(properties.getHeartbeatTimeout())
			.keepaliveBackoff(properties.getKeepaliveBackoff())
			.readBufferSize((int) properties.getReadBufferSize().toBytes())
			.maxBytesInMessage((int) properties.getMaxBytesInMessage().toBytes())
			.bufferAllocator(properties.getBufferAllocator())
			.maxClientIdLength(properties.getMaxClientIdLength())
			.webPort(properties.getWebPort())
			.websocketEnable(properties.isWebsocketEnable())
			.httpEnable(properties.isHttpEnable())
			.nodeName(properties.getNodeName())
			.statEnable(properties.isStatEnable());
		if (properties.isDebug()) {
			serverCreator.debug();
		}

		// http 认证
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
		// 自定义消息监听
		messageListenerObjectProvider.ifAvailable(serverCreator::messageListener);
		// 认证处理器
		MqttServerProperties.MqttAuth mqttAuth = properties.getAuth();
		IMqttServerAuthHandler authHandler = authHandlerObjectProvider.getIfAvailable(() -> new DefaultMqttServerAuthHandler(mqttAuth.isEnable(), mqttAuth.getUsername(), mqttAuth.getPassword()));
		serverCreator.authHandler(authHandler);
		// mqtt 内唯一id
		uniqueIdServiceObjectProvider.ifAvailable(serverCreator::uniqueIdService);
		// 订阅校验
		subscribeValidatorObjectProvider.ifAvailable(serverCreator::subscribeValidator);
		// 订阅权限校验
		publishPermissionObjectProvider.ifAvailable(serverCreator::publishPermission);
		// 消息转发
		messageDispatcherObjectProvider.ifAvailable(serverCreator::messageDispatcher);
		// 消息存储
		messageStoreObjectProvider.ifAvailable(serverCreator::messageStore);
		// session 管理
		sessionManagerObjectProvider.ifAvailable(serverCreator::sessionManager);
		// session 监听
		sessionListenerObjectProvider.ifAvailable(serverCreator::sessionListener);
		// 状态监听
		connectStatusListenerObjectProvider.ifAvailable(serverCreator::connectStatusListener);
		// ip 状态监听
		ipStatListenerObjectProvider.ifAvailable(serverCreator::ipStatListener);
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
