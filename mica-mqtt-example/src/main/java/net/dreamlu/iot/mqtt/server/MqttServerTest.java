package net.dreamlu.iot.mqtt.server;

import net.dreamlu.iot.mqtt.codec.*;
import net.dreamlu.iot.mqtt.core.server.MqttServerAioHandler;
import net.dreamlu.iot.mqtt.core.server.MqttServerAioListener;
import net.dreamlu.iot.mqtt.core.server.MqttServerProcessor;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.server.ServerTioConfig;
import org.tio.server.TioServer;
import org.tio.server.intf.ServerAioHandler;
import org.tio.server.intf.ServerAioListener;
import org.tio.utils.lock.SetWithLock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * mqtt 服务端测试
 *
 * @author L.cm
 */
public class MqttServerTest {

	public static void main(String[] args) throws IOException {
		int socketPort = 1883;
		MqttServerProcessor brokerHandler = new MqttBrokerProcessorImpl();
		ByteBufferAllocator bufferAllocator = ByteBufferAllocator.HEAP;
		// 处理消息
		ServerAioHandler handler = new MqttServerAioHandler(bufferAllocator, brokerHandler);
		// 监听
		ServerAioListener listener = new MqttServerAioListener();
		// 配置
		ServerTioConfig config = new ServerTioConfig("mqtt-server", handler, listener);
		// 设置timeout
		config.setHeartbeatTimeout(500);
		TioServer tioServer = new TioServer(config);
		// 不校验版本号，社区版设置无效
		tioServer.setCheckLastVersion(false);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				SetWithLock<ChannelContext> contextSet = Tio.getAll(config);
				Set<ChannelContext> channelContexts = contextSet.getObj();
				channelContexts.forEach(context -> {
					MqttPublishMessage message = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false, 0),
						new MqttPublishVariableHeader("/test/123", 0), ByteBuffer.wrap("mica最牛皮".getBytes()));
					Tio.send(context, message);
				});
			}
		}, 1000, 2000);

		// 启动
		tioServer.start("127.0.0.1", socketPort);
	}
}
