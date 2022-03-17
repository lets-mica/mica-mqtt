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
import net.dreamlu.iot.mqtt.core.util.ThreadUtil;
import org.tio.utils.Threads;
import org.tio.utils.thread.pool.DefaultThreadFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * mqtt 压力测试
 *
 * @author L.cm
 */
public class MqttBenchmark {

	public static void main(String[] args) {
		// 注意： windows 上需要修改最大的 Tcp 连接数，不然超不过 2W。
		// 《修改Windows服务器最大的Tcp连接数》：https://www.jianshu.com/p/00136a97d2d8
		int connCount = 6_0000;
		int threadCount = 100;
		String ip = "127.0.0.1";
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		ThreadPoolExecutor groupExecutor = ThreadUtil.getGroupExecutor(connCount);
		ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(10, DefaultThreadFactory.getInstance("MqttClient"));
		for (int i = 0; i < connCount; i++) {
			int num = i;
			executor.submit(() -> newClient(ip, num, groupExecutor, scheduledExecutor));
			// 避免太快，导致 jvm 崩溃
			ThreadUtil.sleep(1);
		}
	}

	private static void newClient(String ip, int i, ThreadPoolExecutor groupExecutor,
								  ScheduledThreadPoolExecutor scheduledExecutor) {
		MqttClient.create()
			.ip(ip)
			.clientId("Bench_" + i)
			.readBufferSize(128)
			// 取消 t-io 的心跳线程
			.keepAliveSecs(0)
			.tioExecutor(Threads.getTioExecutor())
			.groupExecutor(groupExecutor)
			.scheduledExecutor(scheduledExecutor)
			.connect();
	}


}
