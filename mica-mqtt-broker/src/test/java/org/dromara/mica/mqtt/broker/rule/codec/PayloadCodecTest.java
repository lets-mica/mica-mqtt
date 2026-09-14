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

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * PayloadCodec 单测。
 *
 * @author L.cm
 */
class PayloadCodecTest {

	@Test
	void rawCodec() {
		PayloadCodec codec = new RawPayloadCodec();
		byte[] in = {1, 2, 3};
		Object decoded = codec.decode(in);
		assertArrayEquals(in, (byte[]) decoded);
		assertArrayEquals(in, codec.encode(in));
		assertEquals("raw", codec.getName());
	}

	@Test
	void stringCodec() {
		PayloadCodec codec = new StringPayloadCodec();
		byte[] in = "hello".getBytes(StandardCharsets.UTF_8);
		Object decoded = codec.decode(in);
		assertEquals("hello", decoded);
		assertArrayEquals(in, codec.encode("hello"));
	}

	@Test
	void jsonCodec() {
		PayloadCodec codec = new JsonPayloadCodec();
		Object decoded = codec.decode("{\"a\":1}".getBytes(StandardCharsets.UTF_8));
		assertNotNull(decoded);
		byte[] encoded = codec.encode("hi");
		assertEquals("\"hi\"", new String(encoded, StandardCharsets.UTF_8));
	}

	@Test
	void registryCachesByName() {
		PayloadCodecRegistry reg = new PayloadCodecRegistry();
		reg.registerFactory(new RawPayloadCodecFactory());
		reg.registerFactory(new StringPayloadCodecFactory());
		assertEquals(RawPayloadCodec.class, reg.get("raw").getClass());
		assertEquals(StringPayloadCodec.class, reg.get("string").getClass());
		// 第二次取应返回同一实例（缓存）
		assertEquals(reg.get("raw"), reg.get("raw"));
	}
}
