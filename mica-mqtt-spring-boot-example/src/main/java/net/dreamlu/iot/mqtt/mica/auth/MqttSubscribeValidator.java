package net.dreamlu.iot.mqtt.mica.auth;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.codec.MqttTopicSubscription;
import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerSubscribeValidator;
import org.springframework.context.annotation.Configuration;
import org.tio.core.ChannelContext;

import java.util.List;

/**
 * 订阅校验
 *
 * @author L.cm
 */
@Configuration(proxyBeanMethods = false)
public class MqttSubscribeValidator implements IMqttServerSubscribeValidator {

	@Override
	public boolean isValid(ChannelContext context, String clientId, List<MqttTopicSubscription> topicSubscriptionList) {
		// 校验客户端订阅的 topic，校验成功返回 true，失败返回 false
		for (MqttTopicSubscription subscription : topicSubscriptionList) {
			String topicName = subscription.topicName();
			MqttQoS mqttQoS = subscription.qualityOfService();
		}
		return true;
	}
}
