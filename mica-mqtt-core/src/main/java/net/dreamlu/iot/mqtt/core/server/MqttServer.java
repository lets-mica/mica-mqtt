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

import net.dreamlu.iot.mqtt.codec.MqttMessageBuilders;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.common.MqttPendingPublish;
import net.dreamlu.iot.mqtt.core.server.model.Subscribe;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.server.ServerTioConfig;
import org.tio.server.TioServer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * mqtt 服务端
 *
 * @author L.cm
 */
public final class MqttServer {
	private static final Logger logger = LoggerFactory.getLogger(MqttServer.class);
	private final TioServer tioServer;
	private final IMqttSessionManager sessionManager;
	private final ScheduledThreadPoolExecutor executor;

	public MqttServer(TioServer tioServer,
					  MqttServerCreator serverCreator,
					  ScheduledThreadPoolExecutor executor) {
		this.tioServer = tioServer;
		this.sessionManager = serverCreator.getSessionManager();
		this.executor = executor;
	}

	public static MqttServerCreator create() {
		return new MqttServerCreator();
	}

	/**
	 * 获取 TioServer
	 *
	 * @return TioServer
	 */
	public TioServer getTioServer() {
		return this.tioServer;
	}

	/**
	 * 获取 ServerTioConfig
	 *
	 * @return the serverTioConfig
	 */
	public ServerTioConfig getServerConfig() {
		return this.tioServer.getServerTioConfig();
	}

	/**
	 * 发布消息
	 *
	 * @param clientId clientId
	 * @param topic    topic
	 * @param payload  消息体
	 * @return 是否发送成功
	 */
	public boolean publish(String clientId, String topic, ByteBuffer payload) {
		return publish(clientId, topic, payload, MqttQoS.AT_MOST_ONCE);
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
	public boolean publish(String clientId, String topic, ByteBuffer payload, MqttQoS qos) {
		return publish(clientId, topic, payload, qos, false);
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
	public boolean publish(String clientId, String topic, ByteBuffer payload, boolean retain) {
		return publish(clientId, topic, payload, MqttQoS.AT_MOST_ONCE, retain);
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
	public boolean publish(String clientId, String topic, ByteBuffer payload, MqttQoS qos, boolean retain) {
		ChannelContext context = Tio.getByBsId(getServerConfig(), clientId);
		if (context == null || context.isClosed) {
			logger.warn("Mqtt Topic:{} publish to clientId:{} ChannelContext is null may be disconnected.", topic, clientId);
			return false;
		}
		List<Subscribe> subscribeList = sessionManager.searchSubscribe(topic, clientId);
		if (subscribeList.isEmpty()) {
			logger.warn("Mqtt Topic:{} publish but clientId:{} subscribeList is empty.", topic, clientId);
			return false;
		}
		for (Subscribe subscribe : subscribeList) {
			int subMqttQoS = subscribe.getMqttQoS();
			MqttQoS mqttQoS = qos.value() > subMqttQoS ? MqttQoS.valueOf(subMqttQoS) : qos;
			publish(context, clientId, topic, payload, mqttQoS, retain);
		}
		return true;
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
		boolean isHighLevelQoS = MqttQoS.AT_LEAST_ONCE == qos || MqttQoS.EXACTLY_ONCE == qos;
		int messageId = isHighLevelQoS ? sessionManager.getMessageId(clientId) : -1;
		payload.rewind();
		MqttPublishMessage message = MqttMessageBuilders.publish()
			.topicName(topic)
			.payload(payload)
			.qos(qos)
			.retained(retain)
			.messageId(messageId)
			.build();
		boolean result = Tio.send(context, message);
		logger.info("MQTT Topic:{} qos:{} retain:{} publish result:{}", topic, qos, retain, result);
		if (isHighLevelQoS) {
			MqttPendingPublish pendingPublish = new MqttPendingPublish(payload, message, qos);
			sessionManager.addPendingPublish(clientId, messageId, pendingPublish);
			pendingPublish.startPublishRetransmissionTimer(executor, msg -> Tio.send(context, msg));
		}
		return result;
	}

	/**
	 * 发布消息给所以的在线设备
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @return 是否发送成功
	 */
	public boolean publishAll(String topic, ByteBuffer payload) {
		return publishAll(topic, payload, MqttQoS.AT_MOST_ONCE);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @return 是否发送成功
	 */
	public boolean publishAll(String topic, ByteBuffer payload, MqttQoS qos) {
		return publishAll(topic, payload, qos, false);
	}

	/**
	 * 发布消息给所以的在线设备
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public boolean publishAll(String topic, ByteBuffer payload, boolean retain) {
		return publishAll(topic, payload, MqttQoS.AT_MOST_ONCE, retain);
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
	public boolean publishAll(String topic, ByteBuffer payload, MqttQoS qos, boolean retain) {
		// 查找订阅该 topic 的客户端
		List<Subscribe> subscribeList = sessionManager.searchSubscribe(topic);
		if (subscribeList.isEmpty()) {
			logger.warn("Mqtt Topic:{} publishAll but subscribe client list is empty.", topic);
			return false;
		}
		for (Subscribe subscribe : subscribeList) {
			String clientId = subscribe.getClientId();
			ChannelContext context = Tio.getByBsId(getServerConfig(), clientId);
			if (context == null || context.isClosed) {
				logger.warn("Mqtt Topic:{} publish to clientId:{} channel is null may be disconnected.", topic, clientId);
				continue;
			}
			int subMqttQoS = subscribe.getMqttQoS();
			MqttQoS mqttQoS = qos.value() > subMqttQoS ? MqttQoS.valueOf(subMqttQoS) : qos;
			publish(context, clientId, topic, payload, mqttQoS, retain);
		}
		return true;
	}

	public boolean stop() {
		boolean result = this.tioServer.stop();
		logger.info("MqttServer stop result:{}", result);
		try {
			sessionManager.clean();
		} catch (Throwable e) {
			logger.error("MqttServer stop session clean error.", e);
		}
		this.executor.shutdown();
		return result;
	}

}
