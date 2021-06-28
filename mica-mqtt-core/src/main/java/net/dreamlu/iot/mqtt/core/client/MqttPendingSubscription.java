package net.dreamlu.iot.mqtt.core.client;


import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.codec.MqttSubscribeMessage;
import net.dreamlu.iot.mqtt.core.common.MqttMessageListener;
import net.dreamlu.iot.mqtt.core.common.MqttSubscription;
import net.dreamlu.iot.mqtt.core.common.RetryProcessor;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * MqttPendingSubscription，参考于 netty-mqtt-client
 */
final class MqttPendingSubscription {
	private final MqttQoS mqttQoS;
	private final String topicFilter;
	private final MqttMessageListener listener;
	private final MqttSubscribeMessage subscribeMessage;
	private final RetryProcessor<MqttSubscribeMessage> retransmissionHandler = new RetryProcessor<>();

	MqttPendingSubscription(MqttQoS mqttQoS,
							String topicFilter,
							MqttMessageListener listener,
							MqttSubscribeMessage message) {
		this.mqttQoS = mqttQoS;
		this.topicFilter = topicFilter;
		this.listener = listener;
		this.subscribeMessage = message;
		this.retransmissionHandler.setOriginalMessage(message);
	}

	MqttQoS getMqttQoS() {
		return mqttQoS;
	}

	String getTopicFilter() {
		return topicFilter;
	}

	MqttSubscribeMessage getSubscribeMessage() {
		return subscribeMessage;
	}

	MqttMessageListener getListener() {
		return listener;
	}

	public MqttSubscription toSubscription() {
		return new MqttSubscription(getMqttQoS(), getTopicFilter(), getListener());
	}

	void startRetransmitTimer(ScheduledThreadPoolExecutor executor, Consumer<MqttMessage> sendPacket) {
		this.retransmissionHandler.setHandle((fixedHeader, originalMessage) ->
			sendPacket.accept(new MqttSubscribeMessage(fixedHeader, originalMessage.variableHeader(), originalMessage.payload())));
		this.retransmissionHandler.start(executor);
	}

	void onSubAckReceived() {
		this.retransmissionHandler.stop();
	}

}
