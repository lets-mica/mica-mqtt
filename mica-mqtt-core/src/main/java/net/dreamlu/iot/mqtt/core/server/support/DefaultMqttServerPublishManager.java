package net.dreamlu.iot.mqtt.core.server.support;

import net.dreamlu.iot.mqtt.core.common.MqttPendingPublish;
import net.dreamlu.iot.mqtt.core.common.MqttPendingQos2Publish;
import net.dreamlu.iot.mqtt.core.server.IMqttServerPublishManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 发布过程中的数据存储
 *
 * @author L.cm
 */
public class DefaultMqttServerPublishManager implements IMqttServerPublishManager {
	/**
	 * clientId: {msgId: Object}
 	 */
	private final Map<String, Map<Integer, MqttPendingPublish>> pendingPublishData = new ConcurrentHashMap<>();
	/**
	 * clientId: {msgId: Object}
 	 */
	private final Map<String , Map<Integer, MqttPendingQos2Publish>> pendingQos2PublishData = new ConcurrentHashMap<>();

	@Override
	public void addPendingPublish(String clientId, int messageId, MqttPendingPublish pendingPublish) {
		Map<Integer, MqttPendingPublish> data = pendingPublishData.computeIfAbsent(clientId, (key) -> new ConcurrentHashMap<>(16));
		data.put(messageId, pendingPublish);
	}

	@Override
	public MqttPendingPublish getPendingPublish(String clientId, int messageId) {
		return pendingPublishData.get(clientId).get(messageId);
	}

	@Override
	public void removePendingPublish(String clientId, int messageId) {
		pendingPublishData.get(clientId).remove(messageId);
	}

	@Override
	public void addPendingQos2Publish(String clientId, int messageId, MqttPendingQos2Publish pendingQos2Publish) {
		Map<Integer, MqttPendingQos2Publish> data = pendingQos2PublishData.computeIfAbsent(clientId, (key) -> new ConcurrentHashMap<>());
		data.put(messageId, pendingQos2Publish);
	}

	@Override
	public MqttPendingQos2Publish getPendingQos2Publish(String clientId, int messageId) {
		return pendingQos2PublishData.get(clientId).get(messageId);
	}

	@Override
	public void removePendingQos2Publish(String clientId, int messageId) {
		pendingQos2PublishData.get(clientId).remove(messageId);
	}

}
