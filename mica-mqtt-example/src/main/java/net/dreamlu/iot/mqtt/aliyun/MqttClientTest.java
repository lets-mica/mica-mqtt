package net.dreamlu.iot.mqtt.aliyun;

import net.dreamlu.iot.mqtt.core.client.MqttClient;

import java.nio.ByteBuffer;

/**
 * 客户端测试
 *
 * @author L.cm
 */
public class MqttClientTest {

	public static void main(String[] args) throws Exception {
		String productKey = "g27jB42P9hm";
		String deviceName = "3dbc1cb4";
		String deviceSecret = "";
		// 计算MQTT连接参数。
		MqttSign sign = new MqttSign(productKey, deviceName, deviceSecret);

		String username = sign.getUsername();
		String password = sign.getPassword();
		String clientId = sign.getClientId();
		System.out.println("username: " + username);
		System.out.println("password: " + password);
		System.out.println("clientid: " + clientId);

		// 初始化 mqtt 客户端
		MqttClient client = MqttClient.create()
			.ip(productKey + ".iot-as-mqtt.cn-shanghai.aliyuncs.com")
			.port(443)
			.username(username)
			.password(password)
			.clientId(clientId)
			.connect();

		client.subQos0("/sys/g27jB42P9hm/3dbc1cb4/thing/event/property/post_reply", (topic, payload) -> {

		});

		String content = "{\"id\":\"1\",\"version\":\"1.0\",\"params\":{\"LightSwitch\":1}}";
		client.publish("/sys/g27jB42P9hm/" + deviceName + "/thing/event/property/post", ByteBuffer.wrap(content.getBytes()));
	}
}
