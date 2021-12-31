package net.dreamlu.iot.mqtt.mica.auth;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerSubscribeValidator;
import org.springframework.context.annotation.Configuration;
import org.tio.core.ChannelContext;

/**
 * 订阅校验
 *
 * @author L.cm
 */
@Configuration(proxyBeanMethods = false)
public class MqttSubscribeValidator implements IMqttServerSubscribeValidator {

	@Override
	public boolean isValid(ChannelContext context, String clientId, String topicFilter, MqttQoS qoS) {
		// 校验客户端订阅的 topic，校验成功返回 true，失败返回 false
		return true;
	}

}
