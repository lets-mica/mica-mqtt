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

package net.dreamlu.iot.mqtt.core.server;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.model.Subscribe;

import java.util.List;

/**
 * mqtt 服务端 订阅管理
 *
 * @author L.cm
 */
public interface IMqttServerSubscribeManager {

	/**
	 * 添加订阅存储
	 *
	 * @param topicFilter topicFilter
	 * @param clientId    客户端 Id
	 * @param mqttQoS     MqttQoS
	 */
	void add(String topicFilter, String clientId, MqttQoS mqttQoS);

	/**
	 * 删除订阅
	 *
	 * @param topicFilter topicFilter
	 * @param clientId    客户端 Id
	 */
	void remove(String topicFilter, String clientId);

	/**
	 * 删除订阅
	 *
	 * @param clientId 客户端 Id
	 */
	void remove(String clientId);

	/**
	 * 查找订阅信息
	 *
	 * @param topicName topicName
	 * @param clientId  客户端 Id
	 * @return 订阅存储列表
	 */
	List<Subscribe> search(String topicName, String clientId);

	/**
	 * 查找订阅信息
	 *
	 * @param topicName topicName
	 * @return 订阅存储列表
	 */
	List<Subscribe> search(String topicName);

	/**
	 * 清理
	 */
	void clean();

}
