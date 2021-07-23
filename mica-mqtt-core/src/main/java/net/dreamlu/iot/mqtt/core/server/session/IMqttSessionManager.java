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

package net.dreamlu.iot.mqtt.core.server.session;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.store.SubscribeStore;

import java.util.List;

/**
 * session 管理，不封装 MqttSession 实体，方便 redis 等集群处理
 *
 * @author L.cm
 */
public interface IMqttSessionManager {

	/**
	 * 添加订阅存储
	 *
	 * @param clientId    客户端 Id
	 * @param topicFilter topicFilter
	 * @param mqttQoS     MqttQoS
	 */
	void addSubscribe(String clientId, String topicFilter, MqttQoS mqttQoS);

	/**
	 * 删除订阅
	 *
	 * @param clientId    客户端 Id
	 * @param topicFilter topicFilter
	 */
	void removeSubscribe(String clientId, String topicFilter);

	/**
	 * 查找订阅信息
	 *
	 * @param clientId  客户端 Id
	 * @param topicName topicName
	 * @return 订阅存储列表
	 */
	List<SubscribeStore> searchSubscribe(String clientId, String topicName);

	/**
	 * 生成消息 Id
	 *
	 * @return messageId
	 */
	int getMessageId(String clientId);

	/**
	 * 清除 session
	 *
	 * @param clientId clientId
	 */
	void clean(String clientId);

}
