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
	 * 启动同目录下的 MqttClientTest 进行测试
	 */
	public static void main(String[] args) {
		System.setProperty("spring.profiles.active", "server");
		SpringApplication.run(MqttServerApplication.class, args);
	}

}
