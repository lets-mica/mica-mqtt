package net.dreamlu.iot.mqtt.server.listener;

import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.iot.mqtt.spring.server.MqttServerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.tio.core.ChannelContext;

/**
 * 监听器1，使用 Spring boot event 的方式
 *
 * <p>
 *     优点：解耦使用方便
 *     缺点：性能损失大
 * </p>
 *
 * @author wsq
 */
//@Service
public class MqttServerMessageListener1 {
	private static final Logger logger = LoggerFactory.getLogger(MqttServerMessageListener1.class);
	@Autowired
	private MqttServerTemplate mqttServerTemplate;

	@EventListener
	public void onMessage(Message message) {
		String clientId = message.getFromClientId();
		ChannelContext context = mqttServerTemplate.getChannelContext(clientId);
		logger.info("context:{} clientId:{} message:{} payload:{}", context, clientId, message, ByteBufferUtil.toString(message.getPayload()));
	}

}
