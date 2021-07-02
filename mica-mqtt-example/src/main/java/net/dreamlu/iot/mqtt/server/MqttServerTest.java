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

package net.dreamlu.iot.mqtt.server;

import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.common.MqttSubscription;
import net.dreamlu.iot.mqtt.core.server.DefaultMqttServerSubManager;
import net.dreamlu.iot.mqtt.core.server.IMqttMessageIdGenerator;
import net.dreamlu.iot.mqtt.core.server.MqttServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * mqtt 服务端测试
 *
 * @author L.cm
 */
public class MqttServerTest {

	public static void main(String[] args) throws IOException {
		DefaultMqttServerSubManager subManager = new DefaultMqttServerSubManager();
		// 服务端注册订阅
		subManager.subscribe(new MqttSubscription(MqttQoS.AT_MOST_ONCE, "/test/#", ((topic, payload) -> {
			System.out.println(topic + '\t' + ByteBufferUtil.toString(payload));
		})));

		IMqttMessageIdGenerator messageIdGenerator = new MqttMessageIdGenerator();
		MqttSubscribeStore subscribeStore = new MqttSubscribeStore();
		MqttPublishManager publishManager = new MqttPublishManager();
		MqttServer mqttServer = MqttServer.create()
			// 默认：127.0.0.1
			.ip("127.0.0.1")
			// 默认：1883
			.port(1883)
			.messageIdGenerator(messageIdGenerator)
			.publishManager(publishManager)
			.subManager(subManager)
			.subscribeStore(subscribeStore)
			.debug() // 开启 debug 信息日志
			.start();

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				mqttServer.publishAll("/test/123", ByteBuffer.wrap("mica最牛皮".getBytes()));
			}
		}, 1000, 2000);
	}
}
