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

import net.dreamlu.iot.mqtt.codec.*;
import net.dreamlu.iot.mqtt.core.common.MqttMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.TioClient;
import org.tio.core.Tio;

import java.nio.ByteBuffer;

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
	private final MqttClientSubscriptionManager subscriptionManager;

	public static MqttClientCreator create() {
		return new MqttClientCreator();
	}

	MqttClient(TioClient tioClient,
			   MqttClientCreator config,
			   ClientChannelContext context,
			   MqttClientSubscriptionManager subscriptionManager) {
		this.tioClient = tioClient;
		this.config = config;
		this.context = context;
		this.subscriptionManager = subscriptionManager;
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos0(String topicFilter, MqttMessageListener listener) {
		return subscribe(MqttQoS.AT_MOST_ONCE, topicFilter, listener);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos1(String topicFilter, MqttMessageListener listener) {
		return subscribe(MqttQoS.AT_LEAST_ONCE, topicFilter, listener);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos2(String topicFilter, MqttMessageListener listener) {
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
	public MqttClient subscribe(MqttQoS mqttQoS, String topicFilter, MqttMessageListener listener) {
		int messageId = MqttClientMessageId.getId();
		MqttSubscribeMessage message = MqttMessageBuilders.subscribe()
			.addSubscription(mqttQoS, topicFilter)
			.messageId(messageId)
			.build();
		Boolean result = Tio.send(context, message);
		logger.debug("MQTT subscribe topicFilter:{} mqttQoS:{} messageId:{} result:{}", topicFilter, mqttQoS, messageId, result);
		// 先 tio 发送数据才添加，考虑会不会 ack 早回？？？
		if (Boolean.TRUE.equals(result)) {
			subscriptionManager.addPaddingSubscribe(new MqttSubscription(messageId, mqttQoS, topicFilter, listener));
		}
		return this;
	}

	/**
	 * 取消订阅
	 *
	 * @param topicFilter topicFilter
	 * @return MqttClient
	 */
	public MqttClient unSubscribe(String topicFilter) {
		int messageId = MqttClientMessageId.getId();
		MqttUnsubscribeMessage message = MqttMessageBuilders.unsubscribe()
			.addTopicFilter(topicFilter)
			.messageId(MqttClientMessageId.getId())
			.build();
		Boolean result = Tio.send(context, message);
		logger.debug("MQTT unSubscribe topicFilter:{} messageId:{} result:{}", topicFilter, messageId, result);
		// 解绑 subManage listener
		if (Boolean.TRUE.equals(result)) {
			subscriptionManager.addPaddingUnSubscribe(messageId, topicFilter);
		}
		return this;
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @return 是否发送成功
	 */
	public Boolean publish(String topic, ByteBuffer payload) {
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
	public Boolean publish(String topic, ByteBuffer payload, MqttQoS qos) {
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
	public Boolean publish(String topic, ByteBuffer payload, boolean retain) {
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
	public Boolean publish(String topic, ByteBuffer payload, MqttQoS qos, boolean retain) {
		MqttPublishMessage message = MqttMessageBuilders.publish()
			.topicName(topic)
			.payload(payload)
			.qos(qos)
			.retained(retain)
			.messageId(MqttClientMessageId.getId())
			.build();
		logger.debug("MQTT publish topic:{} qos:{} retain:{}", topic, qos, retain);
		return Tio.send(context, message);
	}

	/**
	 * 重连
	 *
	 * @throws Exception 异常
	 */
	public MqttClient reconnect() throws Exception {
		tioClient.reconnect(context, config.getTimeout());
		return this;
	}

	/**
	 * 断开 mqtt 连接
	 */
	public boolean disconnect() {
		boolean result = Tio.send(context, MqttMessage.DISCONNECT);
		if (result) {
			Tio.close(context, "MqttClient disconnect.");
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
		return tioClient.stop();
	}

	/**
	 * 获取 ClientChannelContext
	 *
	 * @return ClientChannelContext
	 */
	public ClientChannelContext getContext() {
		return context;
	}

}
