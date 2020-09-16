package net.dreamlu.iot.mqtt.core.client;

import net.dreamlu.iot.mqtt.codec.MqttVersion;

/**
 * MqttClient 配置
 *
 * @author L.cm
 */
public class MqttClientConfig {

	/**
	 * ip，可为空，为空 t-io 默认为 127.0.0.1
	 */
	private String ip;
	/**
	 * 端口
	 */
	private int port;
	/**
	 * 超时时间，t-io 配置，可为 null
	 */
	private Integer timeout;
	/**
	 * 自动重连
	 */
	private boolean reconnect = true;
	/**
	 * 重连重试时间
	 */
	private Long reInterval;
	/**
	 * 客户端 id，默认：随机生成
	 */
	private String clientId;
	/**
	 * mqtt 协议，默认：3_1_1
	 */
	private MqttVersion protocolVersion = MqttVersion.MQTT_3_1_1;
	/**
	 * 用户名
	 */
	private String username = null;
	/**
	 * 密码
	 */
	private String password = null;
	/**
	 * 清除会话
	 */
	private boolean cleanSession = true;
	/**
	 * 遗嘱消息
	 */
	private MqttWillMessage willMessage;

}
