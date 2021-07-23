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
