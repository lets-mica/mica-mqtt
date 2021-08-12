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

package net.dreamlu.iot.mqtt.core.server;

import net.dreamlu.iot.mqtt.codec.ByteBufferAllocator;
import net.dreamlu.iot.mqtt.codec.MqttConstant;
import net.dreamlu.iot.mqtt.core.server.dispatcher.AbstractMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import net.dreamlu.iot.mqtt.core.server.session.InMemoryMqttSessionManager;
import net.dreamlu.iot.mqtt.core.server.store.IMqttMessageStore;
import net.dreamlu.iot.mqtt.core.server.store.InMemoryMqttMessageStore;
import net.dreamlu.iot.mqtt.core.server.support.DefaultMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.core.server.support.DefaultMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.support.DefaultMqttServerAuthHandler;
import net.dreamlu.iot.mqtt.core.server.support.DefaultMqttServerProcessor;
import net.dreamlu.iot.mqtt.core.server.websocket.MqttWsMsgHandler;
import org.tio.core.ssl.SslConfig;
import org.tio.core.stat.IpStatListener;
import org.tio.server.ServerTioConfig;
import org.tio.server.TioServer;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;
import org.tio.utils.thread.pool.DefaultThreadFactory;
import org.tio.websocket.common.WsTioUuid;
import org.tio.websocket.server.WsServerAioHandler;
import org.tio.websocket.server.WsServerAioListener;
import org.tio.websocket.server.WsServerConfig;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * mqtt 服务端参数构造
 *
 * @author L.cm
 */
public class MqttServerCreator {

	/**
	 * 名称
	 */
	private String name = "Mica-Mqtt-Server";
	/**
	 * 服务端 ip
	 */
	private String ip = "127.0.0.1";
	/**
	 * 端口
	 */
	private int port = 1883;
	/**
	 * 心跳超时时间(单位: 毫秒 默认: 1000 * 120)，如果用户不希望框架层面做心跳相关工作，请把此值设为0或负数
	 */
	private Long heartbeatTimeout;
	/**
	 * 接收数据的 buffer size，默认：8092
	 */
	private int readBufferSize = MqttConstant.DEFAULT_MAX_BYTES_IN_MESSAGE;
	/**
	 * 消息解析最大 bytes 长度，默认：8092
	 */
	private int maxBytesInMessage = MqttConstant.DEFAULT_MAX_BYTES_IN_MESSAGE;
	/**
	 * 堆内存和堆外内存
	 */
	private ByteBufferAllocator bufferAllocator = ByteBufferAllocator.HEAP;
	/**
	 * ssl 证书配置
	 */
	private SslConfig sslConfig;
	/**
	 * tio 的 IpStatListener
	 */
	private IpStatListener ipStatListener;
	/**
	 * 认证处理器
	 */
	private IMqttServerAuthHandler authHandler;
	/**
	 * 消息处理器
	 */
	private IMqttMessageDispatcher messageDispatcher;
	/**
	 * 消息存储
	 */
	private IMqttMessageStore messageStore;
	/**
	 * session 管理
	 */
	private IMqttSessionManager sessionManager;
	/**
	 * 消息监听
	 */
	private IMqttMessageListener messageListener;
	/**
	 * 链接状态监听
	 */
	private IMqttConnectStatusListener connectStatusListener;
	/**
	 * debug
	 */
	private boolean debug = false;
	/**
	 * mqtt 3.1 会校验此参数
	 */
	private int maxClientIdLength = MqttConstant.DEFAULT_MAX_CLIENT_ID_LENGTH;
	/**
	 * 开启 websocket 服务，默认：true
	 */
	private boolean websocketEnable = true;
	/**
	 * websocket 端口，默认：8083
	 */
	private int websocketPort = 8083;

	public String getName() {
		return name;
	}

	public MqttServerCreator name(String name) {
		this.name = name;
		return this;
	}

	public String getIp() {
		return ip;
	}

	public MqttServerCreator ip(String ip) {
		this.ip = ip;
		return this;
	}

