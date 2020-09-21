package net.dreamlu.iot.mqtt.client;

import net.dreamlu.iot.mqtt.codec.*;
import net.dreamlu.iot.mqtt.core.client.MqttClientProcessor;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;

import java.nio.ByteBuffer;

/**
 * 示例客户端处理
 *
 * @author L.cm
 */
public class MqttClientProcessorImpl implements MqttClientProcessor {

	@Override
	public void processConAck(ChannelContext context, MqttConnAckMessage message) {
		MqttConnectReturnCode returnCode = message.variableHeader().connectReturnCode();
		switch (message.variableHeader().connectReturnCode()) {
			case CONNECTION_ACCEPTED:
				System.out.println("MQTT 连接成功！");
				break;
			case CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD:
			case CONNECTION_REFUSED_IDENTIFIER_REJECTED:
			case CONNECTION_REFUSED_NOT_AUTHORIZED:
			case CONNECTION_REFUSED_SERVER_UNAVAILABLE:
			case CONNECTION_REFUSED_UNACCEPTABLE_PROTOCOL_VERSION:
			default:
				Tio.close(context, "MqttClient connect error.");
				context.setClosed(true);
				throw new IllegalStateException("MqttClient connect error ReturnCode:" + returnCode);
		}
	}

	@Override
	public void processSubAck(MqttSubAckMessage message) {
		System.out.println(message);
	}

	@Override
	public void processPublish(ChannelContext context, MqttPublishMessage message) {
		ByteBuffer byteBuffer = message.payload();
		if (byteBuffer != null) {
			System.out.println(ByteBufferUtil.toString(byteBuffer));
		}
	}

	@Override
	public void processUnSubAck(MqttUnsubAckMessage message) {
		System.out.println(message);
	}

	@Override
	public void processPubAck(MqttPubAckMessage message) {
		System.out.println(message);
	}

	@Override
	public void processPubRec(ChannelContext context, MqttMessage message) {
		System.out.println(message);
	}

	@Override
	public void processPubRel(ChannelContext context, MqttMessage message) {
		System.out.println(message);
	}

	@Override
	public void processPubComp(MqttMessage message) {
		System.out.println(message);
	}
}
