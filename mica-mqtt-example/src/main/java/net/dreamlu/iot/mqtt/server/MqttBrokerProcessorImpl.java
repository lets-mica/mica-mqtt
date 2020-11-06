package net.dreamlu.iot.mqtt.server;

import net.dreamlu.iot.mqtt.codec.*;
import net.dreamlu.iot.mqtt.core.server.MqttServerProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.utils.hutool.StrUtil;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mqtt broker 处理器
 *
 * @author L.cm
 */
public class MqttBrokerProcessorImpl implements MqttServerProcessor {
	private static final Logger log = LoggerFactory.getLogger(MqttBrokerProcessorImpl.class);

	@Override
	public void processConnect(ChannelContext context, MqttConnectMessage mqttMessage) {
		MqttConnectPayload payload = mqttMessage.payload();
		String clientId = payload.clientIdentifier();
		// 1. 客户端必须提供clientId, 不管cleanSession是否为1, 此处没有参考标准协议实现
		if (StrUtil.isBlank(clientId)) {
			refusedIdentifierRejected(context);
			return;
		}
		log.debug("CONNECT - clientId: {}", clientId);
		// 2. 认证
		String userName = payload.userName();
		String password = payload.password();
		boolean authResult = false;
		if (false) {
			MqttConnAckMessage message = MqttMessageBuilders.connAck()
				.returnCode(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD)
				.sessionPresent(false)
				.build();
			Tio.send(context, message);
			return;
		}
		// 3. 设置 clientId
		context.setBsId(clientId);
		// 4. 返回 ack
//		MqttProperties mqttProperties = new MqttProperties();
//
//		MqttProperties.UserProperties userProperty = new MqttProperties.UserProperties();
//		userProperty.add("xxxxxxxxxx", "xxxx");
//		mqttProperties.add(userProperty);

		MqttMessage message = MqttMessageBuilders.connAck()
			.returnCode(MqttConnectReturnCode.CONNECTION_ACCEPTED)
			.sessionPresent(false)
//			.properties(mqttProperties)
			.build();
		Tio.send(context, message);
	}

	private void refusedIdentifierRejected(ChannelContext context) {
		MqttConnAckMessage message = MqttMessageBuilders.connAck()
			.returnCode(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED)
			.sessionPresent(false)
			.build();
		Tio.send(context, message);
	}

	@Override
	public void processPublish(ChannelContext context, MqttPublishMessage message) {
		String clientId = context.getBsId();
		log.debug("PUBLISH - clientId: {}", clientId);
		MqttFixedHeader fixedHeader = message.fixedHeader();
		ByteBuffer payload = message.payload();
		if (payload != null) {
			System.out.println(ByteBufferUtil.toString(payload));
		}
	}

	@Override
	public void processPubAck(ChannelContext context, MqttMessageIdVariableHeader variableHeader) {
		int messageId = variableHeader.messageId();
		String clientId = context.getBsId();
		log.debug("PUBACK - clientId: {}, messageId: {}", clientId, messageId);
	}

	@Override
	public void processPubRec(ChannelContext context, MqttMessageIdVariableHeader variableHeader) {
		String clientId = context.getBsId();
		log.debug("PUBREC - clientId: {}, messageId: {}", clientId, variableHeader.messageId());
		MqttMessage message = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.PUBREL, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(variableHeader.messageId()), null);
		Tio.send(context, message);
	}

	@Override
	public void processPubRel(ChannelContext context, MqttMessageIdVariableHeader variableHeader) {
		String clientId = context.getBsId();
		log.debug("PUBREL - clientId: {}, messageId: {}", clientId, variableHeader.messageId());
		MqttMessage message = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.PUBCOMP, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(variableHeader.messageId()), null);
		Tio.send(context, message);
	}

	@Override
	public void processPubComp(ChannelContext context, MqttMessageIdVariableHeader variableHeader) {
		int messageId = variableHeader.messageId();
		String clientId = context.getBsId();
		log.debug("PUBCOMP - clientId: {}, messageId: {}", clientId, messageId);
	}

	@Override
	public void processSubscribe(ChannelContext context, MqttSubscribeMessage message) {
		List<MqttTopicSubscription> topicSubscriptions = message.payload().topicSubscriptions();
		List<Integer> mqttQoSList = topicSubscriptions.stream()
			.map(MqttTopicSubscription::qualityOfService)
			.map(MqttQoS::value)
			.collect(Collectors.toList());
		MqttMessage subAckMessage = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.SUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(message.variableHeader().messageId()),
			new MqttSubAckPayload(mqttQoSList));
		Tio.send(context, subAckMessage);
	}

	@Override
	public void processUnSubscribe(ChannelContext context, MqttUnsubscribeMessage mqttMessage) {
		String clientId = context.getBsId();
		log.debug("UnSubscribe - clientId: {}", clientId);
		MqttMessage message = MqttMessageFactory.newMessage(
			new MqttFixedHeader(MqttMessageType.UNSUBACK, false, MqttQoS.AT_MOST_ONCE, false, 0),
			MqttMessageIdVariableHeader.from(mqttMessage.variableHeader().messageId()), null);
		Tio.send(context, message);
	}

	@Override
	public void processPingReq(ChannelContext context) {
		String clientId = context.getBsId();
		log.debug("PINGREQ - clientId: {}", clientId);
		Tio.send(context, MqttMessage.PINGRESP);
	}

	@Override
	public void processDisConnect(ChannelContext context) {
		String clientId = context.getBsId();
		log.debug("DISCONNECT - clientId: {}", clientId);
		Tio.close(context, "MqttIdentifierRejected");
	}

}
