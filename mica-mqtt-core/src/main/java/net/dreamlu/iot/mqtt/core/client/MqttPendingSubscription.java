package net.dreamlu.iot.mqtt.core.client;


import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.codec.MqttSubscribeMessage;
import net.dreamlu.iot.mqtt.core.common.MqttMessageListener;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * MqttPendingSubscription，参考于 netty-mqtt-client
 */
final class MqttPendingSubscription {
	private final MqttQoS mqttQoS;
	private final String topicFilter;
	private final MqttMessageListener listener;
	private final RetryProcessor<MqttSubscribeMessage> retryProcessor = new RetryProcessor<>();

	MqttPendingSubscription(MqttQoS mqttQoS,
							String topicFilter,
							MqttMessageListener listener,
							MqttSubscribeMessage message) {
		this.mqttQoS = mqttQoS;
		this.topicFilter = topicFilter;
		this.listener = listener;
		this.retryProcessor.setOriginalMessage(message);
	}

	MqttQoS getMqttQoS() {
		return mqttQoS;
	}

	String getTopicFilter() {
		return topicFilter;
	}

	MqttMessageListener getListener() {
		return listener;
	}

	public MqttSubscription toSubscription() {
		return new MqttSubscription(getMqttQoS(), getTopicFilter(), getListener());
	}

	void startRetransmitTimer(ScheduledThreadPoolExecutor executor, Consumer<MqttMessage> sendPacket) {
		this.retryProcessor.setHandle((fixedHeader, originalMessage) ->
			sendPacket.accept(new MqttSubscribeMessage(fixedHeader, originalMessage.variableHeader(), originalMessage.payload())));
		this.retryProcessor.start(executor);
	}

	void onSubAckReceived() {
		this.retryProcessor.stop();
	}

}
