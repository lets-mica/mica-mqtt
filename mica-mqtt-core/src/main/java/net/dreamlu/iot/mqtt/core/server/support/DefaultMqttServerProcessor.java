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

package net.dreamlu.iot.mqtt.core.server.support;

import net.dreamlu.iot.mqtt.codec.*;
import net.dreamlu.iot.mqtt.core.common.MqttPendingPublish;
import net.dreamlu.iot.mqtt.core.common.MqttPendingQos2Publish;
import net.dreamlu.iot.mqtt.core.server.IMqttServerAuthHandler;
import net.dreamlu.iot.mqtt.core.server.MqttConst;
import net.dreamlu.iot.mqtt.core.server.MqttServerCreator;
import net.dreamlu.iot.mqtt.core.server.MqttServerProcessor;
import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import net.dreamlu.iot.mqtt.core.server.store.IMqttMessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.utils.hutool.StrUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * mqtt broker 处理器
 *
 * @author L.cm
 */
public class DefaultMqttServerProcessor implements MqttServerProcessor {
	private static final Logger logger = LoggerFactory.getLogger(DefaultMqttServerProcessor.class);
	private final IMqttMessageStore messageStore;
	private final IMqttSessionManager sessionManager;
	private final IMqttServerAuthHandler authHandler;
	private final IMqttMessageDispatcher messageDispatcher;
	private final IMqttConnectStatusListener connectStatusListener;
	private final IMqttMessageListener messageListener;
	private final ScheduledThreadPoolExecutor executor;

