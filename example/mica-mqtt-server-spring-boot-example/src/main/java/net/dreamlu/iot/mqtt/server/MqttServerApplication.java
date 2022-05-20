package net.dreamlu.iot.mqtt.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author wsq
 */
@SpringBootApplication
@EnableScheduling
@EnableCaching
public class MqttServerApplication {

	/**
	 * 先启动本项目，再启动 mica-mqtt-client-spring-boot-example 进行测试
	 */
	public static void main(String[] args) {
		SpringApplication.run(MqttServerApplication.class, args);
	}

}
