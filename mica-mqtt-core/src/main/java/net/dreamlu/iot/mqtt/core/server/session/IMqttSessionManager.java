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

package net.dreamlu.iot.mqtt.core.server.session;

import net.dreamlu.iot.mqtt.core.common.MqttPendingPublish;
import net.dreamlu.iot.mqtt.core.common.MqttPendingQos2Publish;

/**
 * session 管理，不封装 MqttSession 实体，方便 redis 等集群处理
 *
 * @author L.cm
 */
public interface IMqttSessionManager {

	/**
	 * 添加发布过程存储
	 *
	 * @param messageId      messageId
	 * @param pendingPublish MqttPendingPublish
	 */
	void addPendingPublish(String clientId, int messageId, MqttPendingPublish pendingPublish);

	/**
	 * 获取发布过程存储
	 *
	 * @param messageId messageId
	 * @return MqttPendingPublish
	 */
	MqttPendingPublish getPendingPublish(String clientId, int messageId);

	/**
	 * 删除发布过程中的存储
	 *
	 * @param messageId messageId
	 */
	void removePendingPublish(String clientId, int messageId);

	/**
	 * 添加发布过程存储
	 *
	 * @param messageId          messageId
	 * @param pendingQos2Publish MqttPendingQos2Publish
	 */
	void addPendingQos2Publish(String clientId, int messageId, MqttPendingQos2Publish pendingQos2Publish);

	/**
	 * 获取发布过程存储
	 *
	 * @param messageId messageId
	 * @return MqttPendingQos2Publish
	 */
	MqttPendingQos2Publish getPendingQos2Publish(String clientId, int messageId);

	/**
	 * 删除发布过程中的存储
	 *
	 * @param messageId messageId
	 */
	void removePendingQos2Publish(String clientId, int messageId);

	/**
	 * 生成消息 Id
	 *
	 * @return messageId
	 */
	int getMessageId(String clientId);

	/**
	 * 判断是否存在 session
	 *
	 * @param clientId clientId
	 * @return 是否存在 session
	 */
	boolean hasSession(String clientId);

	/**
	 * 清除 session
	 *
	 * @param clientId clientId
	 */
	void remove(String clientId);

	/**
	 * 清理
	 */
	void clean();

}
