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

package net.dreamlu.iot.mqtt.core.server.broker;

import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;

import java.util.Objects;

/**
 * broker 消息监听转发
 *
 * @author L.cm
 */
public class MqttBrokerMessageListener implements IMqttMessageListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttBrokerMessageListener.class);
	private final IMqttMessageDispatcher dispatcher;

	public MqttBrokerMessageListener(IMqttMessageDispatcher dispatcher) {
		this.dispatcher = Objects.requireNonNull(dispatcher, "MqttMessageDispatcher is null.");
	}

	@Override
	public void onMessage(ChannelContext context, String clientId, Message message) {
		logger.debug("Mqtt dispatcher send clientId:{} message:{}", clientId, message);
		dispatcher.send(message);
	}

}
