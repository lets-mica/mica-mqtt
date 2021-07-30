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

import net.dreamlu.iot.mqtt.codec.MqttMessageBuilders;
import net.dreamlu.iot.mqtt.codec.MqttProperties;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.codec.MqttSubscribeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.DefaultClientAioListener;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.utils.hutool.StrUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * mqtt 客户端监听器
 *
 * @author L.cm
 */
public class MqttClientAioListener extends DefaultClientAioListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttClient.class);
	private final MqttClientCreator clientConfig;
	private final MqttWillMessage willMessage;
	private final MqttClientStore clientStore;
	private final ScheduledThreadPoolExecutor executor;

	public MqttClientAioListener(MqttClientCreator clientConfig,
								 MqttClientStore clientStore,
								 ScheduledThreadPoolExecutor executor) {
		this.clientConfig = Objects.requireNonNull(clientConfig);
		this.willMessage = clientConfig.getWillMessage();
		this.clientStore = clientStore;
		this.executor = executor;
	}

	@Override
	public void onAfterConnected(ChannelContext context, boolean isConnected, boolean isReconnect) {
		if (isConnected) {
			// 1. 建立连接后发送 mqtt 连接的消息
			MqttMessageBuilders.ConnectBuilder builder = MqttMessageBuilders.connect()
				.clientId(clientConfig.getClientId())
				.username(clientConfig.getUsername())
				.keepAlive(clientConfig.getKeepAliveSecs())
				.cleanSession(clientConfig.isCleanSession())
				.protocolVersion(clientConfig.getVersion())
				.willFlag(willMessage != null);
			// 2. 密码
			String password = clientConfig.getPassword();
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
			// 4. mqtt5 properties
			MqttProperties properties = clientConfig.getProperties();
			if (properties != null) {
				builder.properties(properties);
			}
			// 5. 发送 mqtt 连接消息
			Boolean result = Tio.send(context, builder.build());
			logger.info("MqttClient reconnect send connect result:{}", result);
			// 6. 重连时发送重新订阅
			reSendSubscription(context);
		}
	}

	private void reSendSubscription(ChannelContext context) {
		List<MqttClientSubscription> subscriptionList = clientStore.getAndCleanSubscription();
		for (MqttClientSubscription subscription : subscriptionList) {
			int messageId = MqttClientMessageId.getId();
			MqttQoS mqttQoS = subscription.getMqttQoS();
			String topicFilter = subscription.getTopicFilter();
			MqttSubscribeMessage message = MqttMessageBuilders.subscribe()
				.addSubscription(mqttQoS, topicFilter)
				.messageId(messageId)
				.build();
			MqttPendingSubscription pendingSubscription = new MqttPendingSubscription(mqttQoS, topicFilter, subscription.getListener(), message);
			Boolean result = Tio.send(context, message);
			logger.info("MQTT reconnect subscribe topicFilter:{} mqttQoS:{} messageId:{} result:{}", topicFilter, mqttQoS, messageId, result);
			pendingSubscription.startRetransmitTimer(executor, (msg) -> Tio.send(context, message));
			clientStore.addPaddingSubscribe(messageId, pendingSubscription);
		}
	}
}
