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
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import net.dreamlu.mica.redis.cache.MicaRedisCache;
import org.tio.core.ChannelContext;

/**
 * mqtt 连接监听
 *
 * @author L.cm
 */
@RequiredArgsConstructor
public class RedisMqttConnectStatusListener implements IMqttConnectStatusListener {
	private final MicaRedisCache redisCache;
	private final String connectStatusKey;

	@Override
	public void online(ChannelContext context, String clientId) {
		redisCache.sAdd(connectStatusKey, clientId);
	}

	@Override
	public void offline(ChannelContext context, String clientId) {
		redisCache.sRem(connectStatusKey, clientId);
	}
}
