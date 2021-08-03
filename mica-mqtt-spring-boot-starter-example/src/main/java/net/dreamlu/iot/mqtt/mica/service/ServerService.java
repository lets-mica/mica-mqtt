package net.dreamlu.iot.mqtt.mica.service;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.spring.server.MqttServerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

/**
 * @author wsq
 */
@Service
public class ServerService {
    private static final Logger logger = LoggerFactory.getLogger(ServerService.class);
    @Autowired
    private MqttServerTemplate server;

    public boolean publish(String body) {
        server.publishAll("/test/123", ByteBuffer.wrap(body.getBytes()), MqttQoS.EXACTLY_ONCE);
        return true;
    }
}
