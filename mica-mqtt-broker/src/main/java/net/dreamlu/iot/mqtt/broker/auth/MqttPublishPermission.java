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

package net.dreamlu.iot.mqtt.broker.auth;

import lombok.extern.slf4j.Slf4j;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerPublishPermission;
import org.springframework.context.annotation.Configuration;
import org.tio.core.ChannelContext;

/**
 * mqtt 服务端校验客户端是否有发布权限，请按照自己的需求和业务进行扩展
 *
 * @author L.cm
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class MqttPublishPermission implements IMqttServerPublishPermission {

	/**
	 * 否有发布权限
	 *
	 * @param context  ChannelContext
	 * @param clientId 客户端 id
	 * @param topic    topic
	 * @param qoS      MqttQoS
	 * @param isRetain 是否保留消息
	 * @return 否有发布权限
	 */
	@Override
	public boolean hasPermission(ChannelContext context, String clientId, String topic, MqttQoS qoS, boolean isRetain) {
		log.info("Mqtt client publish permission check clientId:{} topic:{}.", clientId, topic);
		// 可自定义业务，判断客户端是否有发布的权限。
		return true;
	}

}
