package net.dreamlu.iot.mqtt.server;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.store.IMqttSubscribeStore;
import net.dreamlu.iot.mqtt.core.server.store.SubscribeStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MqttSubscribeStore implements IMqttSubscribeStore {
	private final ConcurrentMap<String, ConcurrentMap<String, SubscribeStore>> data = new ConcurrentHashMap<>();

	@Override
	public void add(String clientId, String topicFilter, MqttQoS mqttQoS) {
		ConcurrentMap<String, SubscribeStore> map = data.get(clientId);
		if (map == null) {
			map = new ConcurrentHashMap<>();
		}
		map.put(topicFilter, new SubscribeStore(topicFilter, mqttQoS.value()));
		data.put(clientId, map);
	}

	@Override
	public void remove(String clientId, String topicFilter) {
		ConcurrentMap<String, SubscribeStore> map = data.get(clientId);
		if (map == null) {
			return;
		}
		map.remove(topicFilter);
	}

	@Override
	public List<SubscribeStore> search(String clientId, String topicName) {
		List<SubscribeStore> list = new ArrayList<>();
		ConcurrentMap<String, SubscribeStore> map = data.get(clientId);
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

}
