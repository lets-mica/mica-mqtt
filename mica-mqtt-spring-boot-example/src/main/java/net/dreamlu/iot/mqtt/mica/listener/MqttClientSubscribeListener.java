package net.dreamlu.iot.mqtt.mica.listener;

import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.spring.client.MqttClientSubscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

/**
 * 客户端消息监听
 *
 * @author L.cm
 */
@Service
public class MqttClientSubscribeListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttClientSubscribeListener.class);

	@MqttClientSubscribe("/test/#")
	public void subQos0(String topic, ByteBuffer payload) {
		logger.info("topic:{} payload:{}", topic, ByteBufferUtil.toString(payload));
	}

	@MqttClientSubscribe(value = "/qos1/#", qos = MqttQoS.AT_LEAST_ONCE)
	public void subQos1(String topic, ByteBuffer payload) {
		logger.info("topic:{} payload:{}", topic, ByteBufferUtil.toString(payload));
	}

}

