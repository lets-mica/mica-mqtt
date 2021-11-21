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

package net.dreamlu.iot.mqtt.broker.cluster;

import lombok.RequiredArgsConstructor;
import net.dreamlu.iot.mqtt.broker.enums.RedisKeys;
import net.dreamlu.iot.mqtt.broker.model.ServerNode;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.MqttServerCreator;
import net.dreamlu.mica.core.utils.CharPool;
import net.dreamlu.mica.core.utils.INetUtil;
import net.dreamlu.mica.redis.cache.MicaRedisCache;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;

/**
 * mqtt 服务节点管理
 *
 * @author L.cm
 */
@RequiredArgsConstructor
public class RedisMqttServerManage implements SmartInitializingSingleton, DisposableBean {
	private final MicaRedisCache redisCache;
	private final MqttServer mqttServer;

	@Override
	public void afterSingletonsInstantiated() {
		redisCache.sAdd(RedisKeys.SERVER_NODES.getKey(), getServerNode());
	}

	@Override
	public void destroy() throws Exception {
		redisCache.sRem(RedisKeys.SERVER_NODES.getKey(), getServerNode());
	}

	private ServerNode getServerNode() {
		MqttServerCreator serverCreator = mqttServer.getServerCreator();
		String nodeName = serverCreator.getNodeName();
		String ip = INetUtil.getHostIp();
		int port = serverCreator.getPort();
		ServerNode node = new ServerNode();
		node.setName(nodeName);
		node.setPeerHost(ip + CharPool.COLON + port);
		return node;
	}

}
