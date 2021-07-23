package net.dreamlu.iot.mqtt.core.server.support;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.IMqttServerSubscribeManager;
import net.dreamlu.iot.mqtt.core.server.MqttServerSubscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 默认的 mqtt 订阅管理
 *
 * @author L.cm
 */
public class DefaultMqttServerSubscribeManager implements IMqttServerSubscribeManager {
	private final List<MqttServerSubscription> subscriptionList = new LinkedList<>();

	@Override
	public void subscribe(MqttServerSubscription subscription) {
		subscriptionList.add(subscription);
	}

	@Override
	public List<MqttServerSubscription> getMatchedSubscription(String topicName, MqttQoS mqttQoS) {
		List<MqttServerSubscription> list = new ArrayList<>();
		for (MqttServerSubscription subscription : subscriptionList) {
			MqttQoS qos = subscription.getMqttQoS();
			if (subscription.matches(topicName) && (qos == null || qos == mqttQoS)) {
				list.add(subscription);
			}
		}
		return Collections.unmodifiableList(list);
	}

	@Override
	public void clean() {
		subscriptionList.clear();
	}

}
