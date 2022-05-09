/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & dreamlu.net).
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

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.common.MqttPendingPublish;
import net.dreamlu.iot.mqtt.core.common.MqttPendingQos2Publish;
import net.dreamlu.iot.mqtt.core.util.collection.IntObjectHashMap;
import net.dreamlu.iot.mqtt.core.util.collection.IntObjectMap;
import net.dreamlu.iot.mqtt.core.util.collection.MultiValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 客户端 session 管理，包括 sub 和 pub
 *
 * @author L.cm
 */
public final class DefaultMqttClientSession implements IMqttClientSession {
	private static final Logger logger = LoggerFactory.getLogger(DefaultMqttClientSession.class);
	/**
	 * 订阅的数据承载
	 */
	private final MultiValueMap<String, MqttClientSubscription> subscriptions = new MultiValueMap<>();
	private final IntObjectMap<MqttPendingSubscription> pendingSubscriptions = new IntObjectHashMap<>();
	private final IntObjectMap<MqttPendingUnSubscription> pendingUnSubscriptions = new IntObjectHashMap<>();
	private final IntObjectMap<MqttPendingPublish> pendingPublishData = new IntObjectHashMap<>();
	private final IntObjectMap<MqttPendingQos2Publish> pendingQos2PublishData = new IntObjectHashMap<>();

	@Override
	public void addPaddingSubscribe(int messageId, MqttPendingSubscription pendingSubscription) {
		pendingSubscriptions.put(messageId, pendingSubscription);
	}

	@Override
	public MqttPendingSubscription getPaddingSubscribe(int messageId) {
		return pendingSubscriptions.get(messageId);
	}

	@Override
	public MqttPendingSubscription removePaddingSubscribe(int messageId) {
		return pendingSubscriptions.remove(messageId);
	}

	@Override
	public void addSubscriptionList(List<MqttClientSubscription> subscriptionList) {
		for (MqttClientSubscription subscription : subscriptionList) {
			subscriptions.add(subscription.getTopicFilter(), subscription);
		}
	}

	@Override
	public boolean isSubscribed(MqttClientSubscription clientSubscription) {
		// 1. 判断是否已经存在订阅关系
		String topicFilter = clientSubscription.getTopicFilter();
		Set<MqttClientSubscription> subscriptionSet = this.subscriptions.get(topicFilter);
		if (subscriptionSet == null || subscriptionSet.isEmpty()) {
			return false;
		}
		// 2. 存在时的逻辑
		MqttQoS mqttQoS = clientSubscription.getMqttQoS();
		IMqttClientMessageListener listener = clientSubscription.getListener();
		for (MqttClientSubscription subscription : subscriptionSet) {
			// 3. 已经存在订阅
			if (clientSubscription.equals(subscription)) {
				logger.error("MQTT Topic:{} mqttQoS:{} listener:{} duplicate subscription.", topicFilter, mqttQoS, listener);
				return true;
			}
			MqttQoS subQos = subscription.getMqttQoS();
			IMqttClientMessageListener subListener = subscription.getListener();
			// 4. 如果已经存在更高或同级别 qos
			if (subQos.value() >= mqttQoS.value()) {
				// 5. 监听器不相同则直接添加
				if (subListener != listener) {
					subscriptions.add(topicFilter, clientSubscription);
					logger.warn("MQTT Topic:{} mqttQoS:{} listener:{} has a higher level qos, added directly.", topicFilter, mqttQoS, listener);
				} else {
					logger.error("MQTT Topic:{} mqttQoS:{} listener:{} has a higher level qos, duplicate subscription.", topicFilter, mqttQoS, listener);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public List<MqttClientSubscription> getAndCleanSubscription() {
		List<MqttClientSubscription> subscriptionList = new ArrayList<>();
		for (Set<MqttClientSubscription> mqttSubscriptions : subscriptions.values()) {
			subscriptionList.addAll(mqttSubscriptions);
		}
		List<MqttClientSubscription> data = Collections.unmodifiableList(subscriptionList);
		subscriptions.clear();
		return data;
	}

	@Override
	public List<MqttClientSubscription> getMatchedSubscription(String topicName) {
		List<MqttClientSubscription> subscriptionList = new ArrayList<>();
		for (Set<MqttClientSubscription> mqttSubscriptions : subscriptions.values()) {
			for (MqttClientSubscription subscription : mqttSubscriptions) {
				if (subscription.matches(topicName)) {
					subscriptionList.add(subscription);
				}
			}
		}
		return Collections.unmodifiableList(subscriptionList);
	}

	@Override
	public void removeSubscriptions(List<String> topicFilters) {
		topicFilters.forEach(subscriptions::remove);
	}

	@Override
	public void addPaddingUnSubscribe(int messageId, MqttPendingUnSubscription pendingUnSubscription) {
		pendingUnSubscriptions.put(messageId, pendingUnSubscription);
	}

	@Override
	public MqttPendingUnSubscription getPaddingUnSubscribe(int messageId) {
		return pendingUnSubscriptions.get(messageId);
	}

	@Override
	public MqttPendingUnSubscription removePaddingUnSubscribe(int messageId) {
		return pendingUnSubscriptions.remove(messageId);
	}

	@Override
	public void addPendingPublish(int messageId, MqttPendingPublish pendingPublish) {
		pendingPublishData.put(messageId, pendingPublish);
	}

	@Override
	public MqttPendingPublish getPendingPublish(int messageId) {
		return pendingPublishData.get(messageId);
	}

	@Override
	public MqttPendingPublish removePendingPublish(int messageId) {
		return pendingPublishData.remove(messageId);
	}

	@Override
	public void addPendingQos2Publish(int messageId, MqttPendingQos2Publish pendingQos2Publish) {
		pendingQos2PublishData.put(messageId, pendingQos2Publish);
	}

	@Override
	public MqttPendingQos2Publish getPendingQos2Publish(int messageId) {
		return pendingQos2PublishData.get(messageId);
	}

	@Override
	public MqttPendingQos2Publish removePendingQos2Publish(int messageId) {
		return pendingQos2PublishData.remove(messageId);
	}

	@Override
	public void clean() {
		subscriptions.clear();
		pendingSubscriptions.clear();
		pendingUnSubscriptions.clear();
		pendingPublishData.clear();
		pendingQos2PublishData.clear();
	}
}
