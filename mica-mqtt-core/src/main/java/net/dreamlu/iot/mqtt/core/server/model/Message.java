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

package net.dreamlu.iot.mqtt.core.server.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * 消息模型，用于存储
 *
 * @author L.cm
 */
public class Message implements Serializable {

	/**
	 * 客户端 id
	 */
	private String clientId;
	/**
	 * 消息类型
	 */
	private int messageType;
	/**
	 * topic
	 */
	private String topic;
	/**
	 * qos
	 */
	private int qos;
	/**
	 * retain
	 */
	private boolean retain;
	/**
	 * 是否重发
	 */
	private boolean dup;
	/**
	 * 消息内容
	 */
	private byte[] payload;
	/**
	 * 存储时间
	 */
	private long storeTime;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getMessageType() {
		return messageType;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public int getQos() {
		return qos;
	}

	public void setQos(int qos) {
		this.qos = qos;
	}

	public boolean isRetain() {
		return retain;
	}

	public void setRetain(boolean retain) {
		this.retain = retain;
	}

	public boolean isDup() {
		return dup;
	}

	public void setDup(boolean dup) {
		this.dup = dup;
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public long getStoreTime() {
		return storeTime;
	}

	public void setStoreTime(long storeTime) {
		this.storeTime = storeTime;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Message message = (Message) o;
		return messageType == message.messageType &&
			qos == message.qos &&
			retain == message.retain &&
			dup == message.dup &&
			storeTime == message.storeTime &&
			Objects.equals(clientId, message.clientId) &&
			Objects.equals(topic, message.topic) &&
			Arrays.equals(payload, message.payload);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(clientId, messageType, topic, qos, retain, dup, storeTime);
		result = 31 * result + Arrays.hashCode(payload);
		return result;
	}

	@Override
	public String toString() {
		return "Message{" +
			"clientId='" + clientId + '\'' +
			", messageType=" + messageType +
			", topic='" + topic + '\'' +
			", qos=" + qos +
			", retain=" + retain +
			", dup=" + dup +
			", payload=" + Arrays.toString(payload) +
			", storeTime=" + storeTime +
			'}';
	}
}
