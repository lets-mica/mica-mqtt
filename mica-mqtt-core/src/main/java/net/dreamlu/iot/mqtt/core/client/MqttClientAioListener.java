/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.net.dreamlu.net).
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
	private final MqttClientCreator clientConfig;
	private final MqttWillMessage willMessage;

	public MqttClientAioListener(MqttClientCreator clientConfig) {
		this.clientConfig = Objects.requireNonNull(clientConfig);
		this.willMessage = clientConfig.getWillMessage();
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
				.protocolVersion(clientConfig.getProtocolVersion())
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
					.willQoS(willMessage.getQos());
			}
			// 4. 发送链接请求
			Tio.send(context, builder.build());
		}
	}

}
