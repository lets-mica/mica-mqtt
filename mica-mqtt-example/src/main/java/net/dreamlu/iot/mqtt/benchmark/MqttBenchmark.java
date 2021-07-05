/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.net.dreamlu.net).
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

package net.dreamlu.iot.mqtt.benchmark;

import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * mqtt 压力测试
 *
 * @author L.cm
 */
public class MqttBenchmark {
	private static final Logger logger = LoggerFactory.getLogger(MqttBenchmark.class);

	public static void main(String[] args) throws Exception {
		// 1. 模拟 1w 连接，在开发机（i5-7500 4核4线程 win10 MqttServer 6G）1万连连接很轻松。
		int clientCount = 1_0000;
		for (int i = 0; i < clientCount; i++) {
			// 2. 初始化 mqtt 客户端
			MqttClient client = MqttClient.create()
				.username("admin")
				.password("123456")
				.connect();
			// 3. 订阅服务端消息
			client.subQos0("/#", (topic, payload) -> {
				logger.info(topic + '\t' + ByteBufferUtil.toString(payload));
			});
		}
	}

}
