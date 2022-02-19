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
import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerAuthHandler;
import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerPublishPermission;
import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerSubscribeValidator;
import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerUniqueIdService;
import net.dreamlu.iot.mqtt.core.server.dispatcher.AbstractMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.http.core.MqttWebServer;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import net.dreamlu.iot.mqtt.core.server.session.InMemoryMqttSessionManager;
import net.dreamlu.iot.mqtt.core.server.store.IMqttMessageStore;
import net.dreamlu.iot.mqtt.core.server.store.InMemoryMqttMessageStore;
import net.dreamlu.iot.mqtt.core.server.support.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ssl.SslConfig;
import org.tio.core.stat.IpStatListener;
import org.tio.server.ServerTioConfig;
import org.tio.server.TioServer;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;
import org.tio.utils.Threads;
import org.tio.utils.hutool.StrUtil;
import org.tio.utils.thread.pool.DefaultThreadFactory;

import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.Objects;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * mqtt 服务端参数构造
 *
 * @author L.cm
 */
public class MqttServerCreator {
	private static final Logger logger = LoggerFactory.getLogger(MqttServer.class);

	/**
	 * 名称
	 */
	private String name = "Mica-Mqtt-Server";
	/**
	 * 服务端 ip，默认为空，可不设置
	 */
	private String ip;
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
	 * 唯一 id 服务
	 */
	private IMqttServerUniqueIdService uniqueIdService;
	/**
	 * 订阅校验器
	 */
	private IMqttServerSubscribeValidator subscribeValidator;
	/**
	 * 发布权限校验
	 */
	private IMqttServerPublishPermission publishPermission;
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
	 * 连接状态监听
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
	 * http、websocket 端口，默认：8083
	 */
	private int webPort = 8083;
	/**
	 * 开启 websocket 服务，默认：true
	 */
	private boolean websocketEnable = true;
	/**
	 * 开启 http 服务，默认：true
	 */
	private boolean httpEnable = false;
	/**
	 * http Basic 认证账号
	 */
	private String httpBasicUsername;
	/**
	 * http Basic 认证密码
	 */
	private String httpBasicPassword;
	/**
	 * 节点名称，用于处理集群
	 */
	private String nodeName;
	/**
	 * 是否用队列发送
	 */
	private boolean useQueueSend = true;
	/**
	 * 是否用队列解码（系统初始化时确定该值，中途不要变更此值，否则在切换的时候可能导致消息丢失）
	 */
	private boolean useQueueDecode = false;
	/**
	 * 是否开启监控，默认：false 不开启，节省内存
	 */
	private boolean statEnable = false;

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

	public IMqttServerUniqueIdService getUniqueIdService() {
		return uniqueIdService;
	}

	public MqttServerCreator uniqueIdService(IMqttServerUniqueIdService uniqueIdService) {
		this.uniqueIdService = uniqueIdService;
		return this;
	}

	public IMqttServerSubscribeValidator getSubscribeValidator() {
		return subscribeValidator;
	}

	public MqttServerCreator subscribeValidator(IMqttServerSubscribeValidator subscribeValidator) {
		this.subscribeValidator = subscribeValidator;
		return this;
	}

	public IMqttServerPublishPermission getPublishPermission() {
		return publishPermission;
	}

