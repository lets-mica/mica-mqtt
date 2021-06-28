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

package net.dreamlu.iot.mqtt.server;

import net.dreamlu.iot.mqtt.codec.*;
import net.dreamlu.iot.mqtt.core.common.MqttPendingQos2Publish;
import net.dreamlu.iot.mqtt.core.common.MqttMessageListener;
import net.dreamlu.iot.mqtt.core.common.MqttSubscription;
import net.dreamlu.iot.mqtt.core.server.IMqttAuthHandler;
import net.dreamlu.iot.mqtt.core.server.IMqttMessageIdGenerator;
import net.dreamlu.iot.mqtt.core.server.MqttServerProcessor;
import net.dreamlu.iot.mqtt.core.server.IMqttSubManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.utils.hutool.StrUtil;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * mqtt broker 处理器
 *
 * @author L.cm
 */
public class MqttServerProcessorImpl implements MqttServerProcessor {
	private static final Logger logger = LoggerFactory.getLogger(MqttServerProcessorImpl.class);
	private final IMqttAuthHandler authHandler;
	private final IMqttMessageIdGenerator messageIdGenerator;
	private final IMqttSubManager subManager;
	private final ScheduledThreadPoolExecutor executor;

	public MqttServerProcessorImpl(IMqttAuthHandler authHandler,
								   IMqttSubManager subManager,
								   IMqttMessageIdGenerator messageIdGenerator,
								   ScheduledThreadPoolExecutor executor) {
		this.authHandler = authHandler;
		this.subManager = subManager;
		this.messageIdGenerator = messageIdGenerator;
		this.executor = executor;
	}

	@Override
	public void processConnect(ChannelContext context, MqttConnectMessage mqttMessage) {
		MqttConnectPayload payload = mqttMessage.payload();
		String clientId = payload.clientIdentifier();
		// 1. 客户端必须提供 clientId, 不管 cleanSession 是否为1, 此处没有参考标准协议实现
		if (StrUtil.isBlank(clientId)) {
			connAckByReturnCode(clientId, context, MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED);
			return;
		}
		// 2. 认证
		String userName = payload.userName();
		String password = payload.password();
		if (!authHandler.authenticate(clientId, userName, password)) {
			connAckByReturnCode(clientId, context, MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD);
			return;
		}
		// 3. 设置 clientId
		context.setBsId(clientId);
		Tio.bindBsId(context, clientId);
		// 4. 返回 ack
		connAckByReturnCode(clientId, context, MqttConnectReturnCode.CONNECTION_ACCEPTED);
	}

	private void connAckByReturnCode(String clientId, ChannelContext context, MqttConnectReturnCode returnCode) {
		MqttConnAckMessage message = MqttMessageBuilders.connAck()
			.returnCode(returnCode)
			.sessionPresent(false)
			.build();
		Tio.send(context, message);
		logger.debug("Connect ack send - clientId: {} returnCode:{}", clientId, returnCode);
	}

