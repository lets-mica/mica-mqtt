/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package net.dreamlu.iot.mqtt.codec;

/**
 * Return Code of {@link MqttConnAckMessage}
 *
 * @author netty
 */
public enum MqttConnectReasonCode implements MqttReasonCode {
	/**
	 * 连接被接受
	 */
	CONNECTION_ACCEPTED((byte) 0x00),

	//MQTT 3 codes
	/**
	 * 连接被拒绝，不可接受的协议版本
	 */
	CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION((byte) 0X01),

	/**
	 * 连接被拒绝，标识符被拒绝
	 */
	CONNECTION_REFUSED_IDENTIFIER_REJECTED((byte) 0x02),

	/**
	 * 连接被拒绝，服务器不可用
	 */
	CONNECTION_REFUSED_SERVER_UNAVAILABLE((byte) 0x03),

	/**
	 * 连接被拒绝，用户名或密码错误
	 */
	CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD((byte) 0x04),

	/**
	 * 连接被拒绝，未经授权
	 */
	CONNECTION_REFUSED_NOT_AUTHORIZED((byte) 0x05),

	//MQTT 5 codes
	/**
	 * 连接被拒绝，未指定错误
	 */
	CONNECTION_REFUSED_UNSPECIFIED_ERROR((byte) 0x80),

	/**
	 * 连接被拒绝，格式错误的数据包
	 */
	CONNECTION_REFUSED_MALFORMED_PACKET((byte) 0x81),

	/**
	 * 连接被拒绝，协议错误
	 */
	CONNECTION_REFUSED_PROTOCOL_ERROR((byte) 0x82),

	/**
	 * 连接被拒绝，实现特定的错误
	 */
	CONNECTION_REFUSED_IMPLEMENTATION_SPECIFIC((byte) 0x83),

	/**
	 * 连接被拒绝，不支持的协议版本
	 */
	CONNECTION_REFUSED_UNSUPPORTED_PROTOCOL_VERSION((byte) 0x84),

	/**
	 * 连接被拒绝，客户端标识符无效
	 */
	CONNECTION_REFUSED_CLIENT_IDENTIFIER_NOT_VALID((byte) 0x85),

	/**
	 * 连接被拒绝，用户名或密码错误
	 */
	CONNECTION_REFUSED_BAD_USERNAME_OR_PASSWORD((byte) 0x86),

	/**
	 * 连接被拒绝，未经授权
	 */
	CONNECTION_REFUSED_NOT_AUTHORIZED_5((byte) 0x87),

	/**
	 * 连接被拒绝，服务器不可用
	 */
	CONNECTION_REFUSED_SERVER_UNAVAILABLE_5((byte) 0x88),

	/**
	 * 连接被拒绝，服务器忙
	 */
	CONNECTION_REFUSED_SERVER_BUSY((byte) 0x89),

	/**
	 * 连接被拒绝，被禁止
	 */
	CONNECTION_REFUSED_BANNED((byte) 0x8A),

	/**
	 * 连接被拒绝，身份验证方法错误
	 */
	CONNECTION_REFUSED_BAD_AUTHENTICATION_METHOD((byte) 0x8C),

	/**
	 * 连接被拒绝，主题名无效
	 */
	CONNECTION_REFUSED_TOPIC_NAME_INVALID((byte) 0x90),

	/**
	 * 连接被拒绝，数据包过大
	 */
	CONNECTION_REFUSED_PACKET_TOO_LARGE((byte) 0x95),

	/**
	 * 连接被拒绝，超出配额限制
	 */
	CONNECTION_REFUSED_QUOTA_EXCEEDED((byte) 0x97),

	/**
	 * 连接被拒绝，有效载荷格式无效
	 */
	CONNECTION_REFUSED_PAYLOAD_FORMAT_INVALID((byte) 0x99),

	/**
	 * 连接被拒绝，不支持保留消息
	 */
	CONNECTION_REFUSED_RETAIN_NOT_SUPPORTED((byte) 0x9A),

	/**
	 * 连接被拒绝，不支持的QoS级别
	 */
	CONNECTION_REFUSED_QOS_NOT_SUPPORTED((byte) 0x9B),

	/**
	 * 连接被拒绝，使用其他服务器
	 */
	CONNECTION_REFUSED_USE_ANOTHER_SERVER((byte) 0x9C),

	/**
	 * 连接被拒绝，服务器已移动
	 */
	CONNECTION_REFUSED_SERVER_MOVED((byte) 0x9D),

	/**
	 * 连接被拒绝，连接速率超过限制
	 */
	CONNECTION_REFUSED_CONNECTION_RATE_EXCEEDED((byte) 0x9F);

	private static final MqttConnectReasonCode[] VALUES = new MqttConnectReasonCode[160];

	static {
		ReasonCodeUtils.fillValuesByCode(VALUES, values());
	}

	private final byte byteValue;

	MqttConnectReasonCode(byte byteValue) {
		this.byteValue = byteValue;
	}

	public static MqttConnectReasonCode valueOf(byte b) {
		return ReasonCodeUtils.codeLoopUp(VALUES, b, "Connect");
	}

	@Override
	public byte value() {
		return byteValue;
	}

}
