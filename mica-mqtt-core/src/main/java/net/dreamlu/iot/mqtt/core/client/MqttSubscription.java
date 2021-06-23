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

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.common.MqttMessageListener;

import java.util.regex.Pattern;

/**
 * 发送订阅，未 ack 前的数据承载
 *
 * @author L.cm
 */
final class MqttSubscription {
	private final int messageId;
	private final MqttQoS mqttQoS;
	private final String topicFilter;
	private final Pattern topicRegex;
	private final MqttMessageListener listener;

	public MqttSubscription(int messageId,
							MqttQoS mqttQoS,
							String topicFilter,
							MqttMessageListener listener) {
		this.messageId = messageId;
		this.mqttQoS = mqttQoS;
		this.topicFilter = topicFilter;
		this.topicRegex = Pattern.compile(topicFilter.replace("+", "[^/]+").replace("#", ".+").concat("$"));
		this.listener = listener;
	}

	public int getMessageId() {
		return messageId;
	}

	public MqttQoS getMqttQoS() {
		return mqttQoS;
	}

	public String getTopicFilter() {
		return topicFilter;
	}

	public MqttMessageListener getListener() {
		return listener;
	}

	boolean matches(String topic){
		return this.topicRegex.matcher(topic).matches();
	}

	@Override
	public String toString() {
		return "MqttPendingSubscription{" +
			"messageId=" + messageId +
			", mqttQoS=" + mqttQoS +
			", topicFilter='" + topicFilter + '\'' +
			", listener=" + listener +
			'}';
	}

}
