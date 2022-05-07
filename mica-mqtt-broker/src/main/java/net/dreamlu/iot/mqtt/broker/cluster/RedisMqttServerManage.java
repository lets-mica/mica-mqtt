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

import net.dreamlu.iot.mqtt.broker.enums.RedisKeys;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.MqttServerCreator;
import net.dreamlu.mica.core.utils.CharPool;
import net.dreamlu.mica.core.utils.INetUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * mqtt 服务节点管理
 *
 * @author L.cm
 */
public class RedisMqttServerManage implements SmartInitializingSingleton, DisposableBean {
	private final RedisTemplate<String, Object> redisTemplate;
	private final String nodeName;
	private final String hostName;

	public RedisMqttServerManage(RedisTemplate<String, Object> redisTemplate, MqttServer mqttServer) {
		this.redisTemplate = redisTemplate;
		this.nodeName = mqttServer.getServerCreator().getNodeName();
		this.hostName = getHostName(mqttServer.getServerCreator());
	}

	@Override
	public void afterSingletonsInstantiated() {
		redisTemplate.opsForValue().set(RedisKeys.SERVER_NODES.getKey(nodeName), hostName);
	}

	@Override
	public void destroy() throws Exception {
		redisTemplate.delete(RedisKeys.SERVER_NODES.getKey(nodeName));
	}

	private static String getHostName(MqttServerCreator mqttServerCreator) {
		return INetUtil.getHostIp() + CharPool.COLON + mqttServerCreator.getPort();
	}

}
