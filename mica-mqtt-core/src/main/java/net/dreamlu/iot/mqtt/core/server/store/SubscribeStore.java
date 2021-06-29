package net.dreamlu.iot.mqtt.core.server.store;

import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 订阅存储
 *
 * @author L.cm
 */
public class SubscribeStore implements Serializable {
	private Pattern topicRegex;
	private int mqttQoS;

	public SubscribeStore() {
	}

	public SubscribeStore(String topicFilter, int mqttQoS) {
		this.topicRegex = Pattern.compile(topicFilter.replace("+", "[^/]+").replace("#", ".+").concat("$"));
		this.mqttQoS = mqttQoS;
	}

	public Pattern getTopicRegex() {
		return topicRegex;
	}

	public SubscribeStore setTopicRegex(Pattern topicRegex) {
		this.topicRegex = topicRegex;
		return this;
	}

	public int getMqttQoS() {
		return mqttQoS;
	}

	public SubscribeStore setMqttQoS(int mqttQoS) {
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
		SubscribeStore that = (SubscribeStore) o;
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
