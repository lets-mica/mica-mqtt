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

package net.dreamlu.iot.mqtt.spring.server.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.core.server.event.IMqttMessageListener;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import org.springframework.context.ApplicationEventPublisher;
import org.tio.core.ChannelContext;

/**
 * 使用 Spring event 解耦消息监听
 *
 * @author L.cm
 */
@Slf4j
@RequiredArgsConstructor
public class SpringEventMqttMessageListener implements IMqttMessageListener {
	private final ApplicationEventPublisher eventPublisher;

	@Override
	public void onMessage(ChannelContext context, String clientId, Message message) {
		if (log.isDebugEnabled()) {
			log.debug("mqtt server receive message clientId:{} message:{} payload:{}", clientId, message, ByteBufferUtil.toString(message.getPayload()));
		}
		eventPublisher.publishEvent(message);
	}

}