	public int getPort() {
		return port;
	}

	public MqttServerCreator port(int port) {
		this.port = port;
		return this;
	}

	public Long getHeartbeatTimeout() {
		return heartbeatTimeout;
	}

	public MqttServerCreator heartbeatTimeout(Long heartbeatTimeout) {
		this.heartbeatTimeout = heartbeatTimeout;
		return this;
	}

	public int getReadBufferSize() {
		return readBufferSize;
	}

	public MqttServerCreator readBufferSize(int readBufferSize) {
		this.readBufferSize = readBufferSize;
		return this;
	}

	public int getMaxBytesInMessage() {
		return maxBytesInMessage;
	}

	public MqttServerCreator maxBytesInMessage(int maxBytesInMessage) {
		if (maxBytesInMessage < 1) {
			throw new IllegalArgumentException("maxBytesInMessage must be greater than 0.");
		}
		this.maxBytesInMessage = maxBytesInMessage;
		return this;
	}

	public ByteBufferAllocator getBufferAllocator() {
		return bufferAllocator;
	}

	public MqttServerCreator bufferAllocator(ByteBufferAllocator bufferAllocator) {
		this.bufferAllocator = bufferAllocator;
		return this;
	}

	public SslConfig getSslConfig() {
		return sslConfig;
	}

