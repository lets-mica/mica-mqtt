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

package net.dreamlu.iot.mqtt.core.server.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 订阅模型，用于存储
 *
 * @author L.cm
 */
public class Subscribe implements Serializable {
	private Pattern topicRegex;
	private int mqttQoS;

	public Subscribe() {
	}

	public Subscribe(String topicFilter, int mqttQoS) {
		this.topicRegex = Pattern.compile(topicFilter.replace("+", "[^/]+").replace("#", ".+").concat("$"));
		this.mqttQoS = mqttQoS;
	}

	public Pattern getTopicRegex() {
		return topicRegex;
	}

	public Subscribe setTopicRegex(Pattern topicRegex) {
		this.topicRegex = topicRegex;
		return this;
	}

	public int getMqttQoS() {
		return mqttQoS;
	}

	public Subscribe setMqttQoS(int mqttQoS) {
		this.mqttQoS = mqttQoS;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Subscribe that = (Subscribe) o;
		return mqttQoS == that.mqttQoS &&
			Objects.equals(topicRegex, that.topicRegex);
	}

	@Override
	public int hashCode() {
		return Objects.hash(topicRegex, mqttQoS);
	}

	@Override
	public String toString() {
		return "SubscribeStore{" +
			"topicRegex=" + topicRegex +
			", mqttQoS=" + mqttQoS +
			'}';
	}
}
