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

package net.dreamlu.iot.mqtt.core.server;

import net.dreamlu.iot.mqtt.core.server.store.IMqttSubscribeStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.DefaultAioListener;
import org.tio.core.Tio;

/**
 * mqtt 服务监听
 *
 * @author L.cm
 */
public class MqttServerAioListener extends DefaultAioListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttServerAioListener.class);
	private final IMqttSubscribeStore subscribeStore;

	public MqttServerAioListener(IMqttSubscribeStore subscribeStore) {
		this.subscribeStore = subscribeStore;
	}

	@Override
	public boolean onHeartbeatTimeout(ChannelContext context, Long interval, int heartbeatTimeoutCount) {
		String clientId = context.getBsId();
		logger.info("Mqtt HeartbeatTimeout clientId:{} interval:{} count:{}", clientId, interval, heartbeatTimeoutCount);
		return true;
	}

	@Override
	public void onBeforeClose(ChannelContext context, Throwable throwable, String remark, boolean isRemove) {
		String clientId = context.getBsId();
		logger.info("Mqtt server close clientId:{} remark:{} isRemove:{}", clientId, remark, isRemove);
		// 对于异常，处理遗嘱消息
		if (throwable != null) {
			// TODO 遗嘱消息处理
		}
		subscribeStore.remove(clientId);
		Tio.unbindBsId(context);
	}

}
