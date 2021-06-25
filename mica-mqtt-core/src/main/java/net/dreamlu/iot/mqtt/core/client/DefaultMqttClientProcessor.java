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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * 默认的 mqtt 消息处理器
 *
 * @author L.cm
 */
public class DefaultMqttClientProcessor implements MqttClientProcessor {
	private static final Logger logger = LoggerFactory.getLogger(DefaultMqttClientProcessor.class);
	private final MqttClientSubscriptionManager subscriptionManager;
	private final CountDownLatch connLatch;
	private final ScheduledThreadPoolExecutor executor;

	public DefaultMqttClientProcessor(MqttClientSubscriptionManager subscriptionManager,
									  CountDownLatch connLatch,
									  ScheduledThreadPoolExecutor executor) {
		this.subscriptionManager = subscriptionManager;
		this.connLatch = connLatch;
		this.executor = executor;
	}

	@Override
	public void processDecodeFailure(ChannelContext context, MqttMessage message, Throwable ex) {
		// 客户端失败，默认记录异常日志
		logger.error(ex.getMessage(), ex);
	}

	@Override
	public void processConAck(ChannelContext context, MqttConnAckMessage message) {
		MqttConnectReturnCode returnCode = message.variableHeader().connectReturnCode();
		switch (returnCode) {
			case CONNECTION_ACCEPTED:
				connLatch.countDown();
				logger.info("MqttClient connection succeeded!");
				break;
			case CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD:
			case CONNECTION_REFUSED_IDENTIFIER_REJECTED:
			case CONNECTION_REFUSED_NOT_AUTHORIZED:
			case CONNECTION_REFUSED_SERVER_UNAVAILABLE:
			case CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION:
			default:
				String remark = "MqttClient connect error error ReturnCode:" + returnCode;
				Tio.close(context, remark);
				throw new IllegalStateException(remark);
		}
	}

	@Override
	public void processSubAck(MqttSubAckMessage message) {
		int messageId = message.variableHeader().messageId();
		logger.debug("MqttClient SubAck messageId:{}", messageId);
		MqttPendingSubscription paddingSubscribe = subscriptionManager.getPaddingSubscribe(messageId);
		if (paddingSubscribe == null) {
			return;
		}
		paddingSubscribe.onSubAckReceived();
		subscriptionManager.removePaddingSubscribe(messageId);
		subscriptionManager.addSubscription(paddingSubscribe.toSubscription());
	}

