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

package net.dreamlu.iot.mqtt.core.client;

import net.dreamlu.iot.mqtt.codec.MqttMessage;
import org.tio.client.ClientChannelContext;
import org.tio.client.TioClient;
import org.tio.core.Tio;

/**
 * mqtt 客户端
 *
 * @author L.cm
 */
public class MqttClient {

	private MqttClientConfig clientConfig;
	private MqttClientProcessor processor;
	private TioClient tioClient;
	private ClientChannelContext clientContext;

	// TODO add subscribe

	public void connect() {

	}

	/**
	 * 重连
	 *
	 * @throws Exception 异常
	 */
	public void reconnect() throws Exception {
		checkState();
		tioClient.reconnect(clientContext, clientConfig.getTimeout());
	}

	/**
	 * 断开 mqtt 连接
	 */
	public void disconnect() {
		checkState();
		Tio.send(clientContext, MqttMessage.DISCONNECT);
	}

	/**
	 * 停止客户端
	 *
	 * @return 是否停止成功
	 */
	public boolean stop() {
		checkState();
		return tioClient.stop();
	}

	public ClientChannelContext getClientContext() {
		checkState();
		return clientContext;
	}

	private void checkState() {
		if (clientContext == null) {
			throw new IllegalStateException("您需要先 connect。");
		}
	}

}
