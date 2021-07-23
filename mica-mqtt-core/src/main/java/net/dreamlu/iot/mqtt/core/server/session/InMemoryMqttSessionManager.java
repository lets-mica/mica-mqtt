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

package net.dreamlu.iot.mqtt.core.server.session;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.store.SubscribeStore;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 内存 session 管理
 *
 * @author L.cm
 */
public class InMemoryMqttSessionManager implements IMqttSessionManager {
	/**
	 * clientId: messageId
	 */
	private final Map<String, AtomicInteger> messageIdStore = new ConcurrentHashMap<>();
	/**
	 * clientId:{topicFilter: SubscribeStore}
	 */
	private final ConcurrentMap<String, ConcurrentMap<String, SubscribeStore>> subscribeStore = new ConcurrentHashMap<>();

	@Override
	public void addSubscribe(String clientId, String topicFilter, MqttQoS mqttQoS) {
		Map<String, SubscribeStore> data = subscribeStore.computeIfAbsent(clientId, (key) -> new ConcurrentHashMap<>(16));
		data.put(topicFilter, new SubscribeStore(topicFilter, mqttQoS.value()));
	}

	@Override
	public void removeSubscribe(String clientId, String topicFilter) {
		ConcurrentMap<String, SubscribeStore> map = subscribeStore.get(clientId);
		if (map == null) {
			return;
		}
		map.remove(topicFilter);
	}

	@Override
	public List<SubscribeStore> searchSubscribe(String clientId, String topicName) {
		List<SubscribeStore> list = new ArrayList<>();
		ConcurrentMap<String, SubscribeStore> map = subscribeStore.get(clientId);
		if (map == null) {
			return Collections.emptyList();
		}
		Collection<SubscribeStore> values = map.values();
		for (SubscribeStore value : values) {
			if (value.getTopicRegex().matcher(topicName).matches()) {
				list.add(value);
			}
		}
		return list;
	}

	@Override
	public int getMessageId(String clientId) {
		AtomicInteger value = messageIdStore.computeIfAbsent(clientId, (key) -> new AtomicInteger(1));
		value.compareAndSet(0xffff, 1);
		return value.getAndIncrement();
	}

	@Override
	public void clean(String clientId) {
		subscribeStore.remove(clientId);
		messageIdStore.remove(clientId);
	}
}
