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

package net.dreamlu.iot.mqtt.core.server.websocket;

import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.codec.WriteBuffer;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.TioConfig;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.server.intf.ServerAioHandler;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.nio.ByteBuffer;

/**
 * mqtt websocket 消息处理
 *
 * @author L.cm
 */
public class MqttWsMsgHandler implements IWsMsgHandler {
	/**
	 * mqtt websocket message body key
	 */
	private static final String MQTT_WS_MSG_BODY_KEY = "MQTT_WS_MSG_BODY_KEY";

	private final String[] supportedSubProtocols;
	private final ServerAioHandler mqttServerAioHandler;

	public MqttWsMsgHandler(ServerAioHandler mqttServerAioHandler) {
		this(new String[]{"mqtt", "mqttv3.1", "mqttv3.1.1"}, mqttServerAioHandler);
	}

	public MqttWsMsgHandler(String[] supportedSubProtocols, ServerAioHandler mqttServerAioHandler) {
		this.supportedSubProtocols = supportedSubProtocols;
		this.mqttServerAioHandler = mqttServerAioHandler;
	}

	@Override
	public String[] getSupportedSubProtocols() {
		return this.supportedSubProtocols;
	}

	@Override
	public HttpResponse handshake(HttpRequest request, HttpResponse httpResponse, ChannelContext channelContext) {
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
		// 在连接中添加 WriteBuffer 用来处理半包消息
		WriteBuffer wsBody = (WriteBuffer) context.get(MQTT_WS_MSG_BODY_KEY);
		if (wsBody == null) {
			wsBody = new WriteBuffer();
			context.set(MQTT_WS_MSG_BODY_KEY, wsBody);
		}
	}

	/**
	 * 字节消息（binaryType = arraybuffer）过来后会走这个方法
	 */
	@Override
	public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext context) throws Exception {
		WriteBuffer wsBody = (WriteBuffer) context.get(MQTT_WS_MSG_BODY_KEY);
		ByteBuffer buffer = getMqttBody(wsBody, bytes);
		if (buffer == null) {
			return null;
		}
		// 重置 buffer
		buffer.rewind();
		// 解析 mqtt 消息
		Packet packet = mqttServerAioHandler.decode(buffer, 0, 0, buffer.remaining(), context);
		if (packet == null) {
			return null;
		}
		mqttServerAioHandler.handler(packet, context);
		return null;
	}

	@Override
	public WsResponse encodeSubProtocol(Packet packet, TioConfig tioConfig, ChannelContext context) {
		if (packet instanceof MqttMessage) {
			ByteBuffer buffer = mqttServerAioHandler.encode(packet, null, context);
			return WsResponse.fromBytes(buffer.array());
		}
		return null;
	}

	/**
	 * 当客户端发 close flag 时，会走这个方法
	 */
	@Override
	public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext context) {
		Tio.remove(context, "Mqtt websocket close.");
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
	 * 读取 mqtt 消息体处理半包的情况
	 *
	 * @param bytes   消息类容
	 * @return ByteBuffer
	 */
	private static synchronized ByteBuffer getMqttBody(WriteBuffer wsBody, byte[] bytes) {
		wsBody.writeBytes(bytes);
		int length = wsBody.size();
		if (length < 2) {
			return null;
		}
		ByteBuffer buffer = wsBody.toBuffer();
		int mqttLength = getMqttLength(buffer) + 2;
		if (length < mqttLength) {
			return null;
		}
		// 数据已经读取完毕
		wsBody.reset();
		return buffer;
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
