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
import net.dreamlu.iot.mqtt.core.server.MqttServerCreator;
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import net.dreamlu.mica.core.utils.CharPool;
import net.dreamlu.mica.redis.cache.MicaRedisCache;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.tio.core.ChannelContext;

/**
 * mqtt 连接监听
 *
 * @author L.cm
 */
@RequiredArgsConstructor
public class RedisMqttConnectStatusListener implements IMqttConnectStatusListener, SmartInitializingSingleton, DisposableBean {
	private final ApplicationContext context;
	private final MicaRedisCache redisCache;
	private final String connectStatusKey;
	private MqttServerCreator serverCreator;

	@Override
	public void online(ChannelContext context, String clientId) {
		redisCache.sAdd(getRedisKey(), clientId);
	}

	@Override
	public void offline(ChannelContext context, String clientId) {
		redisCache.sRem(getRedisKey(), clientId);
	}

	/**
	 * 设备上下线存储，key 的值为 前缀:nodeName
	 *
	 * @return redis key
	 */
	private String getRedisKey() {
		return connectStatusKey + CharPool.COLON + serverCreator.getNodeName();
	}

	@Override
	public void afterSingletonsInstantiated() {
		this.serverCreator = context.getBean(MqttServerCreator.class);
	}

	@Override
	public void destroy() throws Exception {
		// 停机时删除集合
		redisCache.del(getRedisKey());
	}
}
