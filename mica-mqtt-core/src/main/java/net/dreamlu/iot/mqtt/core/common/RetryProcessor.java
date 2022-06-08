package net.dreamlu.iot.mqtt.core.common;


import net.dreamlu.iot.mqtt.codec.MqttFixedHeader;
import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.core.util.timer.AckService;
import net.dreamlu.iot.mqtt.core.util.timer.AckTimerTask;

import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 重试处理器，参考于 netty-mqtt-client
 *
 * @param <T> MqttMessage
 */
public final class RetryProcessor<T extends MqttMessage> {

	private AckTimerTask ackTimerTask;
	private BiConsumer<MqttFixedHeader, T> handler;
	private T originalMessage;

	public void start(AckService ackService) {
		Objects.requireNonNull(ackService, "RetryProcessor ackService is null.");
		Objects.requireNonNull(this.handler, "RetryProcessor handler is null.");
		this.startTimer(ackService);
	}

	private void startTimer(AckService ackService) {
		this.ackTimerTask = ackService.addTask(() -> {
			MqttFixedHeader fixedHeader = new MqttFixedHeader(this.originalMessage.fixedHeader().messageType(), true, this.originalMessage.fixedHeader().qosLevel(), this.originalMessage.fixedHeader().isRetain(), this.originalMessage.fixedHeader().remainingLength());
			handler.accept(fixedHeader, originalMessage);
		}, 5, 10);
	}

	public void stop() {
		if (this.ackTimerTask != null) {
			this.ackTimerTask.cancel();
		}
	}

	public void setHandle(BiConsumer<MqttFixedHeader, T> runnable) {
		this.handler = runnable;
	}

	public void setOriginalMessage(T originalMessage) {
		this.originalMessage = originalMessage;
	}

}
