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

import lombok.RequiredArgsConstructor;
import net.dreamlu.iot.mqtt.codec.MqttConnectMessage;
import net.dreamlu.iot.mqtt.codec.MqttMessageBuilders;
import org.tio.client.DefaultClientAioListener;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;

import java.nio.charset.StandardCharsets;

/**
 * mqtt 客户端监听器
 *
 * @author L.cm
 */
@RequiredArgsConstructor
public class MqttClientAioListener extends DefaultClientAioListener {
	private final String clientId;
	private final String username;
	private final String password;

	@Override
	public void onAfterConnected(ChannelContext context, boolean isConnected, boolean isReconnect) {
		if (isConnected) {
			// 1. 建立连接后发送 mqtt 连接的消息
			MqttConnectMessage message = MqttMessageBuilders.connect()
				.clientId(clientId)
				.username(username)
				.password(password.getBytes(StandardCharsets.UTF_8))
				.build();
			Tio.send(context, message);
		}
	}

}
