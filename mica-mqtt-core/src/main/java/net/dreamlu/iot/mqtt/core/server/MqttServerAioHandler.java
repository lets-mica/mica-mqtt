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

import net.dreamlu.iot.mqtt.codec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.TioConfig;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.AcceptCompletionHandler;
import org.tio.server.intf.ServerAioHandler;

import java.nio.ByteBuffer;

/**
 * @author L.cm
 */
public class MqttServerAioHandler implements ServerAioHandler {
	private static final Logger log = LoggerFactory.getLogger(AcceptCompletionHandler.class);
	private final MqttDecoder mqttDecoder;
	private final MqttEncoder mqttEncoder;
	private final MqttServerProcessor processor;

	public MqttServerAioHandler(MqttServerProcessor processor) {
		this.mqttDecoder = MqttDecoder.INSTANCE;
		this.mqttEncoder = MqttEncoder.INSTANCE;
		this.processor = processor;
	}

	/**
	 * 根据ByteBuffer解码成业务需要的Packet对象.
	 * 如果收到的数据不全，导致解码失败，请返回null，在下次消息来时框架层会自动续上前面的收到的数据
	 *
	 * @param buffer         参与本次希望解码的ByteBuffer
	 * @param limit          ByteBuffer的limit
	 * @param position       ByteBuffer的position，不一定是0哦
	 * @param readableLength ByteBuffer参与本次解码的有效数据（= limit - position）
	 * @param channelContext ChannelContext
	 * @return Packet
	 * @throws AioDecodeException AioDecodeException
	 */
	@Override
	public Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
		return mqttDecoder.decode(buffer);
	}

	/**
	 * 编码
	 *
	 * @param packet         Packet
	 * @param tioConfig      TioConfig
	 * @param channelContext ChannelContext
	 * @return ByteBuffer
	 */
	@Override
	public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
		return mqttEncoder.doEncode((MqttMessage) packet);
	}

	/**
	 * 处理消息包
	 *
	 * @param packet  Packet
	 * @param context ChannelContext
	 * @throws Exception Exception
	 */
	@Override
	public void handler(Packet packet, ChannelContext context) throws Exception {
		MqttMessage mqttMessage = (MqttMessage) packet;
		// 1. 先判断 mqtt 消息解析是否正常
		DecoderResult decoderResult = mqttMessage.decoderResult();
		if (decoderResult.isFailure()) {
			processFailure(context, mqttMessage);
			return;
		}
		MqttFixedHeader fixedHeader = mqttMessage.fixedHeader();
		MqttMessageType messageType = fixedHeader.messageType();
		log.debug("MqttMessageType:{}", messageType);
		// 2. 单独处理 CONNECT 的消息
		// 3. 其他消息先判断是否连接、认证过
		// TODO L.cm 还是设计 filter 去处理该问题？？？
		switch (messageType) {
			case CONNECT:
				processor.processConnect(context, (MqttConnectMessage) mqttMessage);
				break;
			case PUBLISH:
				processor.processPublish(context, (MqttPublishMessage) mqttMessage);
				break;
			case PUBACK:
				processor.processPubAck(context, (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
				break;
			case PUBREC:
				processor.processPubRec(context, (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
				break;
			case PUBREL:
				processor.processPubRel(context, (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
				break;
			case PUBCOMP:
				processor.processPubComp(context, (MqttMessageIdVariableHeader) mqttMessage.variableHeader());
				break;
			case SUBSCRIBE:
				processor.processSubscribe(context, (MqttSubscribeMessage) mqttMessage);
				break;
			case UNSUBSCRIBE:
				processor.processUnSubscribe(context, (MqttUnsubscribeMessage) mqttMessage);
				break;
			case PINGREQ:
				processor.processPingReq(context);
				break;
			case DISCONNECT:
				processor.processDisConnect(context);
				break;
			default:
				break;
		}
	}

	/**
	 * 处理失败
	 *
	 * @param context     ChannelContext
	 * @param mqttMessage MqttMessage
	 */
	private void processFailure(ChannelContext context, MqttMessage mqttMessage) {
		Throwable cause = mqttMessage.decoderResult().getCause();
		if (cause instanceof MqttUnacceptableProtocolVersionException) {
			log.error(cause.getMessage());
			// 不支持的协议版本
			MqttConnAckMessage message = MqttMessageBuilders.connAck()
				.returnCode(MqttConnectReturnCode.CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION)
				.sessionPresent(false)
				.build();
			Tio.send(context, message);
		} else if (cause instanceof MqttIdentifierRejectedException) {
			log.error(cause.getMessage());
			// 不合格的 clientId
			MqttConnAckMessage message = MqttMessageBuilders.connAck()
				.returnCode(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED)
				.sessionPresent(false)
				.build();
			Tio.send(context, message);
		} else if (cause instanceof DecoderException) {
			log.error(cause.getMessage(), cause);
			// 消息解码异常，
		} else {
			log.error(cause.getMessage(), cause);
			// 发送断开连接，是否强制关闭客户端连接？？？
			Tio.send(context, MqttMessage.DISCONNECT);
		}
	}

}
