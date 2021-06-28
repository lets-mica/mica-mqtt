package net.dreamlu.iot.mqtt.server;

import net.dreamlu.iot.mqtt.core.server.IMqttMessageIdGenerator;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 仅仅作为演示，实际需要考虑集群
 */
public class MqttMessageIdGenerator implements IMqttMessageIdGenerator {
	private final AtomicInteger value = new AtomicInteger(1);

	@Override
	public int getId() {
		this.value.compareAndSet(0xffff, 1);
		return this.value.getAndIncrement();
	}
}
