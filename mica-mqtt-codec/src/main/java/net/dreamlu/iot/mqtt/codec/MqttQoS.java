/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package net.dreamlu.iot.mqtt.codec;

public enum MqttQoS {

	/**
	 * QoS level 0 至多发送一次，发送即丢弃。没有确认消息，也不知道对方是否收到。
	 */
	AT_MOST_ONCE(0),
	/**
	 * QoS level 1 都要在可变头部中附加一个16位的消息ID，SUBSCRIBE 和 UNSUBSCRIBE 消息使用 QoS level 1。
	 */
	AT_LEAST_ONCE(1),
	/**
	 * QoS level 2 仅仅在 PUBLISH 类型消息中出现，要求在可变头部中要附加消息ID。
	 */
	EXACTLY_ONCE(2),
	/**
	 * 失败
	 */
	FAILURE(0x80);

    private final int value;

    MqttQoS(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

    public static MqttQoS valueOf(int value) {
        switch (value) {
        case 0:
            return AT_MOST_ONCE;
        case 1:
            return AT_LEAST_ONCE;
        case 2:
            return EXACTLY_ONCE;
        case 0x80:
            return FAILURE;
        default:
            throw new IllegalArgumentException("invalid QoS: " + value);
        }
    }
}
