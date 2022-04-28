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

package net.dreamlu.iot.mqtt.core.server.session;

/**
 * 共享订阅均衡策略
 *
 * @author L.cm
 */
public enum SharedStrategy {

	/**
	 * 在所有订阅者中随机选择
	 */
	random,
	/**
	 * 按照订阅顺序
	 */
	round_robin,
	/**
	 * 一直发往上次选取的订阅者
	 */
	sticky,
	/**
	 * 按照发布者 ClientID 的哈希值
	 */
	hash;

}
