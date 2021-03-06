package net.dreamlu.iot.mqtt.core.client;

import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.codec.MqttUnsubscribeMessage;
import net.dreamlu.iot.mqtt.core.common.RetryProcessor;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * MqttPendingSubscription，参考于 netty-mqtt-client
 */
final class MqttPendingUnSubscription {
	private final String topic;
	private final RetryProcessor<MqttUnsubscribeMessage> retryProcessor = new RetryProcessor<>();

	MqttPendingUnSubscription(String topic, MqttUnsubscribeMessage unSubscribeMessage) {
		this.topic = topic;
		this.retryProcessor.setOriginalMessage(unSubscribeMessage);
	}

	protected String getTopic() {
		return topic;
	}

	protected void startRetransmissionTimer(ScheduledThreadPoolExecutor executor, Consumer<MqttMessage> sendPacket) {
		this.retryProcessor.setHandle((fixedHeader, originalMessage) ->
			sendPacket.accept(new MqttUnsubscribeMessage(fixedHeader, originalMessage.variableHeader(), originalMessage.payload())));
		this.retryProcessor.start(executor);
	}

	protected void onUnSubAckReceived() {
		this.retryProcessor.stop();
	}

}
