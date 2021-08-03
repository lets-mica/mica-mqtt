package net.dreamlu.iot.mqtt.mica.listener;

import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.core.client.IMqttClientMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

@Service
public class MqttClientMessageListener implements IMqttClientMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MqttServerMessageListener.class);

    @Override
    public void onMessage(String topic, ByteBuffer byteBuffer) {
        logger.info("topic:{}  message:{}", topic, ByteBufferUtil.toString(byteBuffer));
    }
}

