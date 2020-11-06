package net.dreamlu.iot.mqtt.client;

import net.dreamlu.iot.mqtt.codec.MqttVersion;
import net.dreamlu.iot.mqtt.core.client.MqttClient;

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
		// 初始化 mqtt 客户端
		MqttClient client = MqttClient.create()
			.ip("127.0.0.1")
			.username("admin")
			.password("123456")
			.protocolVersion(MqttVersion.MQTT_5)
			.connect();

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				client.publish("testtopicxx", ByteBuffer.wrap("mica最牛皮".getBytes()));
			}
		}, 1000, 2000);

	}
}
