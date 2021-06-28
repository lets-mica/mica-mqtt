package net.dreamlu.iot.mqtt.core.server;

/**
 * 服务端消息id
 *
 * @author L.cm
 */
public interface IMqttMessageIdGenerator {

	/**
	 * 获取消息 id
	 *
	 * @return 消息id
	 */
	int getId();

}
