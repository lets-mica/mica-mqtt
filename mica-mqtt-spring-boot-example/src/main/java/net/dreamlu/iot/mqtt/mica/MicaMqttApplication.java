package net.dreamlu.iot.mqtt.mica;

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
public class MicaMqttApplication {

	/**
	 * 启动同目录下的 MqttClientTest、MqttServerTest 进行测试
	 */
	public static void main(String[] args) {
		SpringApplication.run(MicaMqttApplication.class, args);
	}

}
