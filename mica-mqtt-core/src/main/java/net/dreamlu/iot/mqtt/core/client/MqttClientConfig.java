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

import net.dreamlu.iot.mqtt.codec.MqttVersion;
import org.tio.core.ssl.SslConfig;

/**
 * MqttClient 配置
 *
 * @author L.cm
 */
public class MqttClientConfig {

	/**
	 * ip，可为空，为空 t-io 默认为 127.0.0.1
	 */
	private String ip;
	/**
	 * 端口
	 */
	private int port;
	/**
	 * 超时时间，t-io 配置，可为 null
	 */
	private Integer timeout;
	/**
	 * Keep Alive (s)
	 */
	private int keepAliveSecs = 60;
	/**
	 * SSL配置
	 */
	protected SslConfig sslConfig;
	/**
	 * 自动重连
	 */
	private boolean reconnect = true;
	/**
	 * 重连重试时间
	 */
	private Long reInterval;
	/**
	 * 客户端 id，默认：随机生成
	 */
	private String clientId;
	/**
	 * mqtt 协议，默认：3_1_1
	 */
	private MqttVersion protocolVersion = MqttVersion.MQTT_3_1_1;
	/**
	 * 用户名
	 */
	private String username = null;
	/**
	 * 密码
	 */
	private String password = null;
	/**
	 * 清除会话
	 * <p>
	 * false 表示如果订阅的客户机断线了，那么要保存其要推送的消息，如果其重新连接时，则将这些消息推送。
	 * true 表示消除，表示客户机是第一次连接，消息所以以前的连接信息。
	 * </p>
	 */
	private boolean cleanSession = true;
	/**
	 * 遗嘱消息
	 */
	private MqttWillMessage willMessage;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	public Integer getKeepAliveSecs() {
		return keepAliveSecs;
	}

	public void setKeepAliveSecs(Integer keepAliveSecs) {
		this.keepAliveSecs = keepAliveSecs;
	}

	public SslConfig getSslConfig() {
		return sslConfig;
	}

	public void setSslConfig(SslConfig sslConfig) {
		this.sslConfig = sslConfig;
	}

	public boolean isReconnect() {
		return reconnect;
	}

	public void setReconnect(boolean reconnect) {
		this.reconnect = reconnect;
	}

	public Long getReInterval() {
		return reInterval;
	}

	public void setReInterval(Long reInterval) {
		this.reInterval = reInterval;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public MqttVersion getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(MqttVersion protocolVersion) {
		this.protocolVersion = protocolVersion;
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

	public boolean isCleanSession() {
		return cleanSession;
	}

	public void setCleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
	}

	public MqttWillMessage getWillMessage() {
		return willMessage;
	}

	public void setWillMessage(MqttWillMessage willMessage) {
		this.willMessage = willMessage;
	}
}