	@Override
	public void processPublish(ChannelContext context, MqttPublishMessage message) {
		String clientId = context.getBsId();
		MqttFixedHeader fixedHeader = message.fixedHeader();
		MqttQoS mqttQoS = fixedHeader.qosLevel();
		MqttPublishVariableHeader variableHeader = message.variableHeader();
		String topicName = variableHeader.topicName();
		int packetId = variableHeader.packetId();
		logger.debug("Publish - clientId: {} topicName:{} mqttQoS:{} packetId:{}", clientId, topicName, mqttQoS, packetId);
		switch (mqttQoS) {
			case AT_MOST_ONCE:
				invokeListenerForPublish(mqttQoS, topicName, message);
				break;
			case AT_LEAST_ONCE:
				invokeListenerForPublish(mqttQoS, topicName, message);
				if (packetId != -1) {
					MqttMessage messageAck = MqttMessageBuilders.pubAck()
						.packetId(packetId)
						.build();
					Tio.send(context, messageAck);
				}
				break;
			case EXACTLY_ONCE:
				if (packetId != -1) {
					MqttFixedHeader pubRecFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0);
					MqttMessage pubRecMessage = new MqttMessage(pubRecFixedHeader, MqttMessageIdVariableHeader.from(packetId));
					MqttPendingQos2Publish pendingQos2Publish = new MqttPendingQos2Publish(message, pubRecMessage);
//					subscriptionManager.addPendingQos2Publish(packetId, pendingQos2Publish);
					pendingQos2Publish.startPubRecRetransmitTimer(executor, msg -> Tio.send(context, msg));
				}
				break;
			case FAILURE:
			default:
		}
	}

	@Override
	public void processPubAck(ChannelContext context, MqttMessageIdVariableHeader variableHeader) {
		int messageId = variableHeader.messageId();
		String clientId = context.getBsId();
		logger.debug("PubAck - clientId: {}, messageId: {}", clientId, messageId);
	}

	@Override
	public void processPubRec(ChannelContext context, MqttMessageIdVariableHeader variableHeader) {
		String clientId = context.getBsId();
		int messageId = variableHeader.messageId();
		logger.debug("PubRec - clientId: {}, messageId: {}", clientId, messageId);
		MqttMessage message = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(messageId), null);
		Tio.send(context, message);
	}

	@Override
	public void processPubRel(ChannelContext context, MqttMessageIdVariableHeader variableHeader) {
		String clientId = context.getBsId();
		logger.debug("PubRel - clientId: {}, messageId: {}", clientId, variableHeader.messageId());
		// TODO L.cm invokeListenerForPublish
		MqttMessage message = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(variableHeader.messageId()), null);
		Tio.send(context, message);
	}

	@Override
	public void processPubComp(ChannelContext context, MqttMessageIdVariableHeader variableHeader) {
		int messageId = variableHeader.messageId();
		String clientId = context.getBsId();
		logger.debug("PubComp - clientId: {}, messageId: {}", clientId, messageId);
	}

	@Override
	public void processSubscribe(ChannelContext context, MqttSubscribeMessage message) {
		String clientId = context.getBsId();
		int messageId = message.variableHeader().messageId();
		logger.debug("Subscribe - clientId: {} messageId:{}", clientId, messageId);
		List<MqttTopicSubscription> topicSubscriptions = message.payload().topicSubscriptions();
		// 1. 校验 topicFilter
		// 2. 存储 clientId 订阅的 topic
		// 3. 返回 ack
		List<MqttQoS> mqttQoSList = topicSubscriptions.stream()
			.map(MqttTopicSubscription::qualityOfService)
			.collect(Collectors.toList());
		MqttMessage subAckMessage = MqttMessageBuilders.subAck()
			.addGrantedQosList(mqttQoSList)
			.packetId(messageId)
			.build();
		Tio.send(context, subAckMessage);
	}

	@Override
	public void processUnSubscribe(ChannelContext context, MqttUnsubscribeMessage message) {
		String clientId = context.getBsId();
		int messageId = message.variableHeader().messageId();
		logger.debug("UnSubscribe - clientId: {} messageId:{}", clientId, messageId);
		MqttMessage unSubMessage = MqttMessageBuilders.unsubAck()
			.packetId(messageId)
			.build();
		Tio.send(context, unSubMessage);
	}

	@Override
	public void processPingReq(ChannelContext context) {
		String clientId = context.getBsId();
		logger.debug("PingReq - clientId: {}", clientId);
		Tio.send(context, MqttMessage.PINGRESP);
	}

	@Override
	public void processDisConnect(ChannelContext context) {
		String clientId = context.getBsId();
		logger.info("DisConnect - clientId: {}", clientId);
		Tio.unbindBsId(context);
		Tio.close(context, "Mqtt DisConnect");
	}

	/**
	 * 处理订阅的消息
	 *
	 * @param topicName topicName
	 * @param message   MqttPublishMessage
	 */
	private void invokeListenerForPublish(MqttQoS mqttQoS, String topicName, MqttPublishMessage message) {
		List<MqttSubscription> subscriptionList = subManager.getMatchedSubscription(topicName, mqttQoS);
		subscriptionList.forEach(subscription -> {
			MqttMessageListener listener = subscription.getListener();
			listener.onMessage(topicName, message.payload());
		});
	}

}
