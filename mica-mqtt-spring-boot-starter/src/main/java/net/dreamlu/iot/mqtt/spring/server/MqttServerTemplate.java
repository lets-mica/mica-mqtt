package net.dreamlu.iot.mqtt.spring.server;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.IMqttServerAuthHandler;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.MqttServerCreator;
import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import net.dreamlu.iot.mqtt.core.server.store.IMqttMessageStore;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.tio.core.ChannelContext;
import org.tio.core.stat.IpStatListener;
import org.tio.utils.hutool.StrUtil;

import java.nio.ByteBuffer;
import java.util.Objects;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(MqttServerProperties.class)
public class MqttServerTemplate implements SmartLifecycle, Ordered {
	/**
	 * 是否启用
	 */
	private boolean enable = false;
	private MqttServer mqttServer;
	private final MqttServerCreator serverCreator;
	private Thread mqttServerThread;
	private boolean running = false;

	public MqttServerTemplate(
		MqttServerProperties properties,
		ObjectProvider<IMqttServerAuthHandler> authHandlerObjectProvider,
		ObjectProvider<IMqttMessageDispatcher> messageDispatcherObjectProvider,
		ObjectProvider<IMqttMessageStore> messageStoreObjectProvider,
		ObjectProvider<IMqttSessionManager> sessionManagerObjectProvider,
		ObjectProvider<IMqttMessageListener> messageListenerObjectProvider,
		ObjectProvider<IMqttConnectStatusListener> connectStatusListenerObjectProvider,
		ObjectProvider<IpStatListener> ipStatListenerObjectProvider,
		ObjectProvider<MqttServerCustomizer> customizers)
	{
		serverCreator = MqttServer.create()
			.name(properties.getName())
			.ip(properties.getIp())
			.port(properties.getPort())
			.heartbeatTimeout(properties.getHeartbeatTimeout())
			.readBufferSize(properties.getReadBufferSize())
			.maxBytesInMessage(properties.getMaxBytesInMessage())
			.bufferAllocator(properties.getBufferAllocator());
		if (properties.isDebug()) {
			serverCreator.debug();
		}
		MqttServerProperties.Ssl ssl = properties.getSsl();
		String keyStorePath = ssl.getKeyStorePath();
		String trustStorePath = ssl.getTrustStorePath();
		String password = ssl.getPassword();
		// ssl 配置
		if (StrUtil.isNotBlank(keyStorePath) && StrUtil.isNotBlank(trustStorePath) && StrUtil.isNotBlank(password)) {
			serverCreator.useSsl(keyStorePath, trustStorePath, password);
		}
		// bean 初始化
		authHandlerObjectProvider.ifAvailable(serverCreator::authHandler);
		messageDispatcherObjectProvider.ifAvailable(serverCreator::messageDispatcher);
		messageStoreObjectProvider.ifAvailable(serverCreator::messageStore);
		sessionManagerObjectProvider.ifAvailable(serverCreator::sessionManager);
		messageListenerObjectProvider.ifAvailable(serverCreator::messageListener);
		connectStatusListenerObjectProvider.ifAvailable(serverCreator::connectStatusListener);
		ipStatListenerObjectProvider.ifAvailable(serverCreator::ipStatListener);
		// 自定义处理
		customizers.ifAvailable((customizer) -> customizer.customize(serverCreator));
		// 消息监听器不能为 null
		Objects.requireNonNull(serverCreator.getMessageListener(), "Mqtt server IMqttMessageListener Bean not found.");
		enable=properties.isEnable();
	}

	@Override
	public void start() {
		if(enable) {
			mqttServerThread = new Thread(() -> {
				mqttServer = serverCreator.start();
				running = true;
			});
			mqttServerThread.setDaemon(true);
			mqttServerThread.start();
		}
	}

	@Override
	public void stop() {
		if(enable&&running) {
			if (mqttServer != null) {
				mqttServer.stop();
			}
			if (mqttServerThread != null) {
				mqttServerThread.interrupt();
			}
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

	@Override
	public int getPhase() {
		return DEFAULT_PHASE;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	/**
	 * 发布消息
	 *
	 * @param clientId clientId
	 * @param topic    topic
	 * @param payload  消息体
	 * @return 是否发送成功
	 */
	public Boolean publish(String clientId, String topic, ByteBuffer payload) {
		return mqttServer.publish(clientId,topic,payload);
	}

	/**
	 * 发布消息
	 *
	 * @param clientId clientId
	 * @param topic    topic
	 * @param payload  消息体
	 * @param qos      MqttQoS
	 * @return 是否发送成功
	 */
	public Boolean publish(String clientId, String topic, ByteBuffer payload, MqttQoS qos) {
		return mqttServer.publish(clientId,topic,payload,qos);
	}

	/**
	 * 发布消息
	 *
	 * @param clientId clientId
	 * @param topic    topic
	 * @param payload  消息体
	 * @param retain   是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public Boolean publish(String clientId, String topic, ByteBuffer payload, boolean retain) {
		return mqttServer.publish(clientId,topic,payload,retain);
	}

	/**
	 * 发布消息
	 *
	 * @param clientId clientId
	 * @param topic    topic
	 * @param payload  消息体
	 * @param qos      MqttQoS
	 * @param retain   是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public Boolean publish(String clientId, String topic, ByteBuffer payload, MqttQoS qos, boolean retain) {
		return mqttServer.publish(clientId,topic,payload,qos,retain);
	}

	/**
	 * 发布消息
	 *
	 * @param context ChannelContext
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public boolean publish(ChannelContext context, String clientId, String topic, ByteBuffer payload, MqttQoS qos, boolean retain) {
		return mqttServer.publish(context,clientId,topic,payload,qos,retain);
	}

	/**
	 * 发布消息给所以的在线设备
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @return 是否发送成功
	 */
	public Boolean publishAll(String topic, ByteBuffer payload) {
		return mqttServer.publishAll(topic,payload);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @return 是否发送成功
	 */
	public Boolean publishAll(String topic, ByteBuffer payload, MqttQoS qos) {
		return mqttServer.publishAll(topic,payload,qos);
	}

	/**
	 * 发布消息给所以的在线设备
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public Boolean publishAll(String topic, ByteBuffer payload, boolean retain) {
		return mqttServer.publishAll(topic,payload,retain);
	}

	/**
	 * 发布消息给所以的在线设备
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public Boolean publishAll(String topic, ByteBuffer payload, MqttQoS qos, boolean retain) {
		return mqttServer.publishAll(topic,payload,qos,retain);
	}
}
