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

package net.dreamlu.iot.mqtt.core.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientGroupStat;
import org.tio.client.TioClientConfig;
import org.tio.client.intf.TioClientHandler;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.intf.Packet;
import org.tio.utils.timer.Timer;
import org.tio.utils.timer.TimerTask;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * mqtt 客户端心跳任务
 *
 * @author L.cm
 */
public class MqttClientHeartbeatTask extends TimerTask {
	private static final Logger logger = LoggerFactory.getLogger(MqttClientHeartbeatTask.class);
	private final Timer timer;
	private final TioClientConfig clientTioConfig;
	private final ClientGroupStat clientGroupStat;
	private final TioClientHandler tioHandler;
	private final String id;
	/**
	 * 心跳超时时间，采用 keepAliveSecs - delayMs（定时的时间），经量保证 60s 发一次心跳
	 */
	private final long intervalTimeout;

	public MqttClientHeartbeatTask(Timer timer, TioClientConfig clientTioConfig, int keepAliveSecs) {
		super(TimeUnit.SECONDS.toMillis(keepAliveSecs / 3));
		this.timer = timer;
		this.clientTioConfig = clientTioConfig;
		this.clientGroupStat = (ClientGroupStat) clientTioConfig.groupStat;
		this.tioHandler = clientTioConfig.getTioClientHandler();
		this.id = clientTioConfig.getId();
		this.intervalTimeout = TimeUnit.SECONDS.toMillis(keepAliveSecs) - delayMs;
	}

	@Override
	public void run() {
		// 1. 添加 task，保持后续执行
		timer.add(this);
		// 2. 已经停止，跳过
		if (clientTioConfig.isStopped()) {
			return;
		}
		Set<ChannelContext> set = clientTioConfig.connecteds;
		long currTime = System.currentTimeMillis();
		try {
			for (ChannelContext entry : set) {
				ClientChannelContext channelContext = (ClientChannelContext) entry;
				if (channelContext.isClosed() || channelContext.isRemoved()) {
					continue;
				}
				long interval = currTime - channelContext.stat.latestTimeOfSentPacket;
				if (interval > intervalTimeout) {
					Packet packet = tioHandler.heartbeatPacket(channelContext);
					if (packet != null) {
						boolean result = Tio.send(channelContext, packet);
						if (clientTioConfig.debug && logger.isInfoEnabled()) {
							logger.info("{} 发送心跳包 result:{}", channelContext, result);
						}
					}
				}
			}
			// 打印连接信息
			if (clientTioConfig.debug && logger.isInfoEnabled()) {
				if (clientTioConfig.statOn) {
					logger.info("[{}]: curr:{}, closed:{}, received:({}p)({}b), handled:{}, sent:({}p)({}b)]",
						id, set.size(), clientGroupStat.closed.sum(),
						clientGroupStat.receivedPackets.sum(), clientGroupStat.receivedBytes.sum(), clientGroupStat.handledPackets.sum(),
						clientGroupStat.sentPackets.sum(), clientGroupStat.sentBytes.sum());
				} else {
					logger.info("[{}]: curr:{}, closed:{}]",
						id, set.size(), clientGroupStat.closed.sum());
				}
			}
		} catch (Throwable e) {
			logger.error(e.getMessage(), e);
		}
	}
}
