package net.dreamlu.iot.mqtt.client;

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
public class MqttClientApplication {

	/**
	 * 启动同目录下的 MqttServerTest 进行测试
	 */
	public static void main(String[] args) {
		System.setProperty("spring.profiles.active", "client");
		SpringApplication.run(MqttClientApplication.class, args);
	}

}
