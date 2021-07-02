package net.dreamlu.iot.mqtt.core.server;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.common.MqttSubscription;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 默认的 mqtt 订阅管理
 *
 * @author L.cm
 */
public class DefaultMqttServerSubManager implements IMqttSubManager {
	private final List<MqttSubscription> subscriptionList = new LinkedList<>();

	@Override
	public void subscribe(MqttSubscription subscription) {
		subscriptionList.add(subscription);
	}

	@Override
	public List<MqttSubscription> getMatchedSubscription(String topicName, MqttQoS mqttQoS) {
		List<MqttSubscription> list = new ArrayList<>();
		for (MqttSubscription subscription : subscriptionList) {
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
