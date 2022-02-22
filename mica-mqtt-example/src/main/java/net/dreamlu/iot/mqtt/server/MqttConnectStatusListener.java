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

package net.dreamlu.iot.mqtt.server;

import net.dreamlu.iot.mqtt.client.MqttClientTest;
import net.dreamlu.iot.mqtt.core.server.MqttConst;
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;

/**
 * mqtt 连接状态
 *
 * @author L.cm
 */
public class MqttConnectStatusListener implements IMqttConnectStatusListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttClientTest.class);

	@Override
	public void online(ChannelContext context, String clientId) {
		String username = (String) context.get(MqttConst.USER_NAME_KEY);
		logger.info("Mqtt clientId:{} username:{} online...", clientId, username);
	}

	@Override
	public void offline(ChannelContext context, String clientId) {
		String username = (String) context.get(MqttConst.USER_NAME_KEY);
		logger.info("Mqtt clientId:{} username:{} offline...", clientId, username);
	}

}