	@Override
	public void processPublish(ChannelContext context, MqttPublishMessage message) {
		MqttFixedHeader mqttFixedHeader = message.fixedHeader();
		MqttPublishVariableHeader variableHeader = message.variableHeader();
		String topicName = variableHeader.topicName();
		MqttQoS mqttQoS = mqttFixedHeader.qosLevel();
		int packetId = variableHeader.packetId();
		logger.debug("MqttClient received publish topic:{} qoS:{} packetId:{}", topicName, mqttQoS, packetId);
		switch (mqttFixedHeader.qosLevel()) {
			case AT_MOST_ONCE:
				invokeListenerForPublish(topicName, message);
				break;
			case AT_LEAST_ONCE:
				invokeListenerForPublish(topicName, message);
				if (packetId != -1) {
					MqttMessage messageAck = MqttMessageBuilders.pubAck()
						.packetId(packetId)
						.build();
					Tio.send(context, messageAck);
				}
				break;
			case EXACTLY_ONCE:
				if (packetId != -1) {
					MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0);
					MqttMessage pubRecMessage = new MqttMessage(fixedHeader, MqttMessageIdVariableHeader.from(packetId));
					MqttPendingQos2Publish pendingQos2Publish = new MqttPendingQos2Publish(message, pubRecMessage);
					subscriptionManager.addPendingQos2Publish(packetId, pendingQos2Publish);
					message.payload().clear();
					pendingQos2Publish.startPubRecRetransmitTimer(executor, msg -> Tio.send(context, msg));
				}
				break;
			case FAILURE:
			default:
		}
	}

	@Override
	public void processUnSubAck(MqttUnsubAckMessage message) {
		int messageId = message.variableHeader().messageId();
		logger.debug("MqttClient UnSubAck messageId:{}", messageId);
		MqttPendingUnSubscription pendingUnSubscription = subscriptionManager.getPaddingUnSubscribe(messageId);
		if (pendingUnSubscription == null) {
			return;
		}
		pendingUnSubscription.onUnSubAckReceived();
		subscriptionManager.removePaddingUnSubscribe(messageId);
		subscriptionManager.removeSubscriptions(pendingUnSubscription.getTopic());
	}

	@Override
	public void processPubAck(MqttPubAckMessage message) {
		int messageId = message.variableHeader().messageId();
		logger.debug("MqttClient PubAck messageId:{}", messageId);
		MqttPendingPublish pendingPublish = subscriptionManager.getPendingPublish(messageId);
		if (pendingPublish == null) {
			return;
		}
		pendingPublish.onPubAckReceived();
		subscriptionManager.removePendingPublish(messageId);
		pendingPublish.getPayload().clear();
	}

	@Override
	public void processPubRec(ChannelContext context, MqttMessage message) {
		int messageId = ((MqttMessageIdVariableHeader) message.variableHeader()).messageId();
		logger.debug("MqttClient PubRec messageId:{}", messageId);
		MqttPendingPublish pendingPublish = subscriptionManager.getPendingPublish(messageId);
		pendingPublish.onPubAckReceived();

		MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_LEAST_ONCE, false, 0);
		MqttMessageIdVariableHeader variableHeader = (MqttMessageIdVariableHeader) message.variableHeader();
		MqttMessage pubRelMessage = new MqttMessage(fixedHeader, variableHeader);
		Tio.send(context, pubRelMessage);

		pendingPublish.setPubRelMessage(pubRelMessage);
		pendingPublish.startPubRelRetransmissionTimer(executor, msg -> Tio.send(context, msg));
	}

	@Override
	public void processPubRel(ChannelContext context, MqttMessage message) {
		int messageId = ((MqttMessageIdVariableHeader) message.variableHeader()).messageId();
		logger.debug("MqttClient PubRel messageId:{}", messageId);
		MqttPendingQos2Publish pendingQos2Publish = subscriptionManager.getPendingQos2Publish(messageId);
		if (pendingQos2Publish != null) {
			MqttPublishMessage incomingPublish = pendingQos2Publish.getIncomingPublish();
			String topicName = incomingPublish.variableHeader().topicName();
			this.invokeListenerForPublish(topicName, incomingPublish);
			pendingQos2Publish.onPubRelReceived();
			subscriptionManager.removePendingQos2Publish(messageId);
		}
		MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0);
		MqttMessageIdVariableHeader variableHeader = MqttMessageIdVariableHeader.from(messageId);
		Tio.send(context, new MqttMessage(fixedHeader, variableHeader));
	}

	@Override
	public void processPubComp(MqttMessage message) {
		int messageId = ((MqttMessageIdVariableHeader) message.variableHeader()).messageId();
		MqttPendingPublish pendingPublish = subscriptionManager.getPendingPublish(messageId);
		pendingPublish.getPayload().clear();
		pendingPublish.onPubCompReceived();
		subscriptionManager.removePendingPublish(messageId);
	}

	/**
	 * 处理订阅的消息
	 *
	 * @param topicName topicName
	 * @param message   MqttPublishMessage
	 */
	private void invokeListenerForPublish(String topicName, MqttPublishMessage message) {
		List<MqttSubscription> subscriptionList = subscriptionManager.getMatchedSubscription(topicName);
		subscriptionList.forEach(subscription -> subscription.getListener().onMessage(topicName, message.payload()));
	}

}
