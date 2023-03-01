package net.dreamlu.iot.mqtt.client.listener;

import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import net.dreamlu.iot.mqtt.core.client.IMqttClientMessageListener;
import net.dreamlu.iot.mqtt.spring.client.MqttClientSubscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 * 客户端消息监听的另一种方式
 *
 * @author L.cm
 */
@Service
@MqttClientSubscribe("${topic1}")
public class MqttClientMessageListener implements IMqttClientMessageListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttClientMessageListener.class);

	@Override
	public void onMessage(ChannelContext context, String topic, MqttPublishMessage message, ByteBuffer payload) {
		logger.info("topic:{} payload:{}", topic, ByteBufferUtil.toString(payload));
	}
}

