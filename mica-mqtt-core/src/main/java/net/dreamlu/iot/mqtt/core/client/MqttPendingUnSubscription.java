package net.dreamlu.iot.mqtt.core.client;

import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.codec.MqttUnsubscribeMessage;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * MqttPendingSubscription，参考于 netty-mqtt-client
 */
final class MqttPendingUnSubscription {

	private final String topic;
	private final RetryProcessor<MqttUnsubscribeMessage> retransmissionHandler = new RetryProcessor<>();

	MqttPendingUnSubscription(String topic, MqttUnsubscribeMessage unSubscribeMessage) {
		this.topic = topic;
		this.retransmissionHandler.setOriginalMessage(unSubscribeMessage);
	}

	String getTopic() {
		return topic;
	}

	void startRetransmissionTimer(ScheduledThreadPoolExecutor executor, Consumer<MqttMessage> sendPacket) {
		this.retransmissionHandler.setHandle((fixedHeader, originalMessage) ->
			sendPacket.accept(new MqttUnsubscribeMessage(fixedHeader, originalMessage.variableHeader(), originalMessage.payload())));
		this.retransmissionHandler.start(executor);
	}

	void onUnSubAckReceived() {
		this.retransmissionHandler.stop();
	}

}
