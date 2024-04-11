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

import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.client.*;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.tio.client.ClientChannelContext;
import org.tio.client.TioClient;
import org.tio.client.TioClientConfig;

import java.util.List;

/**
 * mqtt client 模板
 *
 * @author wsq（冷月宫主）
 */
public class MqttClientTemplate implements ApplicationContextAware, SmartInitializingSingleton, InitializingBean, DisposableBean, Ordered {
	public static final String DEFAULT_CLIENT_TEMPLATE_BEAN = "mqttClientTemplate";
	private final MqttClientCreator clientCreator;
	private ApplicationContext applicationContext;
	private MqttClient client;

	public MqttClientTemplate(MqttClientCreator clientCreator) {
		this.clientCreator = clientCreator;
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos0(String topicFilter, IMqttClientMessageListener listener) {
		return client.subscribe(topicFilter, MqttQoS.AT_MOST_ONCE, listener);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos1(String topicFilter, IMqttClientMessageListener listener) {
		return client.subscribe(topicFilter, MqttQoS.AT_LEAST_ONCE, listener);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subQos2(String topicFilter, IMqttClientMessageListener listener) {
		return client.subscribe(topicFilter, MqttQoS.EXACTLY_ONCE, listener);
	}

	/**
	 * 订阅
	 *
	 * @param mqttQoS     MqttQoS
	 * @param topicFilter topicFilter
	 * @param listener    MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subscribe(MqttQoS mqttQoS, String topicFilter, IMqttClientMessageListener listener) {
		return client.subscribe(topicFilter, mqttQoS, listener);
	}

	/**
	 * 订阅
	 *
	 * @param topicFilters topicFilter 数组
	 * @param mqttQoS      MqttQoS
	 * @param listener     MqttMessageListener
	 * @return MqttClient
	 */
	public MqttClient subscribe(String[] topicFilters, MqttQoS mqttQoS, IMqttClientMessageListener listener) {
		return client.subscribe(topicFilters, mqttQoS, listener);
	}

	/**
	 * 批量订阅
	 *
	 * @param subscriptionList 订阅集合
	 * @return MqttClient
	 */
	public MqttClient subscribe(List<MqttClientSubscription> subscriptionList) {
		return client.subscribe(subscriptionList);
	}

	/**
	 * 取消订阅
	 *
	 * @param topicFilter topicFilter
	 * @return MqttClient
	 */
	public MqttClient unSubscribe(String... topicFilter) {
		return client.unSubscribe(topicFilter);
	}

	/**
	 * 取消订阅
	 *
	 * @param topicFilters topicFilter 集合
	 * @return MqttClient
	 */
	public MqttClient unSubscribe(List<String> topicFilters) {
		return client.unSubscribe(topicFilters);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息内容
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, byte[] payload) {
		return client.publish(topic, payload, MqttQoS.AT_MOST_ONCE);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息内容
	 * @param qos     MqttQoS
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, byte[] payload, MqttQoS qos) {
		return client.publish(topic, payload, qos, false);
	}

	/**
	 * 发布消息
	 *
	 * @param topic   topic
	 * @param payload 消息体
	 * @param qos     MqttQoS
	 * @param retain  是否在服务器上保留消息
	 * @return 是否发送成功
	 */
	public boolean publish(String topic, byte[] payload, MqttQoS qos, boolean retain) {
		return client.publish(topic, payload, qos, retain);
	}

	/**
	 * 重连
	 */
	public void reconnect() {
		client.reconnect();
	}

	/**
	 * 重连到新的服务端节点
	 *
	 * @param ip   ip
	 * @param port port
	 * @return 是否成功
	 */
	public boolean reconnect(String ip, int port) {
		return client.reconnect(ip, port);
	}

	/**
	 * 断开 mqtt 连接
	 *
	 * @return 是否成功
	 */
	public boolean disconnect() {
		return client.disconnect();
	}

	/**
	 * 获取 TioClient
	 *
	 * @return TioClient
	 */
	public TioClient getTioClient() {
		return client.getTioClient();
	}

	/**
	 * 获取配置
	 *
	 * @return MqttClientCreator
	 */
	public MqttClientCreator getClientCreator() {
		return clientCreator;
	}

	/**
	 * 获取 ClientTioConfig
	 *
	 * @return ClientTioConfig
	 */
	public TioClientConfig getClientTioConfig() {
		return client.getClientTioConfig();
	}

	/**
	 * 获取 ClientChannelContext
	 *
	 * @return ClientChannelContext
	 */
	public ClientChannelContext getContext() {
		return client.getContext();
	}

	/**
	 * 判断客户端跟服务端是否连接
	 *
	 * @return 是否已经连接成功
	 */
	public boolean isConnected() {
		return client.isConnected();
	}

	/**
	 * 判断客户端跟服务端是否断开连接
	 *
	 * @return 是否断连
	 */
	public boolean isDisconnected() {
		return client.isDisconnected();
	}

	/**
	 * 获取 MqttClient
	 *
	 * @return MqttClient
	 */
	public MqttClient getMqttClient() {
		return client;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// 需要支持注解订阅所以 clientSession 配置的时机要早一些
		IMqttClientSession clientSession = this.clientCreator.getClientSession();
		if (clientSession == null) {
			clientSession = this.applicationContext.getBeanProvider(IMqttClientSession.class)
				.getIfAvailable(DefaultMqttClientSession::new);
			this.clientCreator.clientSession(clientSession);
		}
	}

	@Override
	public void afterSingletonsInstantiated() {
		// 配置客户端连接监听器
		this.applicationContext.getBeanProvider(IMqttClientConnectListener.class).ifAvailable(clientCreator::connectListener);
		// 全局监听器
		this.applicationContext.getBeanProvider(IMqttClientGlobalMessageListener.class).ifAvailable(clientCreator::globalMessageListener);
		// 自定义处理
		this.applicationContext.getBeanProvider(MqttClientCustomizer.class).ifAvailable(customizer -> customizer.customize(clientCreator));
		// 连接超时时间，如果没设置，改成 3s，减少因连不上卡顿时间
		Integer timeout = clientCreator.getTimeout();
		if (timeout == null) {
			clientCreator.timeout(3);
		}
		// 使用同步连接，不过如果连不上会卡一会
		client = clientCreator.connectSync();
	}

	@Override
	public void destroy() {
		client.stop();
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

}
