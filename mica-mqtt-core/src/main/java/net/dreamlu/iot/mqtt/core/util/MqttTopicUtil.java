package net.dreamlu.iot.mqtt.core.util;

import java.util.regex.Pattern;

/**
 * Mqtt Topic 工具
 *
 * @author L.cm
 */
public final class MqttTopicUtil {

	public static Pattern getTopicPattern(String topic) {
		if (topic.startsWith("$")) {
			topic = "\\" + topic;
		}
		return Pattern.compile(topic
			.replace("+", "[^/]+")
			.replace("#", ".+")
			.concat("$")
		);
	}

}
