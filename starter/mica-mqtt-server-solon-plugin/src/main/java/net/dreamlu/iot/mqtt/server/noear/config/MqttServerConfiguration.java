/* Copyright (c) 2022 Peigen.info. All rights reserved. */

package net.dreamlu.iot.mqtt.server.noear.config;

import net.dreamlu.iot.mqtt.server.noear.event.SolonEventMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.server.noear.event.SolonEventMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.MqttServerCreator;
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Condition;
import org.noear.solon.annotation.Configuration;

/**
 * <b>(MqttServerConfiguration)</b>
 *
 * @author LiHai
 * @version 1.0.0
 * @since 2023/7/20
 */
@Configuration
public class MqttServerConfiguration {

	@Bean
	@Condition(onMissingBean = IMqttConnectStatusListener.class)
	public IMqttConnectStatusListener connectStatusListener() {
		return new SolonEventMqttConnectStatusListener();
	}

	@Bean
	@Condition(onMissingBean = IMqttMessageListener.class)
	public IMqttMessageListener messageListener() {
		return new SolonEventMqttMessageListener();
	}

	@Bean
	public MqttServerCreator mqttServerCreator(MqttServerProperties properties) {
		MqttServerCreator serverCreator = MqttServer.create()
			.name(properties.getName())
			.ip(properties.getIp())
			.port(properties.getPort())
			.heartbeatTimeout(properties.getHeartbeatTimeout())
			.keepaliveBackoff(properties.getKeepaliveBackoff())
			.readBufferSize((int) DataSize.parse(properties.getReadBufferSize()).getBytes())
			.maxBytesInMessage((int) DataSize.parse(properties.getMaxBytesInMessage()).getBytes())
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
		// ssl 配置
		if (ssl.isEnabled()) {
			serverCreator.useSsl(ssl.getKeystorePath(), ssl.getKeystorePass(), ssl.getTruststorePath(), ssl.getTruststorePass(), ssl.getClientAuth());
		}
		return serverCreator;
	}

}
