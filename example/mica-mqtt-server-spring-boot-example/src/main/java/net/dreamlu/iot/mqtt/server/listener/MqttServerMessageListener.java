package net.dreamlu.iot.mqtt.server.listener;

import net.dreamlu.iot.mqtt.core.server.dispatcher.AbstractMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author wsq
 */
@Service
public class MqttServerMessageListener extends AbstractMqttMessageDispatcher {
	private static final Logger logger = LoggerFactory.getLogger(MqttServerMessageListener.class);

	@Override
	public void sendAll(Message message) {
		logger.info("message:{}", message);
	}

}
