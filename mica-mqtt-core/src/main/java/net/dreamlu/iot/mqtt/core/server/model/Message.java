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

package net.dreamlu.iot.mqtt.core.server.model;

import net.dreamlu.iot.mqtt.codec.MqttMessageType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

/**
 * 消息模型，用于存储
 *
 * @author L.cm
 */
public class Message implements Serializable {
	private String clientId;
	private int messageId;
	private Map<String, Object> headers;
	private MqttMessageType messageType;
	private byte[] payload;
	private long storeTime;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public Map<String, Object> getHeaders() {
		return headers;
	}

	public void setHeaders(Map<String, Object> headers) {
		this.headers = headers;
	}

	public MqttMessageType getMessageType() {
		return messageType;
	}

	public void setMessageType(MqttMessageType messageType) {
		this.messageType = messageType;
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
	public String toString() {
		return "MessageInfo{" +
			"clientId='" + clientId + '\'' +
			", messageId=" + messageId +
			", headers=" + headers +
			", messageType=" + messageType +
			", payload=" + Arrays.toString(payload) +
			", storeTime=" + storeTime +
			'}';
	}
}