	public MqttServerCreator publishPermission(IMqttServerPublishPermission publishPermission) {
		this.publishPermission = publishPermission;
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

	public int getWebPort() {
		return webPort;
	}

	public MqttServerCreator webPort(int webPort) {
		this.webPort = webPort;
		return this;
	}

	public boolean isWebsocketEnable() {
		return websocketEnable;
	}

	public MqttServerCreator websocketEnable(boolean websocketEnable) {
		this.websocketEnable = websocketEnable;
		return this;
	}

	public boolean isHttpEnable() {
		return httpEnable;
	}

	public MqttServerCreator httpEnable(boolean httpEnable) {
		this.httpEnable = httpEnable;
		return this;
	}

	public String getHttpBasicUsername() {
		return httpBasicUsername;
	}

	public MqttServerCreator httpBasicAuth(String username, String password) {
		if (StrUtil.isBlank(username) || StrUtil.isBlank(password)) {
			throw new IllegalArgumentException("Mqtt http basic auth username or password is blank.");
		}
		this.httpBasicUsername = username;
		this.httpBasicPassword = password;
		return this;
	}

	public String getHttpBasicPassword() {
		return httpBasicPassword;
	}

	public String getNodeName() {
		return nodeName;
	}

	public MqttServerCreator nodeName(String nodeName) {
		this.nodeName = nodeName;
		return this;
	}

	public boolean isUseQueueSend() {
		return useQueueSend;
	}

	public MqttServerCreator useQueueSend(boolean useQueueSend) {
		this.useQueueSend = useQueueSend;
		return this;
	}

	public boolean isUseQueueDecode() {
		return useQueueDecode;
	}

	public MqttServerCreator useQueueDecode(boolean useQueueDecode) {
		this.useQueueDecode = useQueueDecode;
		return this;
	}

	public boolean isStatEnable() {
		return statEnable;
	}

	public MqttServerCreator statEnable() {
		return statEnable(true);
	}

	public MqttServerCreator statEnable(boolean enable) {
		this.statEnable = enable;
		return this;
	}

	public MqttServer build() {
		Objects.requireNonNull(this.messageListener, "Mqtt Server message listener cannot be null.");
		// 默认的节点名称，用于集群
		if (StrUtil.isBlank(this.nodeName)) {
			this.nodeName = ManagementFactory.getRuntimeMXBean().getName() + ':' + port;
		}
		if (this.authHandler == null) {
			this.authHandler = new DefaultMqttServerAuthHandler();
		}
		if (this.uniqueIdService == null) {
			this.uniqueIdService = new DefaultMqttServerUniqueIdServiceImpl();
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
		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(Threads.MAX_POOL_SIZE_FOR_TIO, DefaultThreadFactory.getInstance("MqttServer"));
		DefaultMqttServerProcessor serverProcessor = new DefaultMqttServerProcessor(this, executor);
		// 1. 处理消息
		ServerAioHandler handler = new MqttServerAioHandler(this, serverProcessor);
		// 2. t-io 监听
		ServerAioListener listener = new MqttServerAioListener(this, executor);
		// 3. t-io 配置
		ServerTioConfig tioConfig = new ServerTioConfig(this.name, handler, listener);
		tioConfig.setUseQueueDecode(this.useQueueDecode);
		tioConfig.setUseQueueSend(this.useQueueSend);
		// 4. mqtt 消息最大长度
		tioConfig.setReadBufferSize(this.readBufferSize);
		// 5. 是否开启监控
		tioConfig.statOn = this.statEnable;
		// 6. 设置 t-io 心跳 timeout
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
		TioServer tioServer = new TioServer(tioConfig);
		// 7. 不校验版本号，社区版设置无效
		tioServer.setCheckLastVersion(false);
		// 9 配置 mqtt http/websocket server
		MqttWebServer webServer;
		logger.info("Mica mqtt http api enable:{} websocket enable:{}", this.httpEnable, this.websocketEnable);
		if (this.httpEnable || this.websocketEnable) {
			webServer = MqttWebServer.config(this, tioConfig);
		} else {
			webServer = null;
		}
		// MqttServer
		MqttServer mqttServer = new MqttServer(tioServer, webServer, this, executor);
		// 9. 如果是默认的消息转发器，设置 mqttServer
		if (this.messageDispatcher instanceof AbstractMqttMessageDispatcher) {
			((AbstractMqttMessageDispatcher) this.messageDispatcher).config(mqttServer);
		}
		return mqttServer;
	}

	public MqttServer start() {
		MqttServer mqttServer = this.build();
		mqttServer.start();
		return mqttServer;
	}
}
