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

package net.dreamlu.iot.mqtt.core.server.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * 订阅模型，用于存储
 *
 * @author L.cm
 */
public class Subscribe implements Serializable {
	private String topicFilter;
	private String clientId;
	private int mqttQoS;

	public Subscribe() {
	}

	public Subscribe(String topicFilter, int mqttQoS) {
		this.topicFilter = topicFilter;
		this.mqttQoS = mqttQoS;
	}

	public Subscribe(String topicFilter, String clientId, int mqttQoS) {
		this.topicFilter = topicFilter;
		this.clientId = clientId;
		this.mqttQoS = mqttQoS;
	}

	public String getTopicFilter() {
		return topicFilter;
	}

	public void setTopicFilter(String topicFilter) {
		this.topicFilter = topicFilter;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getMqttQoS() {
		return mqttQoS;
	}

	public void setMqttQoS(int mqttQoS) {
		this.mqttQoS = mqttQoS;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Subscribe subscribe = (Subscribe) o;
		return mqttQoS == subscribe.mqttQoS &&
			Objects.equals(topicFilter, subscribe.topicFilter) &&
			Objects.equals(clientId, subscribe.clientId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(topicFilter, clientId, mqttQoS);
	}

	@Override
	public String toString() {
		return "Subscribe{" +
			"topicFilter='" + topicFilter + '\'' +
			", clientId='" + clientId + '\'' +
			", mqttQoS=" + mqttQoS +
			'}';
	}
}
