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

import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import net.dreamlu.iot.mqtt.core.util.ThreadUtil;
import net.dreamlu.iot.mqtt.core.util.timer.AckService;
import net.dreamlu.iot.mqtt.core.util.timer.EmptyAckService;
import org.tio.client.ClientChannelContext;
import org.tio.core.Tio;
import org.tio.core.stat.ChannelStat;
import org.tio.utils.SystemTimer;
import org.tio.utils.Threads;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
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
		int connCount = 5_0000;
		String ip = "127.0.0.1";
		final List<MqttClient> clientList = new CopyOnWriteArrayList<>();
		SynThreadPoolExecutor tioExecutor = Threads.getTioExecutor();
		ThreadPoolExecutor groupExecutor = Threads.getGroupExecutor();
		AckService ackService = new EmptyAckService();
		for (int i = 0; i < connCount; i++) {
			newClient(ip, i, clientList, tioExecutor, groupExecutor, ackService);
		}
		// 自定义心跳
		new Thread(() -> {
			while (true) {
				ThreadUtil.sleep(20 * 1000L);
				long currTime = SystemTimer.currTime;
				for (MqttClient client : clientList) {
					ClientChannelContext context = client.getContext();
					if (context == null || context.isClosed || context.isRemoved) {
						continue;
					}
					ChannelStat stat = context.stat;
					long compareTime = Math.max(stat.latestTimeOfReceivedByte, stat.latestTimeOfSentPacket);
					long interval = currTime - compareTime;
					if (interval >= 30) {
						Tio.send(context, MqttMessage.PINGREQ);
					}
				}
			}
		}, "timer-heartbeat").start();
	}

	private static void newClient(String ip, int i, final List<MqttClient> clientList,
								  SynThreadPoolExecutor tioExecutor,
								  ThreadPoolExecutor groupExecutor,
								  AckService ackService) {
		MqttClient client = MqttClient.create()
			.ip(ip)
			.clientId(UUID.randomUUID().toString() + i)
			.readBufferSize(128)
			// 取消 t-io 的心跳线程
			.keepAliveSecs(0)
			// 取消自动重连
			.reconnect(false)
			.tioExecutor(tioExecutor)
			.groupExecutor(groupExecutor)
			.mqttExecutor(tioExecutor)
			.ackService(ackService)
			.connect();
		clientList.add(client);
	}

}