	public DefaultMqttServerProcessor(MqttServerCreator serverCreator, ScheduledThreadPoolExecutor executor) {
		this.messageStore = serverCreator.getMessageStore();
		this.sessionManager = serverCreator.getSessionManager();
		this.authHandler = serverCreator.getAuthHandler();
		this.messageDispatcher = serverCreator.getMessageDispatcher();
		this.connectStatusListener = serverCreator.getConnectStatusListener();
		this.messageListener = serverCreator.getMessageListener();
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
		// 3. 绑定 clientId
		Tio.bindBsId(context, clientId);
		MqttConnectVariableHeader variableHeader = mqttMessage.variableHeader();
		// 4. 心跳超时时间，当然这个值如果小于全局配置（默认：120s），定时检查的时间间隔还是以全局为准，只是在判断时用此值
		int keepAliveSeconds = variableHeader.keepAliveTimeSeconds();
		if (keepAliveSeconds > 0) {
			context.setHeartbeatTimeout(TimeUnit.SECONDS.toMillis(keepAliveSeconds));
		}
		// 5. session 处理，先默认全部连接关闭时清除
//		boolean cleanSession = variableHeader.isCleanSession();
//		if (cleanSession) {
//			// TODO L.cm 考虑 session 处理 可参数： https://www.emqx.com/zh/blog/mqtt-session
//			// mqtt v5.0 会话超时时间
//			MqttProperties properties = variableHeader.properties();
//			Integer sessionExpiryInterval = properties.getPropertyValue(MqttProperties.MqttPropertyType.SESSION_EXPIRY_INTERVAL);
//			System.out.println(sessionExpiryInterval);
//		}
		// 6. 存储遗嘱消息
		boolean willFlag = variableHeader.isWillFlag();
		if (willFlag) {
			Message willMessage = new Message();
			willMessage.setTopic(payload.willTopic());
			willMessage.setPayload(payload.willMessageInBytes());
			willMessage.setQos(variableHeader.willQos());
			willMessage.setRetain(variableHeader.isWillRetain());
			messageStore.addWillMessage(clientId, willMessage);
		}
		// 7. 返回 ack
		connAckByReturnCode(clientId, context, MqttConnectReturnCode.CONNECTION_ACCEPTED);
		// 8. 在线状态
		connectStatusListener.online(clientId);
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
		logger.debug("Publish - clientId:{} topicName:{} mqttQoS:{} packetId:{}", clientId, topicName, mqttQoS, packetId);
		switch (mqttQoS) {
			case AT_MOST_ONCE:
				invokeListenerForPublish(clientId, mqttQoS, topicName, message, fixedHeader.isRetain());
				break;
			case AT_LEAST_ONCE:
				invokeListenerForPublish(clientId, mqttQoS, topicName, message, fixedHeader.isRetain());
				if (packetId != -1) {
					MqttMessage messageAck = MqttMessageBuilders.pubAck()
						.packetId(packetId)
						.build();
					Boolean resultPubAck = Tio.send(context, messageAck);
					logger.debug("Publish - PubAck send clientId:{} topicName:{} mqttQoS:{} packetId:{} result:{}", clientId, topicName, mqttQoS, packetId, resultPubAck);
				}
				break;
			case EXACTLY_ONCE:
				if (packetId != -1) {
					MqttFixedHeader pubRecFixedHeader = new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0);
					MqttMessage pubRecMessage = new MqttMessage(pubRecFixedHeader, MqttMessageIdVariableHeader.from(packetId));
					MqttPendingQos2Publish pendingQos2Publish = new MqttPendingQos2Publish(message, pubRecMessage);
					Boolean resultPubRec = Tio.send(context, pubRecMessage);
					logger.debug("Publish - PubRec send clientId:{} topicName:{} mqttQoS:{} packetId:{} result:{}", clientId, topicName, mqttQoS, packetId, resultPubRec);
					sessionManager.addPendingQos2Publish(clientId, packetId, pendingQos2Publish);
					pendingQos2Publish.startPubRecRetransmitTimer(executor, msg -> Tio.send(context, msg));
				}
				break;
			case FAILURE:
			default:
				break;
		}
	}

	@Override
	public void processPubAck(ChannelContext context, MqttMessageIdVariableHeader variableHeader) {
		int messageId = variableHeader.messageId();
		String clientId = context.getBsId();
		logger.debug("PubAck - clientId:{}, messageId: {}", clientId, messageId);
		MqttPendingPublish pendingPublish = sessionManager.getPendingPublish(clientId, messageId);
		if (pendingPublish == null) {
			return;
		}
		pendingPublish.onPubAckReceived();
		sessionManager.removePendingPublish(clientId, messageId);
		ByteBufferUtil.clear(pendingPublish.getPayload());
	}

	@Override
	public void processPubRec(ChannelContext context, MqttMessageIdVariableHeader variableHeader) {
		String clientId = context.getBsId();
		int messageId = variableHeader.messageId();
		logger.debug("PubRec - clientId:{}, messageId: {}", clientId, messageId);
		MqttPendingPublish pendingPublish = sessionManager.getPendingPublish(clientId, messageId);
		if (pendingPublish == null) {
			return;
		}
		pendingPublish.onPubAckReceived();

		MqttFixedHeader fixedHeader = new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_LEAST_ONCE, false, 0);
		MqttMessage pubRelMessage = new MqttMessage(fixedHeader, variableHeader);
		Tio.send(context, pubRelMessage);

		pendingPublish.setPubRelMessage(pubRelMessage);
		pendingPublish.startPubRelRetransmissionTimer(executor, msg -> Tio.send(context, msg));
	}

	@Override
	public void processPubRel(ChannelContext context, MqttMessageIdVariableHeader variableHeader) {
		String clientId = context.getBsId();
		int messageId = variableHeader.messageId();
		logger.debug("PubRel - clientId:{}, messageId: {}", clientId, messageId);
		MqttPendingQos2Publish pendingQos2Publish = sessionManager.getPendingQos2Publish(clientId, messageId);
		if (pendingQos2Publish != null) {
			MqttPublishMessage incomingPublish = pendingQos2Publish.getIncomingPublish();
			String topicName = incomingPublish.variableHeader().topicName();
			MqttFixedHeader incomingFixedHeader = incomingPublish.fixedHeader();
			MqttQoS mqttQoS = incomingFixedHeader.qosLevel();
			boolean retain = incomingFixedHeader.isRetain();
			invokeListenerForPublish(clientId, mqttQoS, topicName, incomingPublish, retain);
			pendingQos2Publish.onPubRelReceived();
			sessionManager.removePendingQos2Publish(clientId, messageId);
		}
		MqttMessage message = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(messageId), null);
		Tio.send(context, message);
	}

	@Override
	public void processPubComp(ChannelContext context, MqttMessageIdVariableHeader variableHeader) {
		int messageId = variableHeader.messageId();
		String clientId = context.getBsId();
		logger.debug("PubComp - clientId:{}, messageId: {}", clientId, messageId);
		MqttPendingPublish pendingPublish = sessionManager.getPendingPublish(clientId, messageId);
		if (pendingPublish != null) {
			ByteBufferUtil.clear(pendingPublish.getPayload());
			pendingPublish.onPubCompReceived();
			sessionManager.removePendingPublish(clientId, messageId);
		}
	}

	@Override
	public void processSubscribe(ChannelContext context, MqttSubscribeMessage message) {
		String clientId = context.getBsId();
		int messageId = message.variableHeader().messageId();
		List<MqttTopicSubscription> topicSubscriptions = message.payload().topicSubscriptions();
		// 1. 校验 topicFilter
		// 2. 存储 clientId 订阅的 topic
		List<MqttQoS> mqttQosList = new ArrayList<>();
		List<String> topicList = new ArrayList<>();
		for (MqttTopicSubscription subscription : topicSubscriptions) {
			String topicName = subscription.topicName();
			MqttQoS mqttQoS = subscription.qualityOfService();
			mqttQosList.add(mqttQoS);
			topicList.add(topicName);
			sessionManager.addSubscribe(topicName, clientId, mqttQoS);
			logger.debug("Subscribe - clientId:{} messageId:{} topicFilter:{} mqttQoS:{}", clientId, messageId, topicName, mqttQoS);
		}
		// 3. 返回 ack
		MqttMessage subAckMessage = MqttMessageBuilders.subAck()
			.addGrantedQosList(mqttQosList)
			.packetId(messageId)
			.build();
		Tio.send(context, subAckMessage);
		// 4. 发送保留消息
		for (String topic : topicList) {
			Message retainMessage = messageStore.getRetainMessage(topic);
			if (retainMessage != null) {
				messageDispatcher.send(clientId, retainMessage);
			}
		}
	}

	@Override
	public void processUnSubscribe(ChannelContext context, MqttUnsubscribeMessage message) {
		String clientId = context.getBsId();
		int messageId = message.variableHeader().messageId();
		List<String> topicFilterList = message.payload().topics();
		for (String topicFilter : topicFilterList) {
			sessionManager.removeSubscribe(topicFilter, clientId);
			logger.debug("UnSubscribe - clientId:{} messageId:{} topicFilter:{}", clientId, messageId, topicFilter);
		}
		MqttMessage unSubMessage = MqttMessageBuilders.unsubAck()
			.packetId(messageId)
			.build();
		Tio.send(context, unSubMessage);
	}

	@Override
	public void processPingReq(ChannelContext context) {
		String clientId = context.getBsId();
		logger.debug("PingReq - clientId:{}", clientId);
		Tio.send(context, MqttMessage.PINGRESP);
	}

	@Override
	public void processDisConnect(ChannelContext context) {
		String clientId = context.getBsId();
		logger.info("DisConnect - clientId:{}", clientId);
		// 设置正常断开的标识
		context.set(MqttConst.DIS_CONNECTED, (byte) 1);
		Tio.close(context, "Mqtt DisConnect");
	}

	/**
	 * 处理订阅的消息
	 *
	 * @param clientId  clientId
	 * @param topicName topicName
	 * @param message   MqttPublishMessage
	 */
	private void invokeListenerForPublish(String clientId,
										  MqttQoS mqttQoS,
										  String topicName,
										  MqttPublishMessage message,
										  boolean isRetain) {
		ByteBuffer payload = message.payload();
		// 1. retain 消息逻辑
		if (isRetain) {
			// qos == 0 or payload is none,then clear previous retain message
			if (MqttQoS.AT_MOST_ONCE == mqttQoS || payload == null || payload.array().length == 0) {
				this.messageStore.clearRetainMessage(topicName);
			} else {
				Message retainMessage = new Message();
				retainMessage.setTopic(topicName);
				retainMessage.setQos(mqttQoS.value());
				retainMessage.setPayload(payload.array());
				retainMessage.setClientId(clientId);
				retainMessage.setMessageType(MqttMessageType.PUBLISH.value());
				this.messageStore.addRetainMessage(topicName, retainMessage);
			}
		}
		// 2. 消息发布
		messageListener.onMessage(clientId, topicName, mqttQoS, payload);
	}

}
