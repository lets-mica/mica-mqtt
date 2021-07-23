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
import net.dreamlu.iot.mqtt.core.util.MqttTopicUtil;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 发送订阅，未 ack 前的数据承载
 *
 * @author L.cm
 */
final class MqttClientSubscription implements Serializable {
	private final String topicFilter;
	private final MqttQoS mqttQoS;
	private final Pattern topicRegex;
	private final IMqttClientMessageListener listener;

	public MqttClientSubscription(MqttQoS mqttQoS,
								  String topicFilter,
								  IMqttClientMessageListener listener) {
		this.mqttQoS = mqttQoS;
		this.topicFilter = topicFilter;
		this.topicRegex = MqttTopicUtil.getTopicPattern(topicFilter);
		this.listener = listener;
	}

	public MqttQoS getMqttQoS() {
		return mqttQoS;
	}

	public String getTopicFilter() {
		return topicFilter;
	}

	public IMqttClientMessageListener getListener() {
		return listener;
	}

	public boolean matches(String topic) {
		return this.topicRegex.matcher(topic).matches();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MqttClientSubscription that = (MqttClientSubscription) o;
		return Objects.equals(topicFilter, that.topicFilter) &&
			mqttQoS == that.mqttQoS &&
			Objects.equals(topicRegex, that.topicRegex) &&
			Objects.equals(listener, that.listener);
	}

	@Override
	public int hashCode() {
		return Objects.hash(topicFilter, mqttQoS, topicRegex, listener);
	}

	@Override
	public String toString() {
		return "MqttSubscription{" +
			"topicFilter='" + topicFilter + '\'' +
			", mqttQoS=" + mqttQoS +
			", topicRegex=" + topicRegex +
			", listener=" + listener +
			'}';
	}

}