	public MqttServerCreator useSsl(InputStream keyStoreInputStream, InputStream trustStoreInputStream, String pwd) {
		try {
			this.sslConfig = SslConfig.forServer(keyStoreInputStream, trustStoreInputStream, pwd);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public MqttServerCreator useSsl(String keyStoreFile, String trustStoreFile, String pwd) {
		try {
			this.sslConfig = SslConfig.forServer(keyStoreFile, trustStoreFile, pwd);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
		return this;
	}

	public IpStatListener getIpStatListener() {
		return ipStatListener;
	}

	public MqttServerCreator ipStatListener(IpStatListener ipStatListener) {
		this.ipStatListener = ipStatListener;
		return this;
	}

	public IMqttServerAuthHandler getAuthHandler() {
		return authHandler;
	}

	public MqttServerCreator authHandler(IMqttServerAuthHandler authHandler) {
		this.authHandler = authHandler;
		return this;
	}

	public IMqttMessageDispatcher getMessageDispatcher() {
		return messageDispatcher;
	}

	public MqttServerCreator messageDispatcher(IMqttMessageDispatcher messageDispatcher) {
		this.messageDispatcher = messageDispatcher;
		return this;
	}

	public IMqttMessageStore getMessageStore() {
		return messageStore;
	}

	public MqttServerCreator messageStore(IMqttMessageStore messageStore) {
		this.messageStore = messageStore;
		return this;
	}

	public IMqttSessionManager getSessionManager() {
		return sessionManager;
	}

	public MqttServerCreator sessionManager(IMqttSessionManager sessionManager) {
		this.sessionManager = sessionManager;
		return this;
	}

	public IMqttMessageListener getMessageListener() {
		return messageListener;
	}

	public MqttServerCreator messageListener(IMqttMessageListener messageListener) {
		this.messageListener = messageListener;
		return this;
	}

	public IMqttConnectStatusListener getConnectStatusListener() {
		return connectStatusListener;
	}

	public MqttServerCreator connectStatusListener(IMqttConnectStatusListener connectStatusListener) {
		this.connectStatusListener = connectStatusListener;
		return this;
	}

	public boolean isDebug() {
		return debug;
	}

	public MqttServerCreator debug() {
		this.debug = true;
		return this;
	}

	public int getMaxClientIdLength() {
		return maxClientIdLength;
	}

	public MqttServerCreator maxClientIdLength(int maxClientIdLength) {
		this.maxClientIdLength = maxClientIdLength;
		return this;
	}

	public boolean isWebsocketEnable() {
		return websocketEnable;
	}

	public MqttServerCreator websocketEnable(boolean websocketEnable) {
		this.websocketEnable = websocketEnable;
		return this;
	}

	public int getWebsocketPort() {
		return websocketPort;
	}

	public MqttServerCreator websocketPort(int websocketPort) {
		this.websocketPort = websocketPort;
		return this;
	}

	public MqttServer start() {
		Objects.requireNonNull(this.messageListener, "Mqtt Server message listener cannot be null.");
		if (this.authHandler == null) {
			this.authHandler = new DefaultMqttServerAuthHandler();
		}
		if (this.messageDispatcher == null) {
			this.messageDispatcher = new DefaultMqttMessageDispatcher();
		}
		if (this.sessionManager == null) {
			this.sessionManager = new InMemoryMqttSessionManager();
		}
		if (this.messageStore == null) {
			this.messageStore = new InMemoryMqttMessageStore();
		}
		if (this.connectStatusListener == null) {
			this.connectStatusListener = new DefaultMqttConnectStatusListener();
		}
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(2, DefaultThreadFactory.getInstance("MqttServer"));
		DefaultMqttServerProcessor serverProcessor = new DefaultMqttServerProcessor(this, executor);
		// 1. 处理消息
		ServerAioHandler handler = new MqttServerAioHandler(this, serverProcessor);
		// 2. t-io 监听
		ServerAioListener listener = new MqttServerAioListener(this);
		// 2. t-io 配置
		ServerTioConfig tioConfig = new ServerTioConfig(this.name, handler, listener);
		// 4. 设置 t-io 心跳 timeout
		if (this.heartbeatTimeout != null) {
			tioConfig.setHeartbeatTimeout(this.heartbeatTimeout);
		}
		if (this.ipStatListener != null) {
			tioConfig.setIpStatListener(this.ipStatListener);
		}
		if (this.sslConfig != null) {
			tioConfig.setSslConfig(this.sslConfig);
		}
		if (this.debug) {
			tioConfig.debug = true;
		}
		// 5. mqtt 消息最大长度
		tioConfig.setReadBufferSize(this.readBufferSize);
		TioServer tioServer = new TioServer(tioConfig);
		// 6. 不校验版本号，社区版设置无效
		tioServer.setCheckLastVersion(false);
		MqttServer mqttServer = new MqttServer(tioServer, this, executor);
		// 7. 如果是默认的消息转发器，设置 mqttServer
		if (this.messageDispatcher instanceof AbstractMqttMessageDispatcher) {
			((AbstractMqttMessageDispatcher) this.messageDispatcher).config(mqttServer);
		}
		// 8. 启动 mqtt tcp
		try {
			tioServer.start(this.ip, this.port);
		} catch (IOException e) {
			throw new IllegalStateException("Mica mqtt tcp server start fail.", e);
		}
		// 9. 启动 mqtt websocket server
		if (this.websocketEnable) {
			WsServerConfig wsServerConfig = new WsServerConfig(this.websocketPort, false);
			IWsMsgHandler mqttWsMsgHandler = new MqttWsMsgHandler(handler);
			WsServerAioHandler wsServerAioHandler = new WsServerAioHandler(wsServerConfig, mqttWsMsgHandler);
			WsServerAioListener wsServerAioListener = new WsServerAioListener();
			ServerTioConfig wsTioConfig = new ServerTioConfig(this.name + "-Websocket", wsServerAioHandler, wsServerAioListener);
			wsTioConfig.setHeartbeatTimeout(0);
			wsTioConfig.setTioUuid(new WsTioUuid());
			wsTioConfig.setReadBufferSize(1024 * 30);
			TioServer tioWsServer = new TioServer(wsTioConfig);
			mqttServer.setTioWsServer(tioWsServer);
			wsTioConfig.share(tioConfig);
			try {
				tioWsServer.start(this.ip, wsServerConfig.getBindPort());
			} catch (IOException e) {
				throw new IllegalStateException("Mica mqtt websocket server start fail.", e);
			}
		}
		return mqttServer;
	}

}
