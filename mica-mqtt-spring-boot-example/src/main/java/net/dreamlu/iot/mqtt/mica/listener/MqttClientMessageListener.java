package net.dreamlu.iot.mqtt.mica.listener;

import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.core.client.IMqttClientMessageListener;
import net.dreamlu.iot.mqtt.spring.client.MqttClientSubscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

/**
 * 客户端消息监听的另一种方式
 *
 * @author L.cm
 */
@Service
@MqttClientSubscribe("/test/#")
public class MqttClientMessageListener implements IMqttClientMessageListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttClientMessageListener.class);

	@Override
	public void onMessage(String topic, ByteBuffer payload) {
		logger.info("topic:{} payload:{}", topic, ByteBufferUtil.toString(payload));
	}
}

