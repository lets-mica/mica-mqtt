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

package net.dreamlu.iot.mqtt.core.client;

import net.dreamlu.iot.mqtt.codec.MqttMessageIdVariableHeader;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * mqtt 客户端的消息 id
 *
 * @author L.cm
 */
public enum MqttClientMessageId {
	/**
	 * 实例
	 */
	INSTANCE(new AtomicInteger(1));

	private final AtomicInteger value;

	MqttClientMessageId(AtomicInteger value) {
		this.value = value;
	}

	public int getMessageId() {
		this.value.compareAndSet(0xffff, 1);
		return this.value.getAndIncrement();
	}

	public static int getId() {
		return INSTANCE.getMessageId();
	}

	public static MqttMessageIdVariableHeader getVariableHeader() {
		return MqttMessageIdVariableHeader.from(getId());
	}

}
