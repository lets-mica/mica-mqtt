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
	private final Map<Integer, MqttPendingSubscription> pendingSubscriptions = new LinkedHashMap<>();
	private final Map<Integer, MqttPendingUnSubscription> pendingUnSubscriptions = new LinkedHashMap<>();
	private final Map<Integer, MqttPendingPublish> pendingPublishData = new LinkedHashMap<>();
	private final Map<Integer, MqttPendingQos2Publish> pendingQos2PublishData = new LinkedHashMap<>();

	void addPaddingSubscribe(int messageId, MqttPendingSubscription pendingSubscription) {
		pendingSubscriptions.put(messageId, pendingSubscription);
	}

	MqttPendingSubscription getPaddingSubscribe(int messageId) {
		return pendingSubscriptions.get(messageId);
	}

	MqttPendingSubscription removePaddingSubscribe(int messageId) {
		return pendingSubscriptions.remove(messageId);
	}

	void addSubscription(MqttSubscription subscription) {
		subscriptions.add(subscription.getTopicFilter(), subscription);
	}

	List<MqttSubscription> getAndCleanSubscription() {
		List<MqttSubscription> subscriptionList = new ArrayList<>();
		for (List<MqttSubscription> mqttSubscriptions : subscriptions.values()) {
			subscriptionList.addAll(mqttSubscriptions);
		}
		List<MqttSubscription> data = Collections.unmodifiableList(subscriptionList);
		subscriptions.clear();
		return data;
	}

	List<MqttSubscription> getMatchedSubscription(String topicName) {
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

	void removeSubscriptions(String topicFilter) {
		subscriptions.remove(topicFilter);
	}

	void addPaddingUnSubscribe(int messageId, MqttPendingUnSubscription pendingUnSubscription) {
		pendingUnSubscriptions.put(messageId, pendingUnSubscription);
	}

	MqttPendingUnSubscription getPaddingUnSubscribe(int messageId) {
		return pendingUnSubscriptions.get(messageId);
	}

	MqttPendingUnSubscription removePaddingUnSubscribe(int messageId) {
		return pendingUnSubscriptions.remove(messageId);
	}

	void addPendingPublish(int messageId, MqttPendingPublish pendingPublish) {
		pendingPublishData.put(messageId, pendingPublish);
	}

	MqttPendingPublish getPendingPublish(int messageId) {
		return pendingPublishData.get(messageId);
	}

	MqttPendingPublish removePendingPublish(int messageId) {
		return pendingPublishData.remove(messageId);
	}

	void addPendingQos2Publish(int messageId, MqttPendingQos2Publish pendingQos2Publish) {
		pendingQos2PublishData.put(messageId, pendingQos2Publish);
	}

	MqttPendingQos2Publish getPendingQos2Publish(int messageId) {
		return pendingQos2PublishData.get(messageId);
	}

	MqttPendingQos2Publish removePendingQos2Publish(int messageId) {
		return pendingQos2PublishData.remove(messageId);
	}

}
