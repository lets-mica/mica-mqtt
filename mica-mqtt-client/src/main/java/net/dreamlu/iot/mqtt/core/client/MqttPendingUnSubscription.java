package net.dreamlu.iot.mqtt.core.client;

import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.codec.MqttUnsubscribeMessage;
import net.dreamlu.iot.mqtt.core.common.RetryProcessor;
import org.tio.utils.timer.TimerTaskService;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * MqttPendingSubscription，参考于 netty-mqtt-client
 */
final class MqttPendingUnSubscription {
	private final List<String> topics;
	private final RetryProcessor<MqttUnsubscribeMessage> retryProcessor = new RetryProcessor<>();

	MqttPendingUnSubscription(List<String> topics, MqttUnsubscribeMessage unSubscribeMessage) {
		this.topics = topics;
		this.retryProcessor.setOriginalMessage(unSubscribeMessage);
	}

	protected List<String> getTopics() {
		return topics;
	}

	protected void startRetransmissionTimer(TimerTaskService taskService, Consumer<MqttMessage> sendPacket) {
		this.retryProcessor.setHandle((fixedHeader, originalMessage) ->
			sendPacket.accept(new MqttUnsubscribeMessage(fixedHeader, originalMessage.variableHeader(), originalMessage.payload())));
		this.retryProcessor.start(taskService);
	}

	protected void onUnSubAckReceived() {
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
		MqttPendingUnSubscription that = (MqttPendingUnSubscription) o;
		return Objects.equals(topics, that.topics);
	}

	@Override
	public int hashCode() {
		return Objects.hash(topics);
	}
}
