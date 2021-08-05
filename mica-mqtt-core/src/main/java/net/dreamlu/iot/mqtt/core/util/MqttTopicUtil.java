package net.dreamlu.iot.mqtt.core.util;

import java.util.regex.Pattern;

/**
 * Mqtt Topic 工具
 *
 * @author L.cm
 */
public final class MqttTopicUtil {

	/**
	 * mqtt topicFilter 转正则
	 *
	 * @param topicFilter topicFilter
	 * @return Pattern
	 */
	public static Pattern getTopicPattern(String topicFilter) {
		// mqtt 分享主题 $share/{ShareName}/{filter}
		String topicRegex = topicFilter.startsWith("$") ? "\\" + topicFilter : topicFilter;
		return Pattern.compile(topicRegex
			.replace("+", "[^/]+")
			.replace("#", ".+")
			.concat("$")
		);
	}

}
