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

package net.dreamlu.iot.mqtt.websocket.test;

import net.dreamlu.iot.mqtt.websocket.MqttWsMsgHandler;
import org.tio.websocket.server.WsServerStarter;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.io.IOException;

/**
 * mqtt websocket 子协议测试
 */
public class MqttWebsocketTest {

	public static void main(String[] args) throws IOException {
		final int port = 8083;
		IWsMsgHandler mqttWsMsgHandler = new MqttWsMsgHandler();
		WsServerStarter wsServerStarter = new WsServerStarter(port, mqttWsMsgHandler);
		wsServerStarter.start();
	}

}
