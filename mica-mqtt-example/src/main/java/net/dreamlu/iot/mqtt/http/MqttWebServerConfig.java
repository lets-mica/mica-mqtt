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

/**
 * mqtt http、websocket 启动配置
 *
 * @author L.cm
 */
public class MqttWebServerConfig {

	/**
	 * http、websocket 端口，默认：8083
	 */
	private int port = 8083;
	/**
	 * 开启 websocket 服务，默认：true
	 */
	private boolean websocketEnable = true;
	/**
	 * 开启 http 服务，默认：true
	 */
	private boolean httpEnable = true;
	/**
	 * Basic 认证账号
	 */
	private String username;
	/**
	 * Basic 认证密码
	 */
	private String password;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isWebsocketEnable() {
		return websocketEnable;
	}

	public void setWebsocketEnable(boolean websocketEnable) {
		this.websocketEnable = websocketEnable;
	}

	public boolean isHttpEnable() {
		return httpEnable;
	}

	public void setHttpEnable(boolean httpEnable) {
		this.httpEnable = httpEnable;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
