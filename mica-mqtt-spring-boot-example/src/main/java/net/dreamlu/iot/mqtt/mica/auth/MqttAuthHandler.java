package net.dreamlu.iot.mqtt.mica.auth;

import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerAuthHandler;
import org.springframework.context.annotation.Configuration;
import org.tio.core.ChannelContext;

/**
 * mqtt tcp websocket 认证
 *
 * @author L.cm
 */
@Configuration(proxyBeanMethods = false)
public class MqttAuthHandler implements IMqttServerAuthHandler {

	@Override
	public boolean authenticate(ChannelContext context, String uniqueId, String clientId, String userName, String password) {
		// 客户端认证逻辑实现
		return true;
	}

}
