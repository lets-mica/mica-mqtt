package net.dreamlu.iot.mqtt.jfinal.client;

/**
 * mica mqtt client 插件测试
 *
 * @author L.cm
 */
public class MqttClientPluginTest {

	public static void main(String[] args) {
		MqttClientPlugin plugin = new MqttClientPlugin();
		plugin.config(mqttClientCreator -> {
			// mqttClientCreator 上有很多方法，详见 mica-mqttx-core
			mqttClientCreator.port(1883).username("mica").password("mica");
		});
		plugin.start();
	}

}
