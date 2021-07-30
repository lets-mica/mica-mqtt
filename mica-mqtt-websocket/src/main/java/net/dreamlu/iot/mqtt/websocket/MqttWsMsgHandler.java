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

package net.dreamlu.iot.mqtt.websocket;

import net.dreamlu.iot.mqtt.codec.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.HeaderName;
import org.tio.http.common.HeaderValue;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.stream.Collectors;

/**
 * mqtt websocket 消息处理
 *
 * @author L.cm
 */
public class MqttWsMsgHandler implements IWsMsgHandler {
	private static final Logger logger = LoggerFactory.getLogger(MqttWsMsgHandler.class);
	// websocket 子协议头
	private static final String Sec_Websocket_Protocol = "Sec-Websocket-Protocol".toLowerCase();
	private static final HeaderName Header_Name_Sec_Websocket_Protocol = HeaderName.from(Sec_Websocket_Protocol);
	// mqtt websocket message body key
	private static final String MQTT_WS_MSG_BODY_KEY = "MQTT_WS_MSG_BODY_KEY";

	private final String[] supportedSubProtocols;

	public MqttWsMsgHandler() {
		this(new String[]{"mqtt", "mqttv3.1", "mqttv3.1.1"});
	}

	public MqttWsMsgHandler(String[] supportedSubProtocols) {
		this.supportedSubProtocols = supportedSubProtocols;
	}

	@Override
	public HttpResponse handshake(HttpRequest request, HttpResponse httpResponse, ChannelContext channelContext) {
		String requestedSubProtocols = request.getHeader(Sec_Websocket_Protocol);
		String selectSubProtocol = selectSubProtocol(requestedSubProtocols, this.supportedSubProtocols);
		if (selectSubProtocol != null) {
			httpResponse.addHeader(Header_Name_Sec_Websocket_Protocol, HeaderValue.from(selectSubProtocol));
		}
		logger.debug("Mqtt websocket handshake requestedSubProtocols:{} selectSubProtocol:{}", requestedSubProtocols, selectSubProtocol);
		return httpResponse;
	}

	/**
	 * 握手后处理
	 *
	 * @param request  HttpRequest
	 * @param response HttpResponse
	 * @param context  ChannelContext
	 */
	@Override
	public void onAfterHandshaked(HttpRequest request, HttpResponse response, ChannelContext context) {

	}

	/**
	 * 字节消息（binaryType = arraybuffer）过来后会走这个方法
	 */
	@Override
	public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext context) {
		WriteBuffer wsBody = (WriteBuffer) context.get(MQTT_WS_MSG_BODY_KEY);
		if (wsBody == null) {
			wsBody = new WriteBuffer();
			context.set(MQTT_WS_MSG_BODY_KEY, wsBody);
		}
		wsBody.writeBytes(bytes);
		byte[] array = wsBody.toArray();
		int length = array.length;
		if (length < 2) {
			return null;
		}
		ByteBuffer buffer = ByteBuffer.wrap(array);
		int mqttLength = getMqttLength(buffer);
		if (length < mqttLength + 2) {
			return null;
		}
		// 数据已经读取完毕
		wsBody.reset();
		buffer.rewind();
		MqttMessage mqttMessage = new MqttDecoder().decode(context, buffer, 0, 0, length);
		if (mqttMessage == null) {
			return null;
		}
		System.out.println(mqttMessage);
		MqttFixedHeader fixedHeader = mqttMessage.fixedHeader();
		MqttMessageType messageType = fixedHeader.messageType();
		MqttEncoder encoder = MqttEncoder.INSTANCE;

		if (MqttMessageType.CONNECT == messageType) {
			MqttConnAckMessage message = MqttMessageBuilders.connAck()
				.returnCode(MqttConnectReturnCode.CONNECTION_ACCEPTED)
				.sessionPresent(false)
				.build();
			ByteBuffer encode = encoder.doEncode(context, message, ByteBufferAllocator.HEAP);
			Tio.send(context, WsResponse.fromBytes(encode.array()));
		} else if (MqttMessageType.PINGREQ == messageType) {
			ByteBuffer encode = encoder.doEncode(context, MqttMessage.PINGRESP, ByteBufferAllocator.HEAP);
			Tio.send(context, WsResponse.fromBytes(encode.array()));
		} else if (MqttMessageType.PUBLISH == messageType) {
			MqttPublishMessage mqttPublishMessage = (MqttPublishMessage) mqttMessage;
			ByteBuffer payload = mqttPublishMessage.payload();
			System.out.println(ByteBufferUtil.toString(payload));
		} else if (MqttMessageType.SUBSCRIBE == messageType) {
			// 3. 返回 ack
			MqttSubscribeMessage message = (MqttSubscribeMessage) mqttMessage;
			int messageId = message.variableHeader().messageId();
			List<MqttQoS> mqttQosList = message.payload()
				.topicSubscriptions()
				.stream()
				.map(MqttTopicSubscription::qualityOfService)
				.collect(Collectors.toList());
			MqttMessage subAckMessage = MqttMessageBuilders.subAck()
				.addGrantedQosList(mqttQosList)
				.packetId(messageId)
				.build();
			ByteBuffer encode = encoder.doEncode(context, subAckMessage, ByteBufferAllocator.HEAP);
			Tio.send(context, WsResponse.fromBytes(encode.array()));
		} else if (MqttMessageType.DISCONNECT == messageType) {
			Tio.close(context, "Mqtt websocket DisConnect");
		}
		return null;
	}

	/**
	 * 当客户端发 close flag 时，会走这个方法
	 */
	@Override
	public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext context) {
		Tio.close(context, "Mqtt websocket close.");
		return null;
	}

	/**
	 * 字符消息（binaryType = blob）过来后会走这个方法
	 */
	@Override
	public Object onText(WsRequest wsRequest, String text, ChannelContext context) {
		return null;
	}

	/**
	 * Selects the first matching supported sub protocol
	 *
	 * @param requestedSubProtocols 请求中的子协议
	 * @param subProtocols          系统支持得子协议，注意：不支持 * 通配
	 * @return First matching supported sub protocol. Null if not found.
	 */
	private static String selectSubProtocol(String requestedSubProtocols, String[] subProtocols) {
		if (requestedSubProtocols == null || subProtocols == null || subProtocols.length == 0) {
			return null;
		}
		String[] requestedSubProtocolArray = requestedSubProtocols.split(",");
		for (String p : requestedSubProtocolArray) {
			String requestedSubProtocol = p.trim();
			for (String supportedSubProtocol : subProtocols) {
				if (requestedSubProtocol.equals(supportedSubProtocol)) {
					return requestedSubProtocol;
				}
			}
		}
		// No match found
		return null;
	}

	/**
	 * 解析 mqtt 消息长度
	 *
	 * @param buffer the buffer to decode from
	 * @return mqtt 消息长度
	 */
	private static int getMqttLength(ByteBuffer buffer) {
		ByteBufferUtil.skipBytes(buffer, 1);
		int remainingLength = 0;
		int multiplier = 1;
		short digit;
		int loops = 0;
		do {
			digit = ByteBufferUtil.readUnsignedByte(buffer);
			remainingLength += (digit & 127) * multiplier;
			multiplier *= 128;
			loops++;
		} while ((digit & 128) != 0 && loops < 4);
		return remainingLength;
	}

}
