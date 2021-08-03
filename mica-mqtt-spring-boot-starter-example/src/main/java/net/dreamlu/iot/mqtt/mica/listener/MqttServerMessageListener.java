package net.dreamlu.iot.mqtt.mica.listener;

import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

/**
 * @author wsq
 */
@Service
public class MqttServerMessageListener implements IMqttMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MqttServerMessageListener.class);
    @Override
    public void onMessage(String clientId, String topic, MqttQoS mqttQoS, ByteBuffer byteBuffer) {
        logger.info("clientId:{} topic:{} mqttQoS:{} message:{}", clientId, topic, mqttQoS, ByteBufferUtil.toString(byteBuffer));
    }
}
