package net.dreamlu.iot.mqtt.spring.client;

import net.dreamlu.iot.mqtt.codec.*;
import net.dreamlu.iot.mqtt.core.client.*;
import net.dreamlu.iot.mqtt.core.common.MqttPendingPublish;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.tio.client.ClientChannelContext;

import java.nio.ByteBuffer;

/**
 * @author wsq
 */
@Service
@EnableConfigurationProperties(MqttClientProperties.class)
public class MqttClientTemplate {
	/**
	 * 是否启用
	 */
	private boolean enable = false;
	MqttClient client;

	public MqttClientTemplate(MqttClientProperties properties){
		client = MqttClient.create()
			.ip(properties.getIp())
			.port(properties.getPort())
			.username(properties.getUserName())
			.password(properties.getPassword())
			.version(MqttVersion.MQTT_5)
			.connect();
		enable=properties.isEnable();
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos0(String topicFilter, IMqttClientMessageListener listener) {
		return client.subscribe(MqttQoS.AT_MOST_ONCE, topicFilter, listener);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos1(String topicFilter, IMqttClientMessageListener listener) {
		return client.subscribe(MqttQoS.AT_LEAST_ONCE, topicFilter, listener);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos2(String topicFilter, IMqttClientMessageListener listener) {
		return client.subscribe(MqttQoS.EXACTLY_ONCE, topicFilter, listener);
	}

	/**
	 * 订阅
	 *
	 * @param mqttQoS     MqttQoS
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subscribe(MqttQoS mqttQoS, String topicFilter, IMqttClientMessageListener listener) {
		return client.subscribe(mqttQoS, topicFilter, listener);
	}

	/**
	 * 取消订阅
	 *
	 * @param topicFilter topicFilter
	 * @return MqttClient
	 */
	public MqttClient unSubscribe(String topicFilter) {
		return client.unSubscribe(topicFilter);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @return 是否发送成功
	 */
	public Boolean publish(String topic, ByteBuffer payload) {
		return client.publish(topic, payload, MqttQoS.AT_MOST_ONCE);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @return 是否发送成功
	 */
	public Boolean publish(String topic, ByteBuffer payload, MqttQoS qos) {
		return client.publish(topic, payload, qos, false);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public Boolean publish(String topic, ByteBuffer payload, boolean retain) {
		return client.publish(topic, payload, MqttQoS.AT_MOST_ONCE, retain);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public Boolean publish(String topic, ByteBuffer payload, MqttQoS qos, boolean retain) {
		return client.publish(topic, payload, qos, retain);
	}

	/**
	 * 重连
	 *
	 * @return MqttClient
	 * @throws Exception 异常
	 */
	public MqttClient reconnect() throws Exception {
		return client.reconnect();
	}

	/**
	 * 断开 mqtt 连接
	 *
	 * @return 是否成功
	 */
	public boolean disconnect() {
		return client.disconnect();
	}

	/**
	 * 停止客户端
	 *
	 * @return 是否停止成功
	 */
	public boolean stop() {
		return client.stop();
	}

	/**
	 * 获取 ClientChannelContext
	 *
	 * @return ClientChannelContext
	 */
	public ClientChannelContext getContext() {
		return client.getContext();
	}

}
