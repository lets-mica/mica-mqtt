package org.dromara.mica.mqtt.client.solon;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Configuration;

/**
 * @author wsq
 */
@Configuration
public class MqttClientApplication {

	/**
	 * 先启动 mica-mqtt-server-spring-boot-example 再启动本项目，进行测试
	 */
	public static void main(String[] args) {
		Solon.start(MqttClientApplication.class, args);
	}

}
