/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & dreamlu.net).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dreamlu.iot.mqtt.client;

import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.codec.MqttVersion;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 客户端测试
 *
 * @author L.cm
 */
public class MqttClientTest {
	private static final Logger logger = LoggerFactory.getLogger(MqttClientTest.class);

	public static void main(String[] args) {
		// 初始化 mqtt 客户端
		MqttClient client = MqttClient.create()
			.ip("127.0.0.1")
			.port(5883)
			.username("admin")
			.password("123456")
			.version(MqttVersion.MQTT_5)
			.connect();

		client.subQos0("/test/#", (topic, payload) -> {
			logger.info(topic + '\t' + ByteBufferUtil.toString(payload));
		});

		client.subQos0("/#", (topic, payload) -> {
			logger.info(topic + '\t' + ByteBufferUtil.toString(payload));
		});

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				client.publish("/test/client", ByteBuffer.wrap("mica最牛皮".getBytes(StandardCharsets.UTF_8)));
			}
		}, 1000, 2000);
	}
}
