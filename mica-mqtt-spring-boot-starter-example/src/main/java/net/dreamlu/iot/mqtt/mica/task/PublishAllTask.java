package net.dreamlu.iot.mqtt.mica.task;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

/**
 * @author wsq
 */
@Service
public class PublishAllTask {
	@Autowired
	private MqttServer mqttServer;

	@Scheduled(fixedDelay = 3000)
	public void run() {
		mqttServer.publishAll("/test/123", ByteBuffer.wrap("mica最牛皮".getBytes()), MqttQoS.EXACTLY_ONCE);
	}
}
