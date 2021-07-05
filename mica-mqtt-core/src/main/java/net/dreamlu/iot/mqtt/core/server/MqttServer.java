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

package net.dreamlu.iot.mqtt.core.server;

import net.dreamlu.iot.mqtt.codec.MqttMessageBuilders;
import net.dreamlu.iot.mqtt.codec.MqttPublishMessage;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.common.MqttPendingPublish;
import net.dreamlu.iot.mqtt.core.server.store.IMqttSubscribeStore;
import net.dreamlu.iot.mqtt.core.server.store.SubscribeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.server.ServerTioConfig;
import org.tio.server.TioServer;
import org.tio.utils.lock.ReadLockHandler;
import org.tio.utils.lock.SetWithLock;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * mqtt 服务端
 *
 * @author L.cm
 */
public final class MqttServer {
	private static final Logger logger = LoggerFactory.getLogger(MqttServer.class);
	private final TioServer tioServer;
	private final IMqttMessageIdGenerator messageIdGenerator;
	private final IMqttPublishManager publishManager;
	private final IMqttSubscribeStore subscribeStore;
	private final ScheduledThreadPoolExecutor executor;

	MqttServer(TioServer tioServer,
			   IMqttMessageIdGenerator messageIdGenerator,
			   IMqttPublishManager publishManager,
			   IMqttSubscribeStore subscribeStore,
			   ScheduledThreadPoolExecutor executor) {
		this.tioServer = tioServer;
		this.messageIdGenerator = messageIdGenerator;
		this.publishManager = publishManager;
		this.subscribeStore = subscribeStore;
		this.executor = executor;
	}

	public static MqttServerCreator create() {
		return new MqttServerCreator();
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
	public Boolean publish(String clientId, String topic, ByteBuffer payload) {
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
	public Boolean publish(String clientId, String topic, ByteBuffer payload, MqttQoS qos) {
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
	public Boolean publish(String clientId, String topic, ByteBuffer payload, boolean retain) {
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
	public Boolean publish(String clientId, String topic, ByteBuffer payload, MqttQoS qos, boolean retain) {
		ChannelContext context = Tio.getByBsId(getServerConfig(), clientId);
		if (context == null) {
			logger.warn("Mqtt publish to clientId:{} ChannelContext is null May be disconnected.", clientId);
			return false;
		}
		List<SubscribeStore> subscribeList = subscribeStore.search(clientId, topic);
		if (subscribeList.isEmpty()) {
			logger.warn("Mqtt publish but clientId:{} subscribeList is empty.", clientId);
			return false;
		}
		for (SubscribeStore subscribe : subscribeList) {
			int subMqttQoS = subscribe.getMqttQoS();
			MqttQoS mqttQoS = qos.value() > subMqttQoS ? MqttQoS.valueOf(subMqttQoS) : qos;
			publish(context, topic, payload, mqttQoS, retain);
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
	private boolean publish(ChannelContext context, String topic, ByteBuffer payload, MqttQoS qos, boolean retain) {
		boolean isHighLevelQoS = MqttQoS.AT_LEAST_ONCE == qos || MqttQoS.EXACTLY_ONCE == qos;
		int messageId = isHighLevelQoS ? messageIdGenerator.getId() : -1;
		payload.rewind();
		MqttPublishMessage message = MqttMessageBuilders.publish()
			.topicName(topic)
			.payload(payload)
			.qos(qos)
			.retained(retain)
			.messageId(messageId)
			.build();
		Boolean result = Tio.send(context, message);
		logger.debug("MQTT publish topic:{} qos:{} retain:{} result:{}", topic, qos, retain, result);
		if (isHighLevelQoS) {
			MqttPendingPublish pendingPublish = new MqttPendingPublish(payload, message, qos);
			publishManager.addPendingPublish(messageId, pendingPublish);
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
	public Boolean publishAll(String topic, ByteBuffer payload) {
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
	public Boolean publishAll(String topic, ByteBuffer payload, MqttQoS qos) {
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
	public Boolean publishAll(String topic, ByteBuffer payload, boolean retain) {
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
	public Boolean publishAll(String topic, ByteBuffer payload, MqttQoS qos, boolean retain) {
		SetWithLock<ChannelContext> contextSet = Tio.getAll(getServerConfig());
		contextSet.handle((ReadLockHandler<Set<ChannelContext>>) channelContexts -> {
			if (channelContexts.isEmpty()) {
				logger.warn("Mqtt publish to all ChannelContext is empty.");
				return;
			}
			for (ChannelContext context : channelContexts) {
				String clientId = context.getBsId();
				publish(clientId, topic, payload, qos, retain);
			}
		});
		return true;
	}

	public boolean stop() {
		boolean result = this.tioServer.stop();
		logger.info("MqttServer stop result:{}", result);
		this.executor.shutdown();
		return result;
	}

}
