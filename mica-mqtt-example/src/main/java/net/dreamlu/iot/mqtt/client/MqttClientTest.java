package net.dreamlu.iot.mqtt.client;

import net.dreamlu.iot.mqtt.codec.*;
import net.dreamlu.iot.mqtt.core.client.MqttClientAioHandler;
import net.dreamlu.iot.mqtt.core.client.MqttClientAioListener;
import net.dreamlu.iot.mqtt.core.client.MqttClientConfig;
import net.dreamlu.iot.mqtt.core.client.MqttClientProcessor;
import org.tio.client.ClientChannelContext;
import org.tio.client.ClientTioConfig;
import org.tio.client.ReconnConf;
import org.tio.client.TioClient;
import org.tio.client.intf.ClientAioHandler;
import org.tio.client.intf.ClientAioListener;
import org.tio.core.Node;
import org.tio.core.Tio;

import java.nio.ByteBuffer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 客户端测试
 *
 * @author L.cm
 */
public class MqttClientTest {

	public static void main(String[] args) throws Exception {
		MqttClientProcessor processor = new MqttClientProcessorImpl();
		ClientAioHandler clientAioHandler = new MqttClientAioHandler(processor);
		// 暂时用 set 后期抽成 builder
		MqttClientConfig clientConfig = new MqttClientConfig();
		clientConfig.setClientId("MqttClientTest");
		clientConfig.setUsername("admin");
		clientConfig.setPassword("123456");
		//
		ClientAioListener clientAioListener = new MqttClientAioListener(clientConfig);
		ReconnConf reconnConf = new ReconnConf();
		ClientTioConfig tioConfig = new ClientTioConfig(clientAioHandler, clientAioListener, reconnConf);

		TioClient tioClient = new TioClient(tioConfig);
		ClientChannelContext context = tioClient.connect(new Node("127.0.0.1", 1883), 1000);

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				if (!context.isClosed) {
					MqttPublishMessage message = (MqttPublishMessage) MqttMessageFactory.newMessage(
						new MqttFixedHeader(MqttMessageType.PUBLISH, false, MqttQoS.AT_MOST_ONCE, false, 0),
						new MqttPublishVariableHeader("testtopicxx", 0), ByteBuffer.wrap("mica最牛皮".getBytes()));
					Tio.send(context, message);
				}
			}
		}, 1000, 2000);

	}
}
