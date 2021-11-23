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

package net.dreamlu.iot.mqtt.core.server.enums;

/**
 * 消息类型
 *
 * @author L.cm
 */
public enum MessageType {

	/**
	 * 连接
	 */
	CONNECT((byte) 1),
	/**
	 * 主题订阅
	 */
	SUBSCRIBE((byte) 2),
	/**
	 * 取消订阅
	 */
	UNSUBSCRIBE((byte) 3),
	/**
	 * 上行数据
	 */
	UP_STREAM((byte) 4),
	/**
	 * 下行数据
	 */
	DOWN_STREAM((byte) 5),
	/**
	 * 断开连接
	 */
	DISCONNECT((byte) 6),
	;

	private final byte value;

	MessageType(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
}
