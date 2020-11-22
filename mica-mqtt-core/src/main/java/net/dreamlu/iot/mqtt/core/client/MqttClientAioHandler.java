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
import org.tio.client.intf.ClientAioHandler;
import org.tio.core.ChannelContext;
import org.tio.core.TioConfig;
import org.tio.core.exception.AioDecodeException;
import org.tio.core.intf.Packet;
import org.tio.server.AcceptCompletionHandler;

import java.nio.ByteBuffer;

/**
 * mqtt 客户端处理
 *
 * @author L.cm
 */
public class MqttClientAioHandler implements ClientAioHandler {
	private static final Logger log = LoggerFactory.getLogger(AcceptCompletionHandler.class);
	private final MqttDecoder mqttDecoder;
	private final MqttEncoder mqttEncoder;
	private final MqttClientProcessor processor;

	public MqttClientAioHandler(MqttClientProcessor processor) {
		this.mqttDecoder = MqttDecoder.INSTANCE;
		this.mqttEncoder = MqttEncoder.INSTANCE;
		this.processor = processor;
	}

	@Override
	public Packet heartbeatPacket(ChannelContext channelContext) {
		return MqttMessage.PINGREQ;
	}

	@Override
	public Packet decode(ByteBuffer buffer, int limit, int position, int readableLength, ChannelContext channelContext) throws AioDecodeException {
		return mqttDecoder.decode(channelContext, buffer, limit, position, readableLength);
	}

	@Override
	public ByteBuffer encode(Packet packet, TioConfig tioConfig, ChannelContext channelContext) {
		return mqttEncoder.doEncode(channelContext, (MqttMessage) packet);
	}

	@Override
	public void handler(Packet packet, ChannelContext context) {
		MqttMessage message = (MqttMessage) packet;
		// 1. 先判断 mqtt 消息解析是否正常
		DecoderResult decoderResult = message.decoderResult();
		if (decoderResult.isFailure()) {
			processFailure(message);
			return;
		}
		MqttFixedHeader fixedHeader = message.fixedHeader();
		// 根据消息类型处理消息
		MqttMessageType messageType = fixedHeader.messageType();
		switch (messageType) {
			case CONNACK:
				processor.processConAck(context, (MqttConnAckMessage) message);
				break;
			case SUBACK:
				processor.processSubAck((MqttSubAckMessage) message);
				break;
			case PUBLISH:
				processor.processPublish(context, (MqttPublishMessage) message);
				break;
			case UNSUBACK:
				processor.processUnSubAck((MqttUnsubAckMessage) message);
				break;
			case PUBACK:
				processor.processPubAck((MqttPubAckMessage) message);
				break;
			case PUBREC:
				processor.processPubRec(context, message);
				break;
			case PUBREL:
				processor.processPubRel(context, message);
				break;
			case PUBCOMP:
				processor.processPubComp(message);
				break;
			default:
				break;
		}
	}

	/**
	 * 处理失败
	 *
	 * @param mqttMessage MqttMessage
	 */
	private void processFailure(MqttMessage mqttMessage) {
		// 客户端失败，我认为日志记录异常就行了
		Throwable cause = mqttMessage.decoderResult().getCause();
		log.error(cause.getMessage(), cause);
	}

}
