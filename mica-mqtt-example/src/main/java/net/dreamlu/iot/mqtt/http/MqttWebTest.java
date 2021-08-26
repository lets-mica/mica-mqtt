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

package net.dreamlu.iot.mqtt.http;

import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.MqttServerCreator;
import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.http.api.MqttHttpApi;
import net.dreamlu.iot.mqtt.core.server.http.api.auth.BasicAuthFilter;
import net.dreamlu.iot.mqtt.core.server.http.core.MqttWebServer;
import net.dreamlu.iot.mqtt.core.server.http.handler.MqttHttpRoutes;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.iot.mqtt.core.server.support.DefaultMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.websocket.MqttWsMsgHandler;
import org.tio.core.intf.AioHandler;
import org.tio.server.ServerTioConfig;
import org.tio.websocket.server.handler.IWsMsgHandler;

/**
 * mqtt websocket 子协议测试
 */
public class MqttWebTest {

	public static void main(String[] args) throws Exception {
		// 1. 消息转发处理器，可用来实现集群
		IMqttMessageDispatcher messageDispatcher = new DefaultMqttMessageDispatcher();
		// 2. 收到消息，将消息转发出去
		IMqttMessageListener messageListener = (clientId, topic, mqttQoS, payload) -> {
			Message message = new Message();
			message.setTopic(topic);
			message.setQos(mqttQoS.value());
			message.setPayload(payload.array());
			messageDispatcher.send(message);
		};

		// 3. 启动服务
		MqttServerCreator serverCreator = MqttServer.create()
			.ip("0.0.0.0")
			.port(1883)
			.readBufferSize(512)
			.messageDispatcher(messageDispatcher)
			.messageListener(messageListener)
			.websocketEnable(false)
			.debug();
		MqttServer mqttServer = serverCreator.start();
		ServerTioConfig serverConfig = mqttServer.getServerConfig();
		AioHandler aioHandler = serverConfig.getAioHandler();

		MqttHttpApi httpApi = new MqttHttpApi(serverCreator.getMessageDispatcher(), serverCreator.getSessionManager());
		httpApi.register();
		MqttHttpRoutes.addFilter(new BasicAuthFilter("123", "123"));

		IWsMsgHandler mqttWsMsgHandler = new MqttWsMsgHandler(aioHandler);
		MqttWebServer httpServerStarter = new MqttWebServer(serverCreator, mqttWsMsgHandler);
		ServerTioConfig httpIioConfig = httpServerStarter.getServerTioConfig();
		httpIioConfig.share(serverConfig);
		httpIioConfig.groupStat = serverConfig.groupStat;
		// 启动http服务器
		httpServerStarter.start();
	}

}
