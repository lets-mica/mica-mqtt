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

import net.dreamlu.iot.mqtt.websocket.test.handler.IWsMsgHandler;
import org.tio.http.common.HttpConfig;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.RequestLine;
import org.tio.http.common.handler.HttpRequestHandler;

/**
 * mqtt websocket 子协议测试
 */
public class MqttWebTest {

	public static void main(String[] args) throws Exception {
		final int port = 8083;
		HttpConfig httpConfig = new HttpConfig(port, null, null, null);
		httpConfig.setUseSession(false);
		httpConfig.setCheckHost(false);
		httpConfig.setMonitorFileChange(false);

		HttpRequestHandler requestHandler = new AbstractMqttWebRequestHandler() {

			@Override
			public HttpResponse handler(HttpRequest packet) throws Exception {
				RequestLine requestLine = packet.getRequestLine();
				String path = requestLine.getPath();
				HttpResponse httpResponse = new HttpResponse(packet);
				httpResponse.setBody("hello".getBytes());
				return httpResponse;
			}
		};

		IWsMsgHandler mqttWsMsgHandler = new MqttWebHandler();
		MqttWebServer httpServerStarter = new MqttWebServer(httpConfig, requestHandler, mqttWsMsgHandler);
		httpServerStarter.start(); //启动http服务器
	}

}
