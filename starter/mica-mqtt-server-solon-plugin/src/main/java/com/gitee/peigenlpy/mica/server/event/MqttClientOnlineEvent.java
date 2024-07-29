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

package com.gitee.peigenlpy.mica.server.event;

import lombok.Data;

import java.io.Serializable;

/**
 * 客户端断开事件
 *
 * @author L.cm
 */
@Data
public class MqttClientOnlineEvent implements Serializable {

	/**
	 * 客户端 id
	 */
	private String clientId;
	/**
	 * 用户名
	 */
	private String username;
	/**
	 * ip
	 */
	private String ipAddress;
	/**
	 * 端口
	 */
	private int port;
	/**
	 * keepalive
	 */
	private long keepalive;
	/**
	 * 时间戳
	 */
	private long ts;

}
