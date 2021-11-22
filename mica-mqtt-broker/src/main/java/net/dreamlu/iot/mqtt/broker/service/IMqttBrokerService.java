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

package net.dreamlu.iot.mqtt.broker.service;

import net.dreamlu.iot.mqtt.broker.model.ServerNode;

import java.util.List;

/**
 * mqtt broker 服务
 *
 * @author L.cm
 */
public interface IMqttBrokerService {

	/**
	 * 获取服务节点
	 *
	 * @return 服务节点集合
	 */
	List<ServerNode> getNodes();

	/**
	 * 获取所有在线的客户端
	 *
	 * @return 在线数
	 */
	long getOnlineClientSize();

	/**
	 * 获取所有在线的客户端
	 *
	 * @return 客户端集合
	 */
	List<String> getOnlineClients();

	/**
	 * 获取所有在线的客户端
	 *
	 * @param nodeName 集群节点
	 * @return 在线数
	 */
	long getOnlineClientSize(String nodeName);

	/**
	 * 获取所有在线的客户端
	 *
	 * @param nodeName 集群节点
	 * @return 客户端集合
	 */
	List<String> getOnlineClients(String nodeName);

}
