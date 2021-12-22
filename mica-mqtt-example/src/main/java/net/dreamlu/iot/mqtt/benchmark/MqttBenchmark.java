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

import net.dreamlu.iot.mqtt.core.client.MqttClient;

import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * mqtt 压力测试
 *
 * @author L.cm
 */
public class MqttBenchmark {

	public static void main(String[] args) {
		// 1. 模拟 1w 连接，在开发机（i5-7500 4核4线程 win10 MqttServer 6G）1万连连接很轻松。
		// 注意： windows 上需要修改最大的 Tcp 连接数，不然超不过 2W。
		// 《修改Windows服务器最大的Tcp连接数》：https://www.jianshu.com/p/00136a97d2d8
		int connCount = 1_0000;
		int threadCount = 1000;
		String ip = "127.0.0.1";
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		for (int i = 0; i < connCount; i++) {
			int num = i;
			executor.submit(() -> newClient(ip, num));
		}
	}

	private static void newClient(String ip, int i) {
		MqttClient.create()
			.ip(ip)
			.clientId(UUID.randomUUID().toString() + '-' + i)
			.username("admin")
			.password("123456")
			.readBufferSize(512)
			.connect();
	}

}
