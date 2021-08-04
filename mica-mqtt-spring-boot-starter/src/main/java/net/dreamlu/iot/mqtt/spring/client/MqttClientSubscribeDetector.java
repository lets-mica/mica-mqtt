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

package net.dreamlu.iot.mqtt.spring.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dreamlu.iot.mqtt.core.client.MqttClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

/**
 * MqttClient 订阅监听器
 *
 * @author L.cm
 */
@Slf4j
@RequiredArgsConstructor
public class MqttClientSubscribeDetector implements BeanPostProcessor {
	private final MqttClientTemplate clientTemplate;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		MqttClient mqttClient = clientTemplate.getMqttClient();
		Objects.requireNonNull(mqttClient, "MqttClient is null.");
		Class<?> userClass = ClassUtils.getUserClass(bean);
		ReflectionUtils.doWithMethods(userClass, method -> {
			MqttClientSubscribe subscribe = AnnotationUtils.findAnnotation(method, MqttClientSubscribe.class);
			if (subscribe != null) {
				// 校验 method，method 入参数必须等于2
				int paramCount = method.getParameterCount();
				if (paramCount != 2) {
					throw new IllegalArgumentException("@MqttClientSubscribe on method " + method + " parameter count must equal to 2.");
				}
				mqttClient.subscribe(subscribe.qos(), subscribe.value(), (topic, payload) ->
					ReflectionUtils.invokeMethod(method, bean, topic, payload)
				);
			}
		}, ReflectionUtils.USER_DECLARED_METHODS);
		return bean;
	}

}
