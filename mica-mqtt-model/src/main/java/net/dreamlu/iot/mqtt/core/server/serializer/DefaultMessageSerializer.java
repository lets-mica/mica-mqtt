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

package net.dreamlu.iot.mqtt.core.server.serializer;

import net.dreamlu.iot.mqtt.core.server.model.Message;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * mica mqtt 消息序列化
 *
 * @author L.cm
 */
public enum DefaultMessageSerializer implements IMessageSerializer {

	/**
	 * 单利
	 */
	INSTANCE;

	/**
	 * 空 byte 数组
	 */
	private static final byte[] EMPTY_BYTES = new byte[0];
	/**
	 * 空 int byte 数组，4 个长度
	 */
	private static final byte[] EMPTY_INT_BYTES = new byte[4];
	/**
	 * 空 long byte 数组，8 个长度
	 */
	private static final byte[] EMPTY_LONG_BYTES = new byte[8];

	@Override
	public byte[] serialize(Message message) {
		if (message == null) {
			return EMPTY_BYTES;
		}
		// 4 + 4 * 4 + 4 + 4 + 4 + 1 * 2 + 4 + 4 + 4 + 8 + 8
		int protocolLength = 62;
		String fromClientId = message.getFromClientId();
		// 消息来源 客户端 id
		byte[] fromClientIdBytes = null;
		if (fromClientId != null) {
			fromClientIdBytes = fromClientId.getBytes(StandardCharsets.UTF_8);
			protocolLength += fromClientIdBytes.length;
		}
		// 消息来源 用户名
		String fromUsername = message.getFromUsername();
		// 消息来源 客户端 id
		byte[] fromUsernameBytes = null;
		if (fromUsername != null) {
			fromUsernameBytes = fromUsername.getBytes(StandardCharsets.UTF_8);
			protocolLength += fromUsernameBytes.length;
		}
		// 消息目的 Client ID，主要是在遗嘱消息用
		String clientId = message.getClientId();
		byte[] clientIdBytes = null;
		if (clientId != null) {
			clientIdBytes = clientId.getBytes(StandardCharsets.UTF_8);
			protocolLength += clientIdBytes.length;
		}
		// 消息目的用户名，主要是在遗嘱消息用
		String username = message.getUsername();
		byte[] usernameBytes = null;
		if (username != null) {
			usernameBytes = username.getBytes(StandardCharsets.UTF_8);
			protocolLength += usernameBytes.length;
		}
		// topic
		String topic = message.getTopic();
		byte[] topicBytes = null;
		if (topic != null) {
			topicBytes = topic.getBytes(StandardCharsets.UTF_8);
			protocolLength += topicBytes.length;
		}
		// 消息内容
		ByteBuffer payload = message.getPayload();
		byte[] payloadBytes = null;
		if (payload != null) {
			payloadBytes = payload.array();
			protocolLength += payloadBytes.length;
		}
		// 客户端的 IPAddress
		String peerHost = message.getPeerHost();
		byte[] peerHostBytes = null;
		if (peerHost != null) {
			peerHostBytes = peerHost.getBytes(StandardCharsets.UTF_8);
			protocolLength += peerHostBytes.length;
		}
		// 事件触发所在节点
		String node = message.getNode();
		byte[] nodeBytes = null;
		if (node != null) {
			nodeBytes = node.getBytes(StandardCharsets.UTF_8);
			protocolLength += nodeBytes.length;
		}
		ByteBuffer buffer = ByteBuffer.allocate(protocolLength);
		// MQTT 消息 ID
		Integer messageId = message.getId();
		if (messageId != null) {
			buffer.putInt(messageId);
		} else {
			buffer.put(EMPTY_INT_BYTES);
		}
		// 消息来源 客户端 id
		if (fromClientId != null) {
			buffer.putInt(fromClientIdBytes.length);
			buffer.put(fromClientIdBytes);
		} else {
			buffer.put(EMPTY_INT_BYTES);
		}
		// 消息来源 用户名
		if (fromUsername != null) {
			buffer.putInt(fromUsernameBytes.length);
			buffer.put(fromUsernameBytes);
		} else {
			buffer.put(EMPTY_INT_BYTES);
		}
		// 消息目的 Client ID，主要是在遗嘱消息用
		if (clientId != null) {
			buffer.putInt(clientIdBytes.length);
			buffer.put(clientIdBytes);
		} else {
			buffer.put(EMPTY_INT_BYTES);
		}
		// 消息来源 用户名
		if (username != null) {
			buffer.putInt(usernameBytes.length);
			buffer.put(usernameBytes);
		} else {
			buffer.put(EMPTY_INT_BYTES);
		}
		// 消息类型
		int messageType = message.getMessageType();
		buffer.put((byte) messageType);
		// topic
		if (topicBytes != null) {
			buffer.putInt(topicBytes.length);
			buffer.put(topicBytes);
		} else {
			buffer.put(EMPTY_INT_BYTES);
		}
		// qos
		buffer.put((byte) message.getQos());
		// retain
		buffer.put(message.isRetain() ? (byte) 1 : (byte) 0);
		// dup
		buffer.put(message.isDup() ? (byte) 1 : (byte) 0);
		// 消息内容
		if (payloadBytes != null) {
			buffer.putInt(payloadBytes.length);
			buffer.put(payloadBytes);
		} else {
			buffer.put(EMPTY_INT_BYTES);
		}
		// 客户端的 IPAddress
		if (peerHostBytes != null) {
			buffer.putInt(peerHostBytes.length);
			buffer.put(peerHostBytes);
		} else {
			buffer.put(EMPTY_INT_BYTES);
		}
		// 存储时间
		buffer.putLong(message.getTimestamp());
		// PUBLISH 消息到达 Broker 的时间 (ms)
		Long publishReceivedAt = message.getPublishReceivedAt();
		if (publishReceivedAt != null) {
			buffer.putLong(publishReceivedAt);
		} else {
			buffer.put(EMPTY_LONG_BYTES);
		}
		// 事件触发所在节点
		if (nodeBytes != null) {
			buffer.putInt(nodeBytes.length);
			buffer.put(nodeBytes);
		} else {
			buffer.put(EMPTY_INT_BYTES);
		}
		return buffer.array();
	}

