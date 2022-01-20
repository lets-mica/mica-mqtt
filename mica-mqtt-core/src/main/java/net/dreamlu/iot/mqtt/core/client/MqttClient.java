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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.TioClient;
import org.tio.core.Tio;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
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
	private final ClientChannelContext context;
	private final IMqttClientSession clientSession;
	private final ScheduledThreadPoolExecutor executor;
	private final IMqttClientMessageIdGenerator messageIdGenerator;

	public static MqttClientCreator create() {
		return new MqttClientCreator();
	}

	MqttClient(TioClient tioClient,
			   MqttClientCreator config,
			   ClientChannelContext context,
			   ScheduledThreadPoolExecutor executor) {
		this.tioClient = tioClient;
		this.config = config;
		this.context = context;
		this.executor = executor;
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
		return subscribe(MqttQoS.AT_MOST_ONCE, topicFilter, listener);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos1(String topicFilter, IMqttClientMessageListener listener) {
		return subscribe(MqttQoS.AT_LEAST_ONCE, topicFilter, listener);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos2(String topicFilter, IMqttClientMessageListener listener) {
		return subscribe(MqttQoS.EXACTLY_ONCE, topicFilter, listener);
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
		return subscribe(Collections.singletonList(new MqttClientSubscription(mqttQoS, topicFilter, listener)));
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
		Objects.requireNonNull(topicFilters, "MQTT subscribe topicFilters is null.");
		List<MqttClientSubscription> subscriptionList = new ArrayList<>();
		for (String topicFilter : topicFilters) {
			subscriptionList.add(new MqttClientSubscription(mqttQoS, topicFilter, listener));
		}
		return subscribe(subscriptionList);
	}

	/**
	 * 批量订阅
	 *
	 * @param subscriptionList 订阅集合
	 * @return MqttClient
	 */
	public MqttClient subscribe(List<MqttClientSubscription> subscriptionList) {
		// 1. 先判断是否已经订阅过，重复订阅，直接跳出
		List<MqttClientSubscription> needSubscriptionList = new ArrayList<>();
		for (MqttClientSubscription subscription : subscriptionList) {
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
			.build();
		Boolean result = Tio.send(context, message);
		logger.info("MQTT subscriptionList:{} messageId:{} subscribing result:{}", needSubscriptionList, messageId, result);
		MqttPendingSubscription pendingSubscription = new MqttPendingSubscription(needSubscriptionList, message);
		pendingSubscription.startRetransmitTimer(executor, (msg) -> Tio.send(context, message));
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
		int messageId = messageIdGenerator.getId();
		MqttUnsubscribeMessage message = MqttMessageBuilders.unsubscribe()
			.addTopicFilters(topicFilters)
			.messageId(messageId)
			.build();
		MqttPendingUnSubscription pendingUnSubscription = new MqttPendingUnSubscription(topicFilters, message);
		Boolean result = Tio.send(context, message);
		logger.info("MQTT Topic:{} messageId:{} unSubscribing result:{}", topicFilters, messageId, result);
		// 解绑 subManage listener
		clientSession.addPaddingUnSubscribe(messageId, pendingUnSubscription);
		pendingUnSubscription.startRetransmissionTimer(executor, msg -> Tio.send(context, msg));
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
		boolean isHighLevelQoS = MqttQoS.AT_LEAST_ONCE == qos || MqttQoS.EXACTLY_ONCE == qos;
		int messageId = isHighLevelQoS ? messageIdGenerator.getId() : -1;
		if (payload == null) {
			payload = ByteBuffer.allocate(0);
		}
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
			clientSession.addPendingPublish(messageId, pendingPublish);
			pendingPublish.startPublishRetransmissionTimer(executor, msg -> Tio.send(context, msg));
		}
		return result;
	}

	/**
	 * 重连
	 */
	public void reconnect() {
		try {
			// 判断是否 removed
			if (context.isRemoved) {
				context.setRemoved(false);
			}
			tioClient.reconnect(context, config.getTimeout());
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
		boolean result = Tio.bSend(context, MqttMessage.DISCONNECT);
		if (result) {
			Tio.close(context, null, "MqttClient disconnect.", true);
		}
		return result;
	}

	/**
	 * 停止客户端
	 *
	 * @return 是否停止成功
	 */
	public boolean stop() {
		// 先断开连接
		this.disconnect();
		boolean result = tioClient.stop();
		logger.info("MqttClient stop result:{}", result);
		this.executor.shutdown();
		this.clientSession.clean();
		return result;
	}

	/**
	 * 获取 ClientChannelContext
	 *
	 * @return ClientChannelContext
	 */
	public ClientChannelContext getContext() {
		return context;
	}

	/**
	 * 判断客户端跟服务端是否断开连接
	 *
	 * @return 是否断连
	 */
	public boolean isDisconnected() {
		return context.isClosed;
	}

}
