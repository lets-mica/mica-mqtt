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

/**
 * 默认的 mqtt 消息处理器
 *
 * @author L.cm
 */
public class DefaultMqttClientProcessor implements MqttClientProcessor {
	private static final Logger logger = LoggerFactory.getLogger(DefaultMqttClientProcessor.class);
	private final MqttClientSubscriptionManager subscriptionManager;
	private final CountDownLatch connLatch;

	public DefaultMqttClientProcessor(MqttClientSubscriptionManager subscriptionManager,
									  CountDownLatch connLatch) {
		this.subscriptionManager = subscriptionManager;
		this.connLatch = connLatch;
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
				logger.info("MQTT 连接成功！");
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
		logger.debug("MQTT SubAck messageId:{}", messageId);
		MqttSubscription paddingSubscribe = subscriptionManager.getPaddingSubscribe(messageId);
		if (paddingSubscribe == null) {
			return;
		}
		subscriptionManager.addSubscription(paddingSubscribe);
	}

	@Override
	public void processPublish(ChannelContext context, MqttPublishMessage message) {
		MqttFixedHeader mqttFixedHeader = message.fixedHeader();
		MqttPublishVariableHeader variableHeader = message.variableHeader();
		String topicName = variableHeader.topicName();
		MqttQoS mqttQoS = mqttFixedHeader.qosLevel();
		int packetId = variableHeader.packetId();
		logger.debug("MQTT received publish topic:{} qoS:{} packetId:{}", topicName, mqttQoS, packetId);
		switch (mqttFixedHeader.qosLevel()) {
			case AT_MOST_ONCE:
				List<MqttSubscription> subscriptionList = subscriptionManager.getMatchedSubscription(topicName);
				subscriptionList.forEach(subscription -> subscription.getListener().onMessage(topicName, message.payload()));
				break;
			case AT_LEAST_ONCE:
				break;
			case EXACTLY_ONCE:
				break;
			case FAILURE:
			default:
		}
	}

	@Override
	public void processUnSubAck(MqttUnsubAckMessage message) {
		int messageId = message.variableHeader().messageId();
		String topicFilter = subscriptionManager.getPaddingUnSubscribe(messageId);
		logger.debug("MQTT UnSubAck messageId:{} topicFilter:{}", messageId, topicFilter);
		if (topicFilter == null) {
			return;
		}
		subscriptionManager.removeSubscriptions(topicFilter);
	}

	@Override
	public void processPubAck(MqttPubAckMessage message) {
		int messageId = message.variableHeader().messageId();
		System.out.println(message);
	}

	@Override
	public void processPubRec(ChannelContext context, MqttMessage message) {
		System.out.println(message);
	}

	@Override
	public void processPubRel(ChannelContext context, MqttMessage message) {
		System.out.println(message);
	}

	@Override
	public void processPubComp(MqttMessage message) {
		System.out.println(message);
	}

}
