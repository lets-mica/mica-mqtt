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

package net.dreamlu.iot.mqtt.jfinal.client;

import com.jfinal.plugin.IPlugin;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import net.dreamlu.iot.mqtt.core.client.MqttClientCreator;

import java.util.function.Consumer;

/**
 * mica mqtt client 插件
 *
 * @author L.cm
 */
public class MqttClientPlugin implements IPlugin {
	private final MqttClientCreator clientCreator;
	private MqttClient mqttClient;

	public MqttClientPlugin() {
		this.clientCreator = new MqttClientCreator();
	}

	/**
	 * 配置 mica mqtt
	 *
	 * @param consumer MqttClientCreator Consumer
	 */
	public void config(Consumer<MqttClientCreator> consumer) {
		consumer.accept(this.clientCreator);
	}

	@Override
	public boolean start() {
		if (this.mqttClient == null) {
			this.mqttClient = clientCreator.connect();
		} else {
			this.mqttClient.reconnect();
		}
		MqttClientKit.init(this.mqttClient);
		return true;
	}

	@Override
	public boolean stop() {
		if (this.mqttClient != null) {
			this.mqttClient.stop();
		}
		return true;
	}

}
