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

package net.dreamlu.iot.mqtt.core.server;

import net.dreamlu.iot.mqtt.codec.MqttMessage;
import net.dreamlu.iot.mqtt.core.server.interceptor.IMqttMessageInterceptor;
import org.tio.core.ChannelContext;

import java.util.ArrayList;
import java.util.List;

/**
 * mqtt 消息拦截器集合
 *
 * @since 1.3.9
 * @author L.cm
 */
public class MqttMessageInterceptors {
	private final List<IMqttMessageInterceptor> interceptors;

	public MqttMessageInterceptors() {
		this.interceptors = new ArrayList<>();
	}

	/**
	 * 添加拦截器
	 *
	 * @param interceptor IMqttMessageInterceptor
	 */
	void add(IMqttMessageInterceptor interceptor) {
		this.interceptors.add(interceptor);
	}

	/**
	 * 接收到TCP层传过来的数据后
	 *
	 * @param context       ChannelContext
	 * @param receivedBytes 本次接收了多少字节
	 * @throws Exception Exception
	 */
	public void onAfterReceivedBytes(ChannelContext context, int receivedBytes) throws Exception {
		for (IMqttMessageInterceptor interceptor : interceptors) {
			interceptor.onAfterReceivedBytes(context, receivedBytes);
		}
	}

	/**
	 * 解码成功后触发本方法
	 *
	 * @param context    ChannelContext
	 * @param message    MqttMessage
	 * @param packetSize packetSize
	 */
	public void onAfterDecoded(ChannelContext context, MqttMessage message, int packetSize) {
		for (IMqttMessageInterceptor interceptor : interceptors) {
			interceptor.onAfterDecoded(context, message, packetSize);
		}
	}

	/**
	 * 处理一个消息包后
	 *
	 * @param context ChannelContext
	 * @param message MqttMessage
	 * @param cost    本次处理消息耗时，单位：毫秒
	 * @throws Exception Exception
	 */
	public void onAfterHandled(ChannelContext context, MqttMessage message, long cost) throws Exception {
		for (IMqttMessageInterceptor interceptor : interceptors) {
			interceptor.onAfterHandled(context, message, cost);
		}
	}

}
