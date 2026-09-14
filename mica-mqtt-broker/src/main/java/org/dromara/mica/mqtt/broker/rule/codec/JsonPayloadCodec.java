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

import net.dreamlu.mica.net.utils.json.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;

/**
 * codec：JSON（依赖 server 模块的 JsonUtil，避免 broker 模块引入第三方 JSON 库）。
 *
 * @author L.cm
 */
public class JsonPayloadCodec implements PayloadCodec {

	private static final Logger logger = LoggerFactory.getLogger(JsonPayloadCodec.class);

	public static final String NAME = "json";

	public JsonPayloadCodec() {
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public Object decode(byte[] payload) {
		if (payload == null || payload.length == 0) {
			return null;
		}
		try {
			return JsonUtil.readValue(payload, Object.class);
		} catch (Exception e) {
			logger.warn("JsonPayloadCodec decode failed", e);
			return null;
		}
	}

	@Override
	public byte[] encode(Object obj) {
		if (obj == null) {
			return new byte[0];
		}
		if (obj instanceof byte[]) {
			return (byte[]) obj;
		}
		String json = JsonUtil.toJsonString(obj);
		return json == null ? new byte[0] : json.getBytes(StandardCharsets.UTF_8);
	}
}
