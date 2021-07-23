package net.dreamlu.iot.mqtt.core.client;


import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.codec.MqttSubscribeMessage;
import net.dreamlu.iot.mqtt.core.common.RetryProcessor;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * MqttPendingSubscription，参考于 netty-mqtt-client
 */
final class MqttPendingSubscription {
	private final MqttQoS mqttQoS;
	private final String topicFilter;
	private final IMqttClientMessageListener listener;
	private final RetryProcessor<MqttSubscribeMessage> retryProcessor = new RetryProcessor<>();

	MqttPendingSubscription(MqttQoS mqttQoS,
							String topicFilter,
							IMqttClientMessageListener listener,
							MqttSubscribeMessage message) {
		this.mqttQoS = mqttQoS;
		this.topicFilter = topicFilter;
		this.listener = listener;
		this.retryProcessor.setOriginalMessage(message);
	}

	protected MqttQoS getMqttQoS() {
		return mqttQoS;
	}

	protected String getTopicFilter() {
		return topicFilter;
	}

	protected IMqttClientMessageListener getListener() {
		return listener;
	}

	public MqttClientSubscription toSubscription() {
		return new MqttClientSubscription(getMqttQoS(), getTopicFilter(), getListener());
	}

	protected void startRetransmitTimer(ScheduledThreadPoolExecutor executor, Consumer<MqttMessage> sendPacket) {
		this.retryProcessor.setHandle((fixedHeader, originalMessage) ->
			sendPacket.accept(new MqttSubscribeMessage(fixedHeader, originalMessage.variableHeader(), originalMessage.payload())));
		this.retryProcessor.start(executor);
	}

	protected void onSubAckReceived() {
		this.retryProcessor.stop();
	}

}