	@Override
	public Message deserialize(byte[] data) {
		// 1. null 或者空 byte 数组
		if (data == null || data.length < 1) {
			return null;
		}
		Message message = new Message();
		ByteBuffer buffer = ByteBuffer.wrap(data);
		// MQTT 消息 ID
		int messageId = buffer.getInt();
		if (messageId > 0) {
			message.setId(messageId);
		}
		// 消息来源 客户端 id
		int fromClientIdLen = buffer.getInt();
		if (fromClientIdLen > 0) {
			byte[] fromClientIdBytes = new byte[fromClientIdLen];
			buffer.get(fromClientIdBytes);
			message.setFromClientId(new String(fromClientIdBytes, StandardCharsets.UTF_8));
		}
		// 消息来源 用户名
		int fromUsernameLen = buffer.getInt();
		if (fromUsernameLen > 0) {
			byte[] fromUsernameBytes = new byte[fromUsernameLen];
			buffer.get(fromUsernameBytes);
			message.setFromUsername(new String(fromUsernameBytes, StandardCharsets.UTF_8));
		}
		// 消息目的 Client ID，主要是在遗嘱消息用
		int clientIdLen = buffer.getInt();
		if (clientIdLen > 0) {
			byte[] clientIdBytes = new byte[clientIdLen];
			buffer.get(clientIdBytes);
			message.setClientId(new String(clientIdBytes, StandardCharsets.UTF_8));
		}
		// 消息目的用户名，主要是在遗嘱消息用
		int usernameLen = buffer.getInt();
		if (usernameLen > 0) {
			byte[] usernameBytes = new byte[usernameLen];
			buffer.get(usernameBytes);
			message.setUsername(new String(usernameBytes, StandardCharsets.UTF_8));
		}
		// 消息类型
		short messageType = readUnsignedByte(buffer);
		if (messageType > 0) {
			message.setMessageType(messageType);
		}
		// topic
		int topicLength = buffer.getInt();
		if (topicLength > 0) {
			byte[] topicBytes = new byte[topicLength];
			buffer.get(topicBytes);
			message.setTopic(new String(topicBytes, StandardCharsets.UTF_8));
		}
		// qos
		short qos = readUnsignedByte(buffer);
		if (qos >= 0) {
			message.setQos(qos);
		}
		// retain
		byte isRetain = buffer.get();
		message.setRetain(isRetain == 1);
		// 是否重发
		byte isDup = buffer.get();
		message.setDup(isDup == 1);
		// 消息内容
		int payloadLen = buffer.getInt();
		if (payloadLen > 0) {
			byte[] payloadBytes = new byte[payloadLen];
			buffer.get(payloadBytes);
			message.setPayload(ByteBuffer.wrap(payloadBytes));
		}
		// 客户端的 peerHost IPAddress
		int peerHostLen = buffer.getInt();
		if (peerHostLen > 0) {
			byte[] peerHostBytes = new byte[peerHostLen];
			buffer.get(peerHostBytes);
			message.setPeerHost(new String(peerHostBytes, StandardCharsets.UTF_8));
		}
		// 存储时间
		long timestamp = buffer.getLong();
		message.setTimestamp(timestamp);
		// PUBLISH 消息到达 Broker 的时间 (ms)
		long publishReceivedAt = buffer.getLong();
		if (publishReceivedAt > 0) {
			message.setPublishReceivedAt(publishReceivedAt);
		}
		// 事件触发所在节点
		int nodeLength = buffer.getInt();
		if (nodeLength > 0) {
			byte[] nodeBytes = new byte[nodeLength];
			buffer.get(nodeBytes);
			message.setNode(new String(nodeBytes, StandardCharsets.UTF_8));
		}
		return message;
	}

	/**
	 * read unsigned byte
	 *
	 * @param buffer ByteBuffer
	 * @return short
	 */
	private static short readUnsignedByte(ByteBuffer buffer) {
		return (short) (buffer.get() & 0xFF);
	}

}
