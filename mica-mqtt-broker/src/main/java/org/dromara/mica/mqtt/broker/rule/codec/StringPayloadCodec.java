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

import java.nio.charset.StandardCharsets;

/**
 * codec：UTF-8 字符串。
 *
 * @author L.cm
 */
public class StringPayloadCodec implements PayloadCodec {

	public static final String NAME = "string";

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Object decode(byte[] payload) {
		if (payload == null) {
			return "";
		}
		return new String(payload, StandardCharsets.UTF_8);
	}

	@Override
	public byte[] encode(Object obj) {
		if (obj == null) {
			return new byte[0];
		}
		return obj.toString().getBytes(StandardCharsets.UTF_8);
	}
}
