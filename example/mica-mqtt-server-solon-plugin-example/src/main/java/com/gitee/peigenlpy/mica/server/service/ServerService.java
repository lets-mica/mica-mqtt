package com.gitee.peigenlpy.mica.server.service;

import com.gitee.peigenlpy.mica.server.MqttServerTemplate;
import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * @author wsq
 */
@Component
public class ServerService {
    private static final Logger             logger = LoggerFactory.getLogger(ServerService.class);
    @Inject
    private              MqttServerTemplate server;

    public boolean publish(String body) {
        boolean result = server.publishAll("/test/123", body.getBytes(StandardCharsets.UTF_8));
        logger.info("Mqtt publishAll result:{}", result);
        return result;
    }
}
