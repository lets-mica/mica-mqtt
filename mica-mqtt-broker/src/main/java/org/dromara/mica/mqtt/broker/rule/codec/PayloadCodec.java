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

package org.dromara.mica.mqtt.broker.rule.codec;

/**
 * Payload 编解码抽象，业务可读对象用于 matcher / 调试。
 *
 * @author L.cm
 */
public interface PayloadCodec {

	/**
	 * @return codec 名称
	 */
	String getName();

	/**
	 * 解码 payload 到业务对象。
	 *
	 * @param payload 原始字节
	 * @return 解码后的对象（解码失败可抛异常或返回 null）
	 */
	Object decode(byte[] payload);

	/**
	 * 将对象编码为字节。
	 *
	 * @param obj 业务对象
	 * @return 字节
	 */
	byte[] encode(Object obj);
}
