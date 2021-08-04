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

package net.dreamlu.iot.mqtt.spring.client;

import lombok.Getter;
import lombok.Setter;
import net.dreamlu.iot.mqtt.codec.ByteBufferAllocator;
import net.dreamlu.iot.mqtt.codec.MqttConstant;
import net.dreamlu.iot.mqtt.codec.MqttVersion;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MqttClient 配置
 *
 * @author wsq（冷月宫主）
 */
@Getter
@Setter
@ConfigurationProperties(MqttClientProperties.PREFIX)
public class MqttClientProperties {

	/**
	 * 配置前缀
	 */
	public static final String PREFIX = "mqtt.client";
	/**
	 * 是否启用，默认：false
	 */
	private boolean enabled = false;
	/**
	 * 名称，默认：Mica-Mqtt-Client
	 */
	private String name = "Mica-Mqtt-Client";
	/**
	 * 服务端 ip，默认：127.0.0.1
	 */
	private String ip = "127.0.0.1";
	/**
	 * 端口，默认：1883
	 */
	private int port = 1883;
	/**
	 * 用户名
	 */
	private String userName;
	/**
	 * 密码
	 */
	private String password;
	/**
	 * 客户端ID
	 */
	private String clientId;
	/**
	 * 超时时间，单位：秒，t-io 配置，可为 null
	 */
	private Integer timeout;
	/**
	 * t-io 每次消息读取长度，跟 maxBytesInMessage 相关
	 */
	private int readBufferSize = MqttConstant.DEFAULT_MAX_BYTES_IN_MESSAGE;
	/**
	 * Keep Alive (s)
	 */
	private int keepAliveSecs = 60;
	/**
	 * 自动重连
	 */
	private boolean reconnect = true;
	/**
	 * 重连重试时间，单位：毫秒，默认：5000
	 */
	private Long reInterval;
	/**
	 * mqtt 协议，默认：MQTT_3_1_1
	 */
	private MqttVersion version = MqttVersion.MQTT_3_1_1;
	/**
	 * 清除会话
	 * <p>
	 * false 表示如果订阅的客户机断线了，那么要保存其要推送的消息，如果其重新连接时，则将这些消息推送。
	 * true 表示消除，表示客户机是第一次连接，消息所以以前的连接信息。
	 * </p>
	 */
	private boolean cleanSession = true;
	/**
	 * ByteBuffer Allocator，支持堆内存和堆外内存，默认为：堆内存
	 */
	private ByteBufferAllocator bufferAllocator = ByteBufferAllocator.HEAP;

}
