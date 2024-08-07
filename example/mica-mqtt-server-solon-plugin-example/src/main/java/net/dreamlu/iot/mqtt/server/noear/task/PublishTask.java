package net.dreamlu.iot.mqtt.server.noear.task;

import net.dreamlu.iot.mqtt.server.noear.MqttServerTemplate;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.noear.solon.scheduling.annotation.Scheduled;

import java.nio.charset.StandardCharsets;

/**
 * @author wsq
 */
@Component
public class PublishTask {
	@Inject
	private MqttServerTemplate mqttServerTemplate;

	@Scheduled(fixedDelay = 1000)
	public void publish() {
		boolean b = mqttServerTemplate.publishAll("/test/123", "mica最牛皮".getBytes(StandardCharsets.UTF_8));
	}

}
