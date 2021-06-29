package net.dreamlu.iot.mqtt.server;

import net.dreamlu.iot.mqtt.core.common.MqttPendingPublish;
import net.dreamlu.iot.mqtt.core.common.MqttPendingQos2Publish;
import net.dreamlu.iot.mqtt.core.server.IMqttPublishManager;

import java.util.LinkedHashMap;
import java.util.Map;

public class MqttPublishManager implements IMqttPublishManager {
	private final Map<Integer, MqttPendingPublish> pendingPublishData = new LinkedHashMap<>();
	private final Map<Integer, MqttPendingQos2Publish> pendingQos2PublishData = new LinkedHashMap<>();

	@Override
	public void addPendingPublish(int messageId, MqttPendingPublish pendingPublish) {
		pendingPublishData.put(messageId, pendingPublish);
	}

	@Override
	public MqttPendingPublish getPendingPublish(int messageId) {
		return pendingPublishData.get(messageId);
	}

	@Override
	public void removePendingPublish(int messageId) {
		pendingPublishData.remove(messageId);
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
	public void removePendingQos2Publish(int messageId) {
		pendingQos2PublishData.remove(messageId);
	}
}
