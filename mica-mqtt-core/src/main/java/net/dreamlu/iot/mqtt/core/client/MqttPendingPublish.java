package net.dreamlu.iot.mqtt.core.client;

import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.common.RetryProcessor;

import java.nio.ByteBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * MqttPendingPublish，参考于 netty-mqtt-client
 */
final class MqttPendingPublish {
	private final ByteBuffer payload;
	private final MqttPublishMessage message;
	private final MqttQoS qos;
	private final RetryProcessor<MqttPublishMessage> publishRetransmissionHandler = new RetryProcessor<>();
	private final RetryProcessor<MqttMessage> pubRelRetransmissionHandler = new RetryProcessor<>();

	MqttPendingPublish(ByteBuffer payload, MqttPublishMessage message, MqttQoS qos) {
		this.payload = payload;
		this.message = message;
		this.qos = qos;
		this.publishRetransmissionHandler.setOriginalMessage(message);
	}

	ByteBuffer getPayload() {
		return payload;
	}

	MqttPublishMessage getMessage() {
		return message;
	}

	MqttQoS getQos() {
		return qos;
	}

	void startPublishRetransmissionTimer(ScheduledThreadPoolExecutor executor, Consumer<MqttMessage> sendPacket) {
		this.publishRetransmissionHandler.setHandle(((fixedHeader, originalMessage) -> {
			this.payload.rewind();
			sendPacket.accept(new MqttPublishMessage(fixedHeader, originalMessage.variableHeader(), this.payload));
		}));
		this.publishRetransmissionHandler.start(executor);
	}

	void onPubAckReceived() {
		this.publishRetransmissionHandler.stop();
	}

	void setPubRelMessage(MqttMessage pubRelMessage) {
		this.pubRelRetransmissionHandler.setOriginalMessage(pubRelMessage);
	}

	void startPubRelRetransmissionTimer(ScheduledThreadPoolExecutor executor, Consumer<MqttMessage> sendPacket) {
		this.pubRelRetransmissionHandler.setHandle((fixedHeader, originalMessage) ->
			sendPacket.accept(new MqttMessage(fixedHeader, originalMessage.variableHeader())));
		this.pubRelRetransmissionHandler.start(executor);
	}

	void onPubCompReceived() {
		this.pubRelRetransmissionHandler.stop();
	}

}
