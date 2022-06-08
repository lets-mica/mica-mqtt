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

package net.dreamlu.iot.mqtt.core.client;

import net.dreamlu.iot.mqtt.codec.*;
import net.dreamlu.iot.mqtt.core.common.MqttPendingPublish;
import net.dreamlu.iot.mqtt.core.util.TopicUtil;
import net.dreamlu.iot.mqtt.core.util.timer.AckService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.TioClient;
import org.tio.client.TioClientConfig;
import org.tio.core.ChannelContext;
import org.tio.core.Node;
import org.tio.core.Tio;
import org.tio.utils.lock.SetWithLock;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * mqtt 客户端
 *
 * @author L.cm
 */
public final class MqttClient {
	private static final Logger logger = LoggerFactory.getLogger(MqttClient.class);
	private final TioClient tioClient;
	private final MqttClientCreator config;
	private final TioClientConfig clientTioConfig;
	private final IMqttClientSession clientSession;
	private final AckService ackService;
	private final IMqttClientMessageIdGenerator messageIdGenerator;
	private volatile ClientChannelContext context;

	public static MqttClientCreator create() {
		return new MqttClientCreator();
	}

	MqttClient(TioClient tioClient, MqttClientCreator config) {
		this.tioClient = tioClient;
		this.config = config;
		this.clientTioConfig = tioClient.getTioClientConfig();
		this.ackService = config.getAckService();
		this.clientSession = config.getClientSession();
		this.messageIdGenerator = config.getMessageIdGenerator();
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos0(String topicFilter, IMqttClientMessageListener listener) {
		return subscribe(topicFilter, MqttQoS.AT_MOST_ONCE, listener);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos1(String topicFilter, IMqttClientMessageListener listener) {
		return subscribe(topicFilter, MqttQoS.AT_LEAST_ONCE, listener);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos2(String topicFilter, IMqttClientMessageListener listener) {
		return subscribe(topicFilter, MqttQoS.EXACTLY_ONCE, listener);
	}

	/**
	 * 订阅
	 *
	 * @param mqttQoS     MqttQoS
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subscribe(MqttQoS mqttQoS, String topicFilter, IMqttClientMessageListener listener) {
		return subscribe(topicFilter, mqttQoS, listener, null);
	}

	/**
	 * 订阅
	 *
	 * @param mqttQoS     MqttQoS
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subscribe(String topicFilter, MqttQoS mqttQoS, IMqttClientMessageListener listener) {
		return subscribe(topicFilter, mqttQoS, listener, null);
	}

	/**
	 * 订阅
	 *
	 * @param mqttQoS     MqttQoS
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @param properties  MqttProperties
	 * @return MqttClient
	 */
	public MqttClient subscribe(String topicFilter, MqttQoS mqttQoS, IMqttClientMessageListener listener, MqttProperties properties) {
		return subscribe(Collections.singletonList(new MqttClientSubscription(mqttQoS, topicFilter, listener)), properties);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilters topicFilter 数组
	 * @param mqttQoS      MqttQoS
	 * @param listener     MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subscribe(String[] topicFilters, MqttQoS mqttQoS, IMqttClientMessageListener listener) {
		return subscribe(topicFilters, mqttQoS, listener, null);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilters topicFilter 数组
	 * @param mqttQoS      MqttQoS
	 * @param listener     MqttMessageListener
	 * @param properties   MqttProperties
	 * @return MqttClient
	 */
	public MqttClient subscribe(String[] topicFilters, MqttQoS mqttQoS, IMqttClientMessageListener listener, MqttProperties properties) {
		Objects.requireNonNull(topicFilters, "MQTT subscribe topicFilters is null.");
		List<MqttClientSubscription> subscriptionList = new ArrayList<>();
		for (String topicFilter : topicFilters) {
			subscriptionList.add(new MqttClientSubscription(mqttQoS, topicFilter, listener));
		}
		return subscribe(subscriptionList, properties);
	}

	/**
	 * 批量订阅
	 *
	 * @param subscriptionList 订阅集合
	 * @return MqttClient
	 */
	public MqttClient subscribe(List<MqttClientSubscription> subscriptionList) {
		return subscribe(subscriptionList, null);
	}

	/**
	 * 批量订阅
	 *
	 * @param subscriptionList 订阅集合
	 * @param properties       MqttProperties
	 * @return MqttClient
	 */
	public MqttClient subscribe(List<MqttClientSubscription> subscriptionList, MqttProperties properties) {
		// 1. 先判断是否已经订阅过，重复订阅，直接跳出
		List<MqttClientSubscription> needSubscriptionList = new ArrayList<>();
		for (MqttClientSubscription subscription : subscriptionList) {
			// 校验 topicFilter
			TopicUtil.validateTopicFilter(subscription.getTopicFilter());
			boolean subscribed = clientSession.isSubscribed(subscription);
			if (!subscribed) {
				needSubscriptionList.add(subscription);
			}
		}
		// 2. 已经订阅的跳出
		if (needSubscriptionList.isEmpty()) {
			return this;
		}
		List<MqttTopicSubscription> topicSubscriptionList = needSubscriptionList.stream()
			.map(MqttClientSubscription::toTopicSubscription)
			.collect(Collectors.toList());
		// 3. 没有订阅过
		int messageId = messageIdGenerator.getId();
		MqttSubscribeMessage message = MqttMessageBuilders.subscribe()
			.addSubscriptions(topicSubscriptionList)
			.messageId(messageId)
			.properties(properties)
			.build();
		Boolean result = Tio.send(getContext(), message);
		logger.info("MQTT subscriptionList:{} messageId:{} subscribing result:{}", needSubscriptionList, messageId, result);
		MqttPendingSubscription pendingSubscription = new MqttPendingSubscription(needSubscriptionList, message);
		pendingSubscription.startRetransmitTimer(ackService, (msg) -> Tio.send(getContext(), message));
		clientSession.addPaddingSubscribe(messageId, pendingSubscription);
		return this;
	}

	/**
	 * 取消订阅
	 *
	 * @param topicFilters topicFilter 集合
	 * @return MqttClient
	 */
	public MqttClient unSubscribe(String... topicFilters) {
		return unSubscribe(Arrays.asList(topicFilters));
	}

	/**
	 * 取消订阅
	 *
	 * @param topicFilters topicFilter 集合
	 * @return MqttClient
	 */
	public MqttClient unSubscribe(List<String> topicFilters) {
		// 1. 校验 topicFilter
		TopicUtil.validateTopicFilter(topicFilters);
		// 2. 优先取消本地订阅
		clientSession.removePaddingSubscribes(topicFilters);
		clientSession.removeSubscriptions(topicFilters);
		// 3. 发送取消订阅到服务端
		int messageId = messageIdGenerator.getId();
		MqttUnsubscribeMessage message = MqttMessageBuilders.unsubscribe()
			.addTopicFilters(topicFilters)
			.messageId(messageId)
			.build();
		MqttPendingUnSubscription pendingUnSubscription = new MqttPendingUnSubscription(topicFilters, message);
		Boolean result = Tio.send(getContext(), message);
		logger.info("MQTT Topic:{} messageId:{} unSubscribing result:{}", topicFilters, messageId, result);
		// 4. 启动取消订阅线程
		clientSession.addPaddingUnSubscribe(messageId, pendingUnSubscription);
		pendingUnSubscription.startRetransmissionTimer(ackService, msg -> Tio.send(getContext(), msg));
		return this;
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, ByteBuffer payload) {
		return publish(topic, payload, MqttQoS.AT_MOST_ONCE);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息内容
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, byte[] payload) {
		return publish(topic, payload, MqttQoS.AT_MOST_ONCE);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, ByteBuffer payload, MqttQoS qos) {
		return publish(topic, payload, qos, false);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息内容
	 * @param qos     MqttQoS
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, byte[] payload, MqttQoS qos) {
		return publish(topic, payload, qos, false);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, ByteBuffer payload, boolean retain) {
		return publish(topic, payload, MqttQoS.AT_MOST_ONCE, retain);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息内容
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, byte[] payload, boolean retain) {
		return publish(topic, payload, MqttQoS.AT_MOST_ONCE, retain);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, byte[] payload, MqttQoS qos, boolean retain) {
		return publish(topic, payload == null ? null : ByteBuffer.wrap(payload), qos, retain);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, ByteBuffer payload, MqttQoS qos, boolean retain) {
		return publish(topic, payload, qos, (publishBuilder) -> publishBuilder.retained(retain));
	}

	/**
	 * 发布消息
	 *
	 * @param topic      topic
	 * @param payload    消息体
	 * @param qos        MqttQoS
	 * @param retain     是否在服务器上保留消息
	 * @param properties MqttProperties
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, ByteBuffer payload, MqttQoS qos, boolean retain, MqttProperties properties) {
		return publish(topic, payload, qos, (publishBuilder) -> publishBuilder.retained(retain).properties(properties));
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @param builder PublishBuilder
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, ByteBuffer payload, MqttQoS qos, Consumer<MqttMessageBuilders.PublishBuilder> builder) {
		// 校验 topic
		TopicUtil.validateTopicName(topic);
		// qos 判断
		boolean isHighLevelQoS = MqttQoS.AT_LEAST_ONCE == qos || MqttQoS.EXACTLY_ONCE == qos;
		int messageId = isHighLevelQoS ? messageIdGenerator.getId() : -1;
		if (payload == null) {
			payload = ByteBuffer.allocate(0);
		}
		MqttMessageBuilders.PublishBuilder publishBuilder = MqttMessageBuilders.publish();
		// 自定义配置
		builder.accept(publishBuilder);
		// 内置
		publishBuilder.topicName(topic)
			.payload(payload)
			.messageId(messageId)
			.qos(qos);
		MqttPublishMessage message = publishBuilder.build();
		boolean result = Tio.send(getContext(), message);
		logger.debug("MQTT Topic:{} qos:{} retain:{} publish result:{}", topic, qos, publishBuilder.isRetained(), result);
		if (isHighLevelQoS) {
			MqttPendingPublish pendingPublish = new MqttPendingPublish(payload, message, qos);
			clientSession.addPendingPublish(messageId, pendingPublish);
			pendingPublish.startPublishRetransmissionTimer(ackService, msg -> Tio.send(getContext(), msg));
		}
		return result;
	}

	/**
	 * 异步连接
	 *
	 * @return TioClient
	 */
	MqttClient start(boolean sync) {
		// 1. 启动 ack service
		ackService.start();
		// 2. 启动 tio
		Node node = new Node(config.getIp(), config.getPort());
		try {
			if (sync) {
				this.tioClient.connect(node, config.getTimeout());
			} else {
				this.tioClient.asynConnect(node, config.getTimeout());
			}
			return this;
		} catch (Exception e) {
			throw new IllegalStateException("Mica mqtt client async start fail.", e);
		}
	}

	/**
	 * 重连
	 */
	public void reconnect() {
		ClientChannelContext channelContext = getContext();
		if (channelContext == null) {
			return;
		}
		try {
			// 判断是否 removed
			if (channelContext.isRemoved) {
				channelContext.setRemoved(false);
			}
			tioClient.reconnect(channelContext, config.getTimeout());
		} catch (Exception e) {
			logger.error("mqtt client reconnect error", e);
		}
	}

	/**
	 * 断开 mqtt 连接
	 *
	 * @return 是否成功
	 */
	public boolean disconnect() {
		ClientChannelContext channelContext = getContext();
		if (channelContext == null) {
			return false;
		}
		boolean result = Tio.bSend(channelContext, MqttMessage.DISCONNECT);
		if (result) {
			Tio.close(channelContext, null, "MqttClient disconnect.", true);
		}
		return result;
	}

	/**
	 * 停止客户端
	 *
	 * @return 是否停止成功
	 */
	public boolean stop() {
		// 1. 先停止 ack 服务
		this.ackService.stop();
		// 2. 断开连接
		this.disconnect();
		// 3. 停止 tio
		boolean result = tioClient.stop();
		logger.info("MqttClient stop result:{}", result);
		// 4. 清理 session
		this.clientSession.clean();
		return result;
	}

	/**
	 * 获取 TioClient
	 *
	 * @return TioClient
	 */
	public TioClient getTioClient() {
		return tioClient;
	}

	/**
	 * 获取配置
	 *
	 * @return MqttClientCreator
	 */
	public MqttClientCreator getClientCreator() {
		return config;
	}

	/**
	 * 获取 ClientTioConfig
	 *
	 * @return ClientTioConfig
	 */
	public TioClientConfig getClientTioConfig() {
		return clientTioConfig;
	}

	/**
	 * 获取 ClientChannelContext
	 *
	 * @return ClientChannelContext
	 */
	public ClientChannelContext getContext() {
		if (context != null) {
			return context;
		}
		SetWithLock<ChannelContext> connectedSet = Tio.getConnecteds(clientTioConfig);
		Set<ChannelContext> contextSet = connectedSet.getObj();
		if (contextSet == null || contextSet.isEmpty()) {
			return null;
		}
		this.context = (ClientChannelContext) contextSet.iterator().next();
		return this.context;
	}

	/**
	 * 判断客户端跟服务端是否连接
	 *
	 * @return 是否已经连接成功
	 */
	public boolean isConnected() {
		ClientChannelContext channelContext = getContext();
		return channelContext != null && !channelContext.isClosed;
	}

	/**
	 * 判断客户端跟服务端是否断开连接
	 *
	 * @return 是否断连
	 */
	public boolean isDisconnected() {
		return !isConnected();
	}

}
