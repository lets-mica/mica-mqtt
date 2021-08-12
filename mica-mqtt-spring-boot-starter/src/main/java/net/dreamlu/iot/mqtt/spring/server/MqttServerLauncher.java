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

package net.dreamlu.iot.mqtt.spring.server;

import lombok.RequiredArgsConstructor;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.MqttServerCreator;
import net.dreamlu.iot.mqtt.core.server.websocket.MqttWsMsgHandler;
import org.springframework.context.SmartLifecycle;
import org.springframework.core.Ordered;
import org.tio.server.ServerTioConfig;
import org.tio.server.TioServer;
import org.tio.websocket.common.WsTioUuid;
import org.tio.websocket.server.WsServerAioHandler;
import org.tio.websocket.server.WsServerAioListener;
import org.tio.websocket.server.WsServerConfig;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.io.IOException;

/**
 * MqttServer 启动器
 *
 * @author L.cm
 */
@RequiredArgsConstructor
public class MqttServerLauncher implements SmartLifecycle, Ordered {
	private final MqttServerCreator serverCreator;
	private final MqttServer mqttServer;
	private boolean running = false;

	@Override
	public void start() {
		// 1. 启动 mqtt tcp server
		TioServer tioServer = mqttServer.getTioServer();
		try {
			tioServer.start(serverCreator.getIp(), serverCreator.getPort());
			running = true;
		} catch (IOException e) {
			throw new IllegalStateException("Mica mqtt server start fail.", e);
		}
		// 2. 启动 mqtt websocket server
		if (serverCreator.isWebsocketEnable()) {
			ServerTioConfig tioConfig = tioServer.getServerTioConfig();
			WsServerConfig wsServerConfig = new WsServerConfig(serverCreator.getWebsocketPort(), false);
			IWsMsgHandler mqttWsMsgHandler = new MqttWsMsgHandler(tioConfig.getServerAioHandler());
			WsServerAioHandler wsServerAioHandler = new WsServerAioHandler(wsServerConfig, mqttWsMsgHandler);
			WsServerAioListener wsServerAioListener = new WsServerAioListener();
			ServerTioConfig wsTioConfig = new ServerTioConfig(tioConfig.getName() + "-Websocket", wsServerAioHandler, wsServerAioListener);
			wsTioConfig.setHeartbeatTimeout(0);
			wsTioConfig.setTioUuid(new WsTioUuid());
			wsTioConfig.setReadBufferSize(1024 * 30);
			TioServer websocketServer = new TioServer(wsTioConfig);
			mqttServer.setTioWsServer(websocketServer);
			wsTioConfig.share(tioConfig);
			try {
				websocketServer.start(tioServer.getServerNode().getIp(), wsServerConfig.getBindPort());
			} catch (IOException e) {
				throw new IllegalStateException("Mica mqtt websocket server start fail.", e);
			}
		}
	}

	@Override
	public void stop() {
		if (mqttServer != null) {
			mqttServer.stop();
		}
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

	@Override
	public int getPhase() {
		return DEFAULT_PHASE;
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
