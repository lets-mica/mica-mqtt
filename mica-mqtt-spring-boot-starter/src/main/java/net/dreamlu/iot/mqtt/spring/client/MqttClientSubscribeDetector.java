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

/**
 * MqttClient 订阅监听器
 *
 * @author L.cm
 */
@Slf4j
@RequiredArgsConstructor
public class MqttClientSubscribeDetector implements BeanPostProcessor {
	private MqttClient client;

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> userClass = ClassUtils.getUserClass(bean);
		ReflectionUtils.doWithMethods(userClass, method -> {
			MqttClientSubscribe subscribe = AnnotationUtils.findAnnotation(method, MqttClientSubscribe.class);
			if (subscribe != null) {
				client.subscribe(subscribe.qos(), subscribe.value(), (topic, payload) ->
					ReflectionUtils.invokeMethod(method, bean, topic, payload)
				);
			}
		}, ReflectionUtils.USER_DECLARED_METHODS);
		return bean;
	}

}
