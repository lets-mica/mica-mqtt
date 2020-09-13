package net.dreamlu.iot.mqtt.client;

import net.dreamlu.iot.mqtt.codec.*;
import net.dreamlu.iot.mqtt.core.client.MqttClientProcessor;
import org.tio.core.ChannelContext;

import java.nio.ByteBuffer;

/**
 * 示例客户端处理
 *
 * @author L.cm
 */
public class MqttClientProcessorImpl implements MqttClientProcessor {

	@Override
	public void processConAck(ChannelContext context, MqttConnAckMessage message) {
		System.out.println(message);
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
