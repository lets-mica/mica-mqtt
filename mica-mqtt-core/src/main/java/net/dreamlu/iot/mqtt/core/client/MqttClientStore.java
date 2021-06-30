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

import net.dreamlu.iot.mqtt.core.common.MqttPendingPublish;
import net.dreamlu.iot.mqtt.core.common.MqttPendingQos2Publish;
import net.dreamlu.iot.mqtt.core.common.MqttSubscription;
import net.dreamlu.iot.mqtt.core.util.MultiValueMap;

import java.util.*;

/**
 * 客户端管理处理，包括 sub 和 pub
 *
 * @author L.cm
 */
final class MqttClientStore {
	/**
	 * 订阅的数据承载
	 */
	private final MultiValueMap<String, MqttSubscription> subscriptions = new MultiValueMap<>();
	private final Map<Integer, MqttPendingSubscription> pendingSubscriptions = new LinkedHashMap<>();
	private final Map<Integer, MqttPendingUnSubscription> pendingUnSubscriptions = new LinkedHashMap<>();
	private final Map<Integer, MqttPendingPublish> pendingPublishData = new LinkedHashMap<>();
	private final Map<Integer, MqttPendingQos2Publish> pendingQos2PublishData = new LinkedHashMap<>();

	protected void addPaddingSubscribe(int messageId, MqttPendingSubscription pendingSubscription) {
		pendingSubscriptions.put(messageId, pendingSubscription);
	}

	protected MqttPendingSubscription getPaddingSubscribe(int messageId) {
		return pendingSubscriptions.get(messageId);
	}

	protected MqttPendingSubscription removePaddingSubscribe(int messageId) {
		return pendingSubscriptions.remove(messageId);
	}

	protected void addSubscription(MqttSubscription subscription) {
		subscriptions.add(subscription.getTopicFilter(), subscription);
	}

	protected List<MqttSubscription> getAndCleanSubscription() {
		List<MqttSubscription> subscriptionList = new ArrayList<>();
		for (List<MqttSubscription> mqttSubscriptions : subscriptions.values()) {
			subscriptionList.addAll(mqttSubscriptions);
		}
		List<MqttSubscription> data = Collections.unmodifiableList(subscriptionList);
		subscriptions.clear();
		return data;
	}

	protected List<MqttSubscription> getMatchedSubscription(String topicName) {
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

	protected void removeSubscriptions(String topicFilter) {
		subscriptions.remove(topicFilter);
	}

	protected void addPaddingUnSubscribe(int messageId, MqttPendingUnSubscription pendingUnSubscription) {
		pendingUnSubscriptions.put(messageId, pendingUnSubscription);
	}

	protected MqttPendingUnSubscription getPaddingUnSubscribe(int messageId) {
		return pendingUnSubscriptions.get(messageId);
	}

	protected MqttPendingUnSubscription removePaddingUnSubscribe(int messageId) {
		return pendingUnSubscriptions.remove(messageId);
	}

	protected void addPendingPublish(int messageId, MqttPendingPublish pendingPublish) {
		pendingPublishData.put(messageId, pendingPublish);
	}

	protected MqttPendingPublish getPendingPublish(int messageId) {
		return pendingPublishData.get(messageId);
	}

	protected MqttPendingPublish removePendingPublish(int messageId) {
		return pendingPublishData.remove(messageId);
	}

	protected void addPendingQos2Publish(int messageId, MqttPendingQos2Publish pendingQos2Publish) {
		pendingQos2PublishData.put(messageId, pendingQos2Publish);
	}

	protected MqttPendingQos2Publish getPendingQos2Publish(int messageId) {
		return pendingQos2PublishData.get(messageId);
	}

	protected MqttPendingQos2Publish removePendingQos2Publish(int messageId) {
		return pendingQos2PublishData.remove(messageId);
	}

	public void clean() {
		subscriptions.clear();
		pendingSubscriptions.clear();
		pendingUnSubscriptions.clear();
		pendingPublishData.clear();
		pendingQos2PublishData.clear();
	}
}
