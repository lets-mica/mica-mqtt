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

package net.dreamlu.iot.mqtt.proxy;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.client.MqttClient;

/**
 * 2 个 mqtt 服务间，使用 2 个 client 做数据传输
 *
 * @author L.cm
 */
public class MqttClientProxy {

	public static void main(String[] args) {
		MqttClient client1 = MqttClient.create()
			.ip("ip1")
			.port(1883)
			.clientId("clientI")
			.username("mica")
			.password("mica")
			.debug()
			.connectSync();

		MqttClient client2 = MqttClient.create()
			.ip("ip2")
			.port(1883)
			.clientId("client2")
			.username("mica")
			.password("mica")
			.debug()
			.connectSync();

		String[] topics = new String[]{
			"$share/test/link/product1/+/event/+/post",
			"$share/test/link/product2/+/event/+/post",
			"$share/test/link/product3/+/event/+/post"
		};
		client1.subscribe(topics, MqttQoS.QOS0, (context, topic, message, payload) -> {
			client2.publish(topic, payload);
		});
	}

}
