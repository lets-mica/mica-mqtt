package net.dreamlu.iot.mqtt.core.client;


import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.codec.MqttSubscribeMessage;
import net.dreamlu.iot.mqtt.core.common.RetryProcessor;
import net.dreamlu.iot.mqtt.core.util.timer.AckService;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * MqttPendingSubscription，参考于 netty-mqtt-client
 */
final class MqttPendingSubscription {
	private final List<MqttClientSubscription> subscriptionList;
	private final RetryProcessor<MqttSubscribeMessage> retryProcessor = new RetryProcessor<>();

	MqttPendingSubscription(List<MqttClientSubscription> subscriptionList, MqttSubscribeMessage message) {
		this.subscriptionList = subscriptionList;
		this.retryProcessor.setOriginalMessage(message);
	}

	public List<MqttClientSubscription> getSubscriptionList() {
		return subscriptionList;
	}

	protected void startRetransmitTimer(AckService ackService, Consumer<MqttMessage> sendPacket) {
		this.retryProcessor.setHandle((fixedHeader, originalMessage) ->
			sendPacket.accept(new MqttSubscribeMessage(fixedHeader, originalMessage.variableHeader(), originalMessage.payload())));
		this.retryProcessor.start(ackService);
	}

	protected void onSubAckReceived() {
		this.retryProcessor.stop();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MqttPendingSubscription that = (MqttPendingSubscription) o;
		return Objects.equals(subscriptionList, that.subscriptionList);
	}

	@Override
	public int hashCode() {
		return Objects.hash(subscriptionList);
	}
}
