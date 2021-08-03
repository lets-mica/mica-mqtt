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
	private boolean enable = false;
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

	private Integer timeout = 120000;
	private long readBufferSize = MqttConstant.DEFAULT_MAX_BYTES_IN_MESSAGE;
	private int keepAliveSecs = 60;
	private boolean reconnect = true;
	private long reInterval = 120000;
	private String clientId;
	private boolean cleanSession = true;
	private MqttVersion version = MqttVersion.MQTT_3_1_1;
	private ByteBufferAllocator bufferAllocator = ByteBufferAllocator.HEAP;

}
