package net.dreamlu.iot.mqtt.jfinal.server;

/**
 * mica mqtt server 插件测试
 *
 * @author L.cm
 */
public class MqttServerPluginTest {

	public static void main(String[] args) {
		MqttServerPlugin plugin = new MqttServerPlugin();
		plugin.config(mqttServerCreator -> {
			// mqttServerCreator 上有很多方法，详见 mica-mqttx-core
			mqttServerCreator.port(1883).webPort(8083).websocketEnable(true);
		});
		plugin.start();
	}

}
