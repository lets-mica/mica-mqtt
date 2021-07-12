package net.dreamlu.iot.mqtt.core.util;

/**
 * Mqtt Topic 工具
 *
 * @author L.cm
 */
public final class MqttTopicUtil {

    public static String regexTopic(String topic) {
        if (topic.startsWith("$")) {
            topic = "\\" + topic;
        }
        return topic
                .replaceAll("/", "\\\\/")
                .replaceAll("\\+", "[^/]+")
                .replaceAll("#", "(.+)") + "$";
    }


}
