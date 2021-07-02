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

import net.dreamlu.iot.mqtt.codec.ByteBufferAllocator;
import net.dreamlu.iot.mqtt.codec.MqttProperties;
import net.dreamlu.iot.mqtt.codec.MqttVersion;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientTioConfig;
import org.tio.client.ReconnConf;
import org.tio.client.TioClient;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.Node;
import org.tio.core.ssl.SslConfig;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.thread.pool.DefaultThreadFactory;

import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * mqtt 客户端构造器
 *
 * @author L.cm
 */
public final class MqttClientCreator {

	/**
	 * 名称
	 */
	private String name = "Mica-Mqtt-Client";
	/**
	 * ip，可为空，为空 t-io 默认为 127.0.0.1
	 */
	private String ip;
	/**
	 * 端口，默认：1883
	 */
	private int port = 1883;
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
	/**
	 * mqtt5 properties
	 */
	private MqttProperties properties;
	/**
	 * ByteBuffer Allocator，支持堆内存和堆外内存，默认为：堆内存
	 */
	private ByteBufferAllocator bufferAllocator = ByteBufferAllocator.HEAP;

	public String getName() {
		return name;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public Integer getTimeout() {
		return timeout;
	}

	public int getKeepAliveSecs() {
		return keepAliveSecs;
	}

	public SslConfig getSslConfig() {
		return sslConfig;
	}

	public boolean isReconnect() {
		return reconnect;
	}

	public Long getReInterval() {
		return reInterval;
	}

	public String getClientId() {
		return clientId;
	}

	public MqttVersion getProtocolVersion() {
		return protocolVersion;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public boolean isCleanSession() {
		return cleanSession;
	}

	public MqttWillMessage getWillMessage() {
		return willMessage;
	}

	public MqttProperties getProperties() {
		return properties;
	}

	public ByteBufferAllocator getBufferAllocator() {
		return bufferAllocator;
	}

	public MqttClientCreator name(String name) {
		this.name = name;
		return this;
	}

	public MqttClientCreator ip(String ip) {
		this.ip = ip;
		return this;
	}

	public MqttClientCreator port(int port) {
		this.port = port;
		return this;
	}

	public MqttClientCreator timeout(int timeout) {
		this.timeout = timeout;
		return this;
	}

	public MqttClientCreator keepAliveSecs(int keepAliveSecs) {
		this.keepAliveSecs = keepAliveSecs;
		return this;
	}

	public MqttClientCreator sslConfig(SslConfig sslConfig) {
		this.sslConfig = sslConfig;
		return this;
	}

	public MqttClientCreator reconnect(boolean reconnect) {
		this.reconnect = reconnect;
		return this;
	}

	public MqttClientCreator reInterval(long reInterval) {
		this.reInterval = reInterval;
		return this;
	}

	public MqttClientCreator clientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public MqttClientCreator protocolVersion(MqttVersion protocolVersion) {
		this.protocolVersion = protocolVersion;
		return this;
	}

	public MqttClientCreator username(String username) {
		this.username = username;
		return this;
	}

	public MqttClientCreator password(String password) {
		this.password = password;
		return this;
	}

	public MqttClientCreator cleanSession(boolean cleanSession) {
		this.cleanSession = cleanSession;
		return this;
	}

	public MqttClientCreator willMessage(MqttWillMessage willMessage) {
		this.willMessage = willMessage;
		return this;
	}

	public MqttClientCreator willMessage(Consumer<MqttWillMessage.Builder> consumer) {
		MqttWillMessage.Builder builder = MqttWillMessage.builder();
		consumer.accept(builder);
		return willMessage(builder.build());
	}

	public MqttClientCreator properties(MqttProperties properties) {
		this.properties = properties;
		return this;
	}

	public MqttClientCreator bufferAllocator(ByteBufferAllocator allocator) {
		this.bufferAllocator = allocator;
		return this;
	}

	public MqttClient connect() throws Exception {
		// 1. 生成 默认的 clientId
		String clientId = getClientId();
		if (StrUtil.isBlank(clientId)) {
			// 默认为：MICA-MQTT- 前缀和 36进制的纳秒数
			this.clientId("MICA-MQTT-" + Long.toString(System.nanoTime(), 36));
		}
		MqttClientStore clientStore = new MqttClientStore();
		// 客户端处理器
		CountDownLatch connLatch = new CountDownLatch(1);
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, DefaultThreadFactory.getInstance("MqttClient"));
		MqttClientProcessor processor = new DefaultMqttClientProcessor(clientStore, connLatch, executor);
		// 2. 初始化 mqtt 处理器
		ClientAioHandler clientAioHandler = new MqttClientAioHandler(this.bufferAllocator, Objects.requireNonNull(processor));
		ClientAioListener clientAioListener = new MqttClientAioListener(this, clientStore, executor);
		// 3. 重连配置
		ReconnConf reconnConf = null;
		if (this.reconnect) {
			if (this.reInterval != null && this.reInterval > 0) {
				reconnConf = new ReconnConf(this.reInterval);
			} else {
				reconnConf = new ReconnConf();
			}
		}
		// 4. tioConfig
		ClientTioConfig tioConfig = new ClientTioConfig(clientAioHandler, clientAioListener, reconnConf);
		tioConfig.setName(this.name);
		// 5. tioClient
		TioClient tioClient = new TioClient(tioConfig);
		ClientChannelContext context = tioClient.connect(new Node(this.ip, this.port), this.timeout);
		// 6. 等待连接成功之后继续
		connLatch.await();
		return new MqttClient(tioClient, this, context, clientStore, executor);
	}

}
