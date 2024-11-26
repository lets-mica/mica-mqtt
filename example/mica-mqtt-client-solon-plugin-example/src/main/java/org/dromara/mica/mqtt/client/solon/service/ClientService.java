package org.dromara.mica.mqtt.client.solon.service;


import org.dromara.mica.mqtt.client.solon.MqttClientTemplate;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author wsq
 */
@Component
public class ClientService {
	private static final Logger             logger = LoggerFactory.getLogger(ClientService.class);
	@Inject
	private              MqttClientTemplate client;

	public boolean publish(String body) {
		client.publish("/test/client", body.getBytes(StandardCharsets.UTF_8));
		return true;
	}

	public boolean sub() {
		client.subQos0("/test/#", (context, topic, message, payload) -> {
			logger.info(topic + '\t' + new String(payload, StandardCharsets.UTF_8));
		});
		return true;
	}

}
