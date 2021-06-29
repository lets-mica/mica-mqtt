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

package net.dreamlu.iot.mqtt.core.server.store;

import net.dreamlu.iot.mqtt.codec.MqttQoS;

import java.util.List;

/**
 * 客户端订阅存储
 *
 * @author L.cm
 */
public interface IMqttSubscribeStore {

	/**
	 * 添加订阅存储
	 *
	 * @param clientId    客户端 Id
	 * @param topicFilter topicFilter
	 * @param mqttQoS     MqttQoS
	 */
	void add(String clientId, String topicFilter, MqttQoS mqttQoS);

	/**
	 * 删除订阅
	 *
	 * @param clientId    客户端 Id
	 * @param topicFilter topicFilter
	 */
	void remove(String clientId, String topicFilter);

	/**
	 * 查找订阅信息
	 *
	 * @param clientId  客户端 Id
	 * @param topicName topicName
	 * @return 订阅存储列表
	 */
	List<SubscribeStore> search(String clientId, String topicName);

}
