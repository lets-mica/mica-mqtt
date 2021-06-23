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

import net.dreamlu.iot.mqtt.core.util.MultiValueMap;

import java.util.*;

/**
 * 客户端管理处理，包括 sub 和 pub
 *
 * @author L.cm
 */
final class MqttClientSubscriptionManager {

	/**
	 * 订阅的数据承载
	 */
	private final MultiValueMap<String, MqttSubscription> subscriptions = new MultiValueMap<>();
	private final Map<Integer, MqttSubscription> pendingSubscriptions = new LinkedHashMap<>();
	private final Map<Integer, String> pendingUnSubscriptions = new LinkedHashMap<>();

	public void addPaddingSubscribe(MqttSubscription pendingSubscription) {
		pendingSubscriptions.put(pendingSubscription.getMessageId(), pendingSubscription);
	}

	public MqttSubscription getPaddingSubscribe(int messageId) {
		return pendingSubscriptions.remove(messageId);
	}

	public void addSubscription(MqttSubscription subscription) {
		subscriptions.add(subscription.getTopicFilter(), subscription);
	}

	public List<MqttSubscription> getMatchedSubscription(String topicName) {
		List<MqttSubscription> subscriptionList = new ArrayList<>();
		for (List<MqttSubscription> mqttSubscriptions : subscriptions.values()) {
			for (MqttSubscription subscription : mqttSubscriptions) {
				if (subscription.matches(topicName)) {
					subscriptionList.add(subscription);
				}
			}
		}
		return Collections.unmodifiableList(subscriptionList);
	}

	public void removeSubscriptions(String topicFilter) {
		subscriptions.remove(topicFilter);
	}

	public void addPaddingUnSubscribe(int messageId, String topicFilter) {
		pendingUnSubscriptions.put(messageId, topicFilter);
	}

	public String getPaddingUnSubscribe(int messageId) {
		return pendingUnSubscriptions.remove(messageId);
	}

}
