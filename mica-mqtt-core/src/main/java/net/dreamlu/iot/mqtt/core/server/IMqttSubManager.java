package net.dreamlu.iot.mqtt.core.server;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.common.MqttSubscription;

import java.util.List;

/**
 * mqtt 服务端 订阅管理
 *
 * @author L.cm
 */
public interface IMqttSubManager {

	/**
	 * 注册订阅
	 *
	 * @param subscription 订阅信息
	 */
	void register(MqttSubscription subscription);

	/**
	 * 获取匹配的订阅
	 *
	 * @param topicName topicName
	 * @param mqttQoS   MqttQoS
	 * @return 订阅信息
	 */
	List<MqttSubscription> getMatchedSubscription(String topicName, MqttQoS mqttQoS);

	/**
	 * 清理
	 */
	void clean();

}
