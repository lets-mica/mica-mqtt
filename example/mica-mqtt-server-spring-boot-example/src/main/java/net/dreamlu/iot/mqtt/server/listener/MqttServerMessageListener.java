package net.dreamlu.iot.mqtt.server.listener;

import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.iot.mqtt.spring.server.MqttServerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;

/**
 * @author wsq
 */
@Service
public class MqttServerMessageListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttServerMessageListener.class);
	@Autowired
	private MqttServerTemplate mqttServerTemplate;

	@EventListener
	public void onMessage(Message message) {
		String clientId = message.getFromClientId();
		ChannelContext context = mqttServerTemplate.getChannelContext(clientId);
		logger.info("context:{} clientId:{} message:{} payload:{}", context, clientId, message, ByteBufferUtil.toString(message.getPayload()));
	}

}
