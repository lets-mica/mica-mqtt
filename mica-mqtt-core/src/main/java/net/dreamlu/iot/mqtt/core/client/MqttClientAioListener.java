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

package net.dreamlu.iot.mqtt.core.client;

import net.dreamlu.iot.mqtt.codec.MqttConnectMessage;
import net.dreamlu.iot.mqtt.codec.MqttMessageBuilders;
import net.dreamlu.iot.mqtt.codec.MqttProperties;
import net.dreamlu.iot.mqtt.codec.MqttVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.DefaultClientAioListener;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.utils.hutool.StrUtil;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * mqtt 客户端监听器
 *
 * @author L.cm
 */
public class MqttClientAioListener extends DefaultClientAioListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttClient.class);
	private final MqttConnectMessage connectMessage;
	private final IMqttClientConnectListener connectListener;

	public MqttClientAioListener(MqttClientCreator clientCreator) {
		this.connectMessage = getConnectMessage(Objects.requireNonNull(clientCreator));
		this.connectListener = clientCreator.getConnectListener();
	}

	/**
	 * 构造连接消息
	 *
	 * @param mqttClientCreator MqttClientCreator
	 * @return MqttConnectMessage
	 */
	private static MqttConnectMessage getConnectMessage(MqttClientCreator mqttClientCreator) {
		MqttWillMessage willMessage = mqttClientCreator.getWillMessage();
		MqttVersion version = mqttClientCreator.getVersion();
		// 1. 建立连接后发送 mqtt 连接的消息
		MqttMessageBuilders.ConnectBuilder builder = MqttMessageBuilders.connect()
			.clientId(mqttClientCreator.getClientId())
			.username(mqttClientCreator.getUsername())
			.keepAlive(mqttClientCreator.getKeepAliveSecs())
			.cleanSession(mqttClientCreator.isCleanSession())
			.protocolVersion(version)
			.willFlag(willMessage != null);
		// 2. 密码
		String password = mqttClientCreator.getPassword();
		if (StrUtil.isNotBlank(password)) {
			builder.password(password.getBytes(StandardCharsets.UTF_8));
		}
		// 3. 遗嘱消息
		if (willMessage != null) {
			builder.willTopic(willMessage.getTopic())
				.willMessage(willMessage.getMessage())
				.willRetain(willMessage.isRetain())
				.willQoS(willMessage.getQos())
				.willProperties(willMessage.getWillProperties());
		}
		// 4. mqtt5 特性
		if (MqttVersion.MQTT_5 == version) {
			MqttProperties properties = mqttClientCreator.getProperties();
			// Session Expiry Interval
			Integer sessionExpiryInterval = mqttClientCreator.getSessionExpiryIntervalSecs();
			if (sessionExpiryInterval != null && sessionExpiryInterval > 0) {
				if (properties == null) {
					properties = new MqttProperties();
				}
				properties.add(new MqttProperties.IntegerProperty(MqttProperties.MqttPropertyType.SESSION_EXPIRY_INTERVAL, sessionExpiryInterval));
			}
			if (properties != null) {
				builder.properties(properties);
			}
		}
		return builder.build();
	}

	@Override
	public void onAfterConnected(ChannelContext context, boolean isConnected, boolean isReconnect) {
		if (isConnected) {
			// 重连时，发送 mqtt 连接消息
			Boolean result = Tio.send(context, this.connectMessage);
			logger.info("MqttClient reconnect send connect result:{}", result);
		}
	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
		// 先判断是否配置监听
		if (connectListener == null) {
			return;
		}
		try {
			connectListener.onDisconnect(channelContext, throwable, remark, isRemove);
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}

}
