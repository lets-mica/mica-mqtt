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

package org.dromara.mica.mqtt.core.server.enums;

/**
 * 消息类型
 *
 * @author L.cm
 */
public enum MessageType {

	/**
	 * 连接
	 */
	CONNECT(1),
	/**
	 * 主题订阅
	 */
	SUBSCRIBE(2),
	/**
	 * 取消订阅
	 */
	UNSUBSCRIBE(3),
	/**
	 * 上行数据
	 */
	UP_STREAM(4),
	/**
	 * 下行数据
	 */
	DOWN_STREAM(5),
	/**
	 * 断开连接
	 */
	DISCONNECT(6),
	/**
	 * http api 上下行消息
	 */
	HTTP_API(7),
	;

	private static final MessageType[] VALUES;

	static {
		// this prevent values to be assigned with the wrong order
		// and ensure valueOf to work fine
		final MessageType[] values = values();
		VALUES = new MessageType[values.length + 1];
		for (MessageType mqttMessageType : values) {
			final int value = mqttMessageType.value;
			if (VALUES[value] != null) {
				throw new AssertionError("value already in use: " + value);
			}
			VALUES[value] = mqttMessageType;
		}
	}

	private final int value;

	MessageType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static MessageType valueOf(int type) {
		if (type <= 0 || type >= VALUES.length) {
			throw new IllegalArgumentException("unknown message type: " + type);
		}
		return VALUES[type];
	}
}
