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

package net.dreamlu.iot.mqtt.core.server.http.core;

import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.TioConfig;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.server.intf.ServerAioListener;

/**
 * 兼容 websocket，参考 HTTPServerAioListener
 *
 * @author tanyaowu
 * @author L.cm
 */
public class MqttWebServerAioListener implements ServerAioListener {

	@Override
	public void onAfterConnected(ChannelContext context, boolean isConnected, boolean isReconnect) {

	}

	@Override
	public void onAfterDecoded(ChannelContext context, Packet packet, int packetSize) {

	}

	@Override
	public void onAfterSent(ChannelContext context, Packet packet, boolean isSentSuccess) {
		if (!(packet instanceof HttpResponse)) {
			return;
		}
		// 1. 短链接数据解绑
		TioConfig tioConfig = context.getTioConfig();
		tioConfig.groups.unbind(context);
		tioConfig.bsIds.unbind(context);
		tioConfig.ids.unbind(context);
		tioConfig.clientNodes.remove(context);
		tioConfig.tokens.unbind(context);
		// 2. 关闭
		HttpResponse httpResponse = (HttpResponse) packet;
		HttpRequest request = httpResponse.getHttpRequest();
		if (request != null) {
			if (request.httpConfig.compatible1_0) {
				if (HttpConst.HttpVersion.V1_0.equals(request.requestLine.version)) {
					if (!HttpConst.RequestHeaderValue.Connection.keep_alive.equals(request.getConnection())) {
						Tio.remove(context, "http 请求头Connection!=keep-alive：" + request.getRequestLine());
					}
				} else {
					if (HttpConst.RequestHeaderValue.Connection.close.equals(request.getConnection())) {
						Tio.remove(context, "http 请求头Connection=close：" + request.getRequestLine());
					}
				}
			} else {
				if (HttpConst.RequestHeaderValue.Connection.close.equals(request.getConnection())) {
					Tio.remove(context, "http 请求头Connection=close：" + request.getRequestLine());
				}
			}
		}
	}

	@Override
	public void onBeforeClose(ChannelContext context, Throwable throwable, String remark, boolean isRemove) {

	}

	@Override
	public void onAfterHandled(ChannelContext context, Packet packet, long cost) throws Exception {

	}

	@Override
	public void onAfterReceivedBytes(ChannelContext context, int receivedBytes) throws Exception {

	}

	@Override
	public boolean onHeartbeatTimeout(ChannelContext context, Long interval, int heartbeatTimeoutCount) {
		return false;
	}

}
