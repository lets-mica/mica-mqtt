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

import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.event.IMqttConnectStatusListener;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import net.dreamlu.iot.mqtt.core.server.store.IMqttMessageStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.core.ChannelContext;
import org.tio.core.DefaultAioListener;
import org.tio.core.Tio;
import org.tio.utils.hutool.StrUtil;

/**
 * mqtt 服务监听
 *
 * @author L.cm
 */
public class MqttServerAioListener extends DefaultAioListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttServerAioListener.class);
	private final IMqttMessageStore messageStore;
	private final IMqttSessionManager sessionManager;
	private final IMqttMessageDispatcher messageDispatcher;
	private final IMqttConnectStatusListener connectStatusListener;

	public MqttServerAioListener(MqttServerCreator serverCreator) {
		this.messageStore = serverCreator.getMessageStore();
		this.sessionManager = serverCreator.getSessionManager();
		this.messageDispatcher = serverCreator.getMessageDispatcher();
		this.connectStatusListener = serverCreator.getConnectStatusListener();
	}

	@Override
	public boolean onHeartbeatTimeout(ChannelContext context, Long interval, int heartbeatTimeoutCount) {
		String clientId = context.getBsId();
		logger.info("Mqtt HeartbeatTimeout clientId:{} interval:{} count:{}", clientId, interval, heartbeatTimeoutCount);
		return false;
	}

	@Override
	public void onBeforeClose(ChannelContext context, Throwable throwable, String remark, boolean isRemove) {
		// 1. 业务 id
		String clientId = context.getBsId();
		// 2. 判断是否正常断开
		boolean isNotNormalDisconnect = context.get(MqttConst.DIS_CONNECTED) == null;
		if (isNotNormalDisconnect || throwable != null) {
			logger.error("Mqtt server close clientId isBlank, remark:{} isRemove:{}", remark, isRemove, throwable);
		} else {
			logger.info("Mqtt server close clientId:{} remark:{} isRemove:{}", clientId, remark, isRemove);
		}
		// 3. 业务 id 不能为空
		if (StrUtil.isBlank(clientId)) {
			return;
		}
		// 4. 对于异常断开连接，处理遗嘱消息
		sendWillMessage(context, clientId);
		// 5. 会话清理
		cleanSession(clientId);
		// 6. 解绑 clientId
		Tio.unbindBsId(context);
		// 7. 下线事件
		notify(context, clientId);
	}

	private void sendWillMessage(ChannelContext context, String clientId) {
		// 1. 判断是否正常断开
		Object normalDisconnectMark = context.get(MqttConst.DIS_CONNECTED);
		if (normalDisconnectMark != null) {
			return;
		}
		// 2. 发送遗嘱消息
		try {
			Message willMessage = messageStore.getWillMessage(clientId);
			if (willMessage == null) {
				return;
			}
			boolean result = messageDispatcher.send(willMessage);
			logger.info("Mqtt server clientId:{} send willMessage result:{}.", clientId, result);
			// 4. 清理遗嘱消息
			messageStore.clearWillMessage(clientId);
		} catch (Throwable throwable) {
			logger.error("Mqtt server clientId:{} send willMessage error.", clientId, throwable);
		}
	}

	private void cleanSession(String clientId) {
		try {
			sessionManager.remove(clientId);
		} catch (Throwable throwable) {
			logger.error("Mqtt server clientId:{} session clean error.", clientId, throwable);
		}
	}

	private void notify(ChannelContext context, String clientId) {
		try {
			connectStatusListener.offline(context, clientId);
		} catch (Throwable throwable) {
			logger.error("Mqtt server clientId:{} offline notify error.", clientId, throwable);
		}
	}
}
