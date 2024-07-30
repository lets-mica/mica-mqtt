package net.dreamlu.iot.mqtt.server.noear;


import org.noear.solon.Solon;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.scheduling.annotation.EnableScheduling;

/**
 * @author wsq
 */
@Configuration
@EnableScheduling
public class MqttServerApplication {

	public static void main(String[] args) {
		Solon.start(MqttServerApplication.class, args);
	}

}
