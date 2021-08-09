package net.dreamlu.iot.mqtt.websocket.test;

import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.core.intf.Packet;
import org.tio.http.common.HttpConst;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.server.intf.ServerAioListener;
import org.tio.websocket.common.WsSessionContext;

/**
 * 兼容 websocket，参考 HTTPServerAioListener
 *
 * @author tanyaowu
 * @author L.cm
 */
public class MqttWebServerAioListener implements ServerAioListener {

	@Override
	public void onAfterConnected(ChannelContext channelContext, boolean isConnected, boolean isReconnect) {
		WsSessionContext wsSessionContext = new WsSessionContext();
		channelContext.set(wsSessionContext);
	}

	@Override
	public void onAfterDecoded(ChannelContext channelContext, Packet packet, int packetSize) {

	}

	@Override
	public void onAfterSent(ChannelContext channelContext, Packet packet, boolean isSentSuccess) {
		if (!(packet instanceof HttpResponse)) {
			return;
		}
		HttpResponse httpResponse = (HttpResponse) packet;
		HttpRequest request = httpResponse.getHttpRequest();
		if (request != null) {
			if (request.httpConfig.compatible1_0) {
				if (HttpConst.HttpVersion.V1_0.equals(request.requestLine.version)) {
					if (!HttpConst.RequestHeaderValue.Connection.keep_alive.equals(request.getConnection())) {
						Tio.remove(channelContext, "http 请求头Connection!=keep-alive：" + request.getRequestLine());
					}
				} else {
					if (HttpConst.RequestHeaderValue.Connection.close.equals(request.getConnection())) {
						Tio.remove(channelContext, "http 请求头Connection=close：" + request.getRequestLine());
					}
				}
			} else {
				if (HttpConst.RequestHeaderValue.Connection.close.equals(request.getConnection())) {
					Tio.remove(channelContext, "http 请求头Connection=close：" + request.getRequestLine());
				}
			}
		}
	}

	@Override
	public void onBeforeClose(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {

	}

	@Override
	public void onAfterHandled(ChannelContext channelContext, Packet packet, long cost) throws Exception {

	}

	@Override
	public void onAfterReceivedBytes(ChannelContext channelContext, int receivedBytes) throws Exception {

	}

	@Override
	public boolean onHeartbeatTimeout(ChannelContext channelContext, Long interval, int heartbeatTimeoutCount) {
		return false;
	}

}
