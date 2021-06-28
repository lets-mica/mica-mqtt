package net.dreamlu.iot.mqtt.core.common;


import net.dreamlu.iot.mqtt.codec.MqttFixedHeader;
import net.dreamlu.iot.mqtt.codec.MqttMessage;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

/**
 * 重试处理器，参考于 netty-mqtt-client
 *
 * @param <T> MqttMessage
 */
public final class RetryProcessor<T extends MqttMessage> {

	private ScheduledFuture<?> timer;
	private int timeout = 10;
	private BiConsumer<MqttFixedHeader, T> handler;
	private T originalMessage;

	public void start(ScheduledThreadPoolExecutor executor) {
		Objects.requireNonNull(executor, "RetryProcessor executor is null.");
		Objects.requireNonNull(this.handler, "RetryProcessor handler is null.");
		this.timeout = 10;
		this.startTimer(executor);
	}

	private void startTimer(ScheduledThreadPoolExecutor executor) {
		this.timer = executor.schedule(() -> {
			this.timeout += 5;
			MqttFixedHeader fixedHeader = new MqttFixedHeader(this.originalMessage.fixedHeader().messageType(), true, this.originalMessage.fixedHeader().qosLevel(), this.originalMessage.fixedHeader().isRetain(), this.originalMessage.fixedHeader().remainingLength());
			handler.accept(fixedHeader, originalMessage);
			startTimer(executor);
		}, timeout, TimeUnit.SECONDS);
	}

	public void stop() {
		if (this.timer != null) {
			this.timer.cancel(true);
		}
	}

	public void setHandle(BiConsumer<MqttFixedHeader, T> runnable) {
		this.handler = runnable;
	}

	public void setOriginalMessage(T originalMessage) {
		this.originalMessage = originalMessage;
	}

}
