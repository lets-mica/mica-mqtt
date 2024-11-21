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

package org.dromara.mica.mqtt.jfinal.server;

import com.jfinal.plugin.IPlugin;
import org.dromara.mica.mqtt.core.server.MqttServer;
import org.dromara.mica.mqtt.core.server.MqttServerCreator;

import java.util.function.Consumer;

/**
 * mica mqtt server 插件
 *
 * @author L.cm
 */
public class MqttServerPlugin implements IPlugin {
	private final MqttServerCreator serverCreator;
	private MqttServer mqttServer;

	public MqttServerPlugin() {
		this.serverCreator = new MqttServerCreator();
	}

	/**
	 * 配置 mica mqtt
	 *
	 * @param consumer MqttServerCreator Consumer
	 */
	public void config(Consumer<MqttServerCreator> consumer) {
		consumer.accept(this.serverCreator);
	}

	@Override
	public boolean start() {
		if (this.mqttServer == null) {
			this.mqttServer = serverCreator.start();
		}
		MqttServerKit.init(this.mqttServer);
		return true;
	}

	@Override
	public boolean stop() {
		if (this.mqttServer != null) {
			this.mqttServer.stop();
		}
		return true;
	}

}
