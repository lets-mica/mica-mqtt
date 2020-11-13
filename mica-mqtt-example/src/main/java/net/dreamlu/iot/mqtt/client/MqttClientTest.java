package net.dreamlu.iot.mqtt.client;

import net.dreamlu.iot.mqtt.core.client.MqttClient;
import org.tio.client.ClientChannelContext;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 客户端测试
 *
 * @author L.cm
 */
public class MqttClientTest {

	public static void main(String[] args) throws Exception {
		String productKey = "g27jB42P9hm";
		String deviceName = "3dbc1cb4";
		String deviceSecret = "87b337020a99ddb9eab5bb68f8b2f891";

		// 计算MQTT连接参数。
		MqttSign sign = new MqttSign();
		sign.calculate(productKey, deviceName, deviceSecret);

		String username = sign.getUsername();
		String password = sign.getPassword();
		String clientId = sign.getClientid();
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

		Thread.sleep(1000L);
//		Timer timer = new Timer();
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
				String content = "{\"id\":\"1\",\"version\":\"1.0\",\"params\":{\"LightSwitch\":1}}";
				client.publish("/sys/g27jB42P9hm/" + deviceName + "/thing/event/property/post", ByteBuffer.wrap(content.getBytes()));
//			}
//		}, 1000, 2000);
	}
}
