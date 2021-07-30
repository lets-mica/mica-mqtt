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

package net.dreamlu.iot.mqtt.spring.server;

import lombok.Getter;
import lombok.Setter;
import net.dreamlu.iot.mqtt.codec.ByteBufferAllocator;
import net.dreamlu.iot.mqtt.codec.MqttConstant;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * MqttServer 配置
 *
 * @author L.cm
 */
@Getter
@Setter
@ConfigurationProperties("mqtt.server")
public class MqttServerProperties {

	/**
	 * 名称
	 */
	private String name = "Mica-Mqtt-Server";
	/**
	 * 服务端 ip
	 */
	private String ip = "127.0.0.1";
	/**
	 * 端口
	 */
	private int port = 1883;
	/**
	 * 心跳超时时间(单位: 毫秒 默认: 1000 * 120)，如果用户不希望框架层面做心跳相关工作，请把此值设为0或负数
	 */
	private Long heartbeatTimeout;
	/**
	 * 接收数据的 buffer size，默认：8092
	 */
	private int readBufferSize = MqttConstant.DEFAULT_MAX_BYTES_IN_MESSAGE;
	/**
	 * 消息解析最大 bytes 长度，默认：8092
	 */
	private int maxBytesInMessage = MqttConstant.DEFAULT_MAX_BYTES_IN_MESSAGE;
	/**
	 * 堆内存和堆外内存
	 */
	private ByteBufferAllocator bufferAllocator = ByteBufferAllocator.HEAP;
	/**
	 * debug
	 */
	private boolean debug = false;
	/**
	 * ssl 配置
	 */
	private Ssl ssl = new Ssl();

	public static class Ssl {
		/**
		 * 证书路径
		 */
		private String keyStorePath;
		/**
		 *
		 */
		private String trustStorePath;
		/**
		 * 证书密钥
		 */
		private String password;
	}

}
