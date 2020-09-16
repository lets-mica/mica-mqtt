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

import java.util.Objects;

/**
 * 遗嘱消息
 *
 * @author L.cm
 */
public final class MqttWillMessage {
	private final String topic;
	private final String message;
	private final boolean retain;
	private final MqttQoS qos;

	private MqttWillMessage(String topic, String message, boolean retain, MqttQoS qos) {
		this.topic = topic;
		this.message = message;
		this.retain = retain;
		this.qos = qos;
	}

	public String getTopic() {
		return topic;
	}

	public String getMessage() {
		return message;
	}

	public boolean isRetain() {
		return retain;
	}

	public MqttQoS getQos() {
		return qos;
	}

	public static MqttWillMessage.Builder builder() {
		return new MqttWillMessage.Builder();
	}

	public static final class Builder {
		private String topic;
		private String message;
		private boolean retain;
		private MqttQoS qos;

		public Builder topic(String topic) {
			this.topic = Objects.requireNonNull(topic);
			return this;
		}

		public Builder message(String message) {
			this.message = Objects.requireNonNull(message);
			return this;
		}

		public Builder retain(boolean retain) {
			this.retain = retain;
			return this;
		}

		public Builder qos(MqttQoS qos) {
			this.qos = Objects.requireNonNull(qos);
			return this;
		}

		public MqttWillMessage build() {
			return new MqttWillMessage(topic, message, retain, qos);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MqttWillMessage that = (MqttWillMessage) o;
		return retain == that.retain &&
			Objects.equals(topic, that.topic) &&
			Objects.equals(message, that.message) &&
			qos == that.qos;
	}

	@Override
	public int hashCode() {
		return Objects.hash(topic, message, retain, qos);
	}

	@Override
	public String toString() {
		return "MqttWillMessage{" +
			"topic='" + topic + '\'' +
			", message='" + message + '\'' +
			", retain=" + retain +
			", qos=" + qos +
			'}';
	}
}
