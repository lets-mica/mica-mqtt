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

package net.dreamlu.iot.mqtt.core.server.event;

import java.nio.ByteBuffer;

/**
 * mqtt 设备事件
 *
 * @author L.cm
 */
@FunctionalInterface
public interface IMqttEvent {

	/**
	 * 设备上线（连接成功）
	 *
	 * @param clientId clientId
	 */
	default void online(String clientId) {

	}

	/**
	 * 监听到消息
	 *
	 * @param clientId clientId
	 * @param topic    topic
	 * @param payload  payload
	 */
	void onMessage(String clientId, String topic, ByteBuffer payload);

	/**
	 * 设备离线
	 * @param clientId clientId
	 */
	default void offline(String clientId) {

	}

}
