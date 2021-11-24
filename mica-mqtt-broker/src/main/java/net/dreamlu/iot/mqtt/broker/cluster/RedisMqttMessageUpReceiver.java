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

import net.dreamlu.iot.mqtt.broker.service.IMqttMessageService;
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

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * 监听集群消息，上行消息，设备到云端
 *
 * @author L.cm
 */
public class RedisMqttMessageUpReceiver implements MessageListener, InitializingBean {
	private final RedisTemplate<String, Object> redisTemplate;
	private final IMessageSerializer messageSerializer;
	private final String channel;
	private final MqttServer mqttServer;
	private final IMqttSessionManager sessionManager;
	private final IMqttMessageService messageService;

	public RedisMqttMessageUpReceiver(MicaRedisCache redisCache,
									  IMessageSerializer messageSerializer,
									  String channel,
									  MqttServer mqttServer,
									  IMqttMessageService messageService) {
		this.redisTemplate = redisCache.getRedisTemplate();
		this.messageSerializer = messageSerializer;
		this.channel = Objects.requireNonNull(channel, "Redis pub/sub channel is null.");
		this.mqttServer = mqttServer;
		this.sessionManager = mqttServer.getServerCreator().getSessionManager();
		this.messageService = messageService;
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

	public void messageProcessing(Message message) {
		MessageType messageType = message.getMessageType();
		String topic = message.getTopic();
		if (MessageType.SUBSCRIBE == messageType) {
			String formClientId = message.getFromClientId();
			ChannelContext context = mqttServer.getChannelContext(formClientId);
			if (context != null) {
				sessionManager.addSubscribe(topic, formClientId, message.getQos());
			}
		} else if (MessageType.UNSUBSCRIBE == messageType) {
			String formClientId = message.getFromClientId();
			ChannelContext context = mqttServer.getChannelContext(formClientId);
			if (context != null) {
				sessionManager.removeSubscribe(topic, formClientId);
			}
		} else if (MessageType.UP_STREAM == messageType) {
			messageService.publishProcessing(message);
		} else if (MessageType.DOWN_STREAM == messageType) {
			// http rest api 也会转发到此
			String clientId = message.getClientId();
			ByteBuffer payload = message.getPayload();
			MqttQoS mqttQoS = MqttQoS.valueOf(message.getQos());
			boolean retain = message.isRetain();
			if (StringUtil.isBlank(clientId)) {
				mqttServer.publishAll(topic, payload, mqttQoS, retain);
			} else {
				mqttServer.publish(clientId, topic, payload, mqttQoS, retain);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		byte[] channelBytes = RedisSerializer.string().serialize(channel);
		redisTemplate.execute((RedisCallback<Void>) connection -> {
			connection.subscribe(RedisMqttMessageUpReceiver.this, channelBytes);
			return null;
		});
	}
}
