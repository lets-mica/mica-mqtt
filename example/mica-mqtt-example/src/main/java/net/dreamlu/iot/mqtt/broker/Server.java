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

package net.dreamlu.iot.mqtt.broker;

import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.broker.MqttBrokerMessageListener;
import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.support.DefaultMqttMessageDispatcher;

/**
 * 服务端，单纯的做消息转发
 *
 * @author L.cm
 */
public class Server {

	/**
	 * 客户端 A 模拟 APP 端订阅 `/a/door/open`，
	 * 客户端 B 模拟 web 网页端 mqtt.js 订阅 `/a/door/open`，
	 * Mqtt 服务端实现 `IMqttMessageListener`，将消息转交给 `AbstractMqttMessageDispatcher`（自定义实现）处理。
	 * 客户端 C 定时上报转态给 `/a/door/open`
	 * 结果：A 和 B 将收到 C 发布的消息，并完成相应的效果展示。
	 */
	public static void main(String[] args) {
		// 1. 消息转发处理器，可用来实现集群
		IMqttMessageDispatcher messageDispatcher = new DefaultMqttMessageDispatcher();
		// 2. mqtt broker 消息转发处理
		IMqttMessageListener messageListener = new MqttBrokerMessageListener(messageDispatcher);
		// 3. 启动服务
		MqttServer.create()
			.ip("0.0.0.0")
			.port(1883)
			.readBufferSize(512)
			.messageDispatcher(messageDispatcher)
			.messageListener(messageListener)
			.debug()
			.start();
	}
}
