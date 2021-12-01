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

package net.dreamlu.iot.mqtt.broker.cluster;

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import net.dreamlu.iot.mqtt.core.server.enums.MessageType;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.iot.mqtt.core.server.serializer.IMessageSerializer;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import net.dreamlu.mica.core.utils.StringUtil;
import net.dreamlu.mica.redis.cache.MicaRedisCache;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;

import java.util.Objects;

/**
 * 监听集群消息，上行和内部集群通道
 *
 * @author L.cm
 */
public class RedisMqttMessageExchangeReceiver implements MessageListener, InitializingBean {
	private final RedisTemplate<String, Object> redisTemplate;
	private final IMessageSerializer messageSerializer;
	private final String channel;
	private final MqttServer mqttServer;
	private final String nodeName;
	private final IMqttSessionManager sessionManager;

	public RedisMqttMessageExchangeReceiver(MicaRedisCache redisCache,
											IMessageSerializer messageSerializer,
											String channel,
											MqttServer mqttServer) {
		this.redisTemplate = redisCache.getRedisTemplate();
		this.messageSerializer = messageSerializer;
		this.channel = Objects.requireNonNull(channel, "Redis pub/sub channel is null.");
		this.mqttServer = mqttServer;
		this.nodeName = mqttServer.getServerCreator().getNodeName();
		this.sessionManager = mqttServer.getServerCreator().getSessionManager();
	}

	@Override
	public void onMessage(org.springframework.data.redis.connection.Message message, byte[] bytes) {
		byte[] messageBody = message.getBody();
		// 手动序列化和反序列化，避免 redis 序列化不一致问题
		Message mqttMessage = messageSerializer.deserialize(messageBody);
		if (mqttMessage == null) {
			return;
		}
		messageProcessing(mqttMessage);
	}

	private void messageProcessing(Message message) {
		MessageType messageType = message.getMessageType();
		String topic = message.getTopic();
		if (MessageType.CONNECT == messageType) {
			// 1. 如果一个 clientId 在集群多个服务上连接时断开其他的
			String node = message.getNode();
			if (nodeName.equals(node)) {
				return;
			}
			String clientId = message.getClientId();
			ChannelContext context = Tio.getByBsId(mqttServer.getServerConfig(), clientId);
			if (context != null) {
				Tio.remove(context, String.format("clientId:[%s] now bind on mqtt node:[%s]", clientId, node));
			}
		} else if (MessageType.SUBSCRIBE == messageType) {
			// http api 订阅广播
			String formClientId = message.getFromClientId();
			ChannelContext context = mqttServer.getChannelContext(formClientId);
			if (context != null) {
				sessionManager.addSubscribe(topic, formClientId, message.getQos());
			}
		} else if (MessageType.UNSUBSCRIBE == messageType) {
			// http api 取消订阅广播
			String formClientId = message.getFromClientId();
			ChannelContext context = mqttServer.getChannelContext(formClientId);
			if (context != null) {
				sessionManager.removeSubscribe(topic, formClientId);
			}
		} else if (MessageType.UP_STREAM == messageType) {
			// mqtt 上行消息，需要发送到对应的监听的客户端
			sendToClient(topic, message);
		} else if (MessageType.DOWN_STREAM == messageType) {
			// http rest api 下行消息也会转发到此
			sendToClient(topic, message);
		}
	}

	/**
	 * 发送消息到客户端
	 *
	 * @param topic   topic
	 * @param message Message
	 */
	private void sendToClient(String topic, Message message) {
		// 客户端id
		String clientId = message.getClientId();
		MqttQoS mqttQoS = MqttQoS.valueOf(message.getQos());
		if (StringUtil.isBlank(clientId)) {
			mqttServer.publishAll(topic, message.getPayload(), mqttQoS, message.isRetain());
		} else {
			mqttServer.publish(clientId, topic, message.getPayload(), mqttQoS, message.isRetain());
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		byte[] channelBytes = RedisSerializer.string().serialize(channel);
		redisTemplate.execute((RedisCallback<Void>) connection -> {
			connection.subscribe(RedisMqttMessageExchangeReceiver.this, channelBytes);
			return null;
		});
	}
}
