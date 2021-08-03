package net.dreamlu.iot.mqtt.mica.listener;

import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.core.client.IMqttClientMessageListener;
import net.dreamlu.iot.mqtt.spring.client.MqttClientTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Service
public class MqttClientSubscribe implements SmartInitializingSingleton {
	private static final Logger logger = LoggerFactory.getLogger(MqttClientSubscribe.class);

	@Autowired
	private MqttClientTemplate clientTemplate;

	@Override
	public void afterSingletonsInstantiated() {
		// 启动完成之后订阅
		clientTemplate.subQos0("/test/#", new IMqttClientMessageListener() {
			@Override
			public void onMessage(String topic, ByteBuffer payload) {
				logger.info("topic:{} payload:{}", topic, ByteBufferUtil.toString(payload));
			}
		});
	}
}

