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

package net.dreamlu.iot.mqtt.core.util;

import org.tio.utils.hutool.StrUtil;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 消息正文编码
 *
 * @author L.cm
 */
public enum PayloadEncode {

	/**
	 * 纯文本、hex、base64
	 */
	plain {
		@Override
		public String encode(byte[] data) {
			return new String(data, StandardCharsets.UTF_8);
		}

		@Override
		public byte[] decode(String data) {
			return data.getBytes(StandardCharsets.UTF_8);
		}
	},
	hex {
		@Override
		public String encode(byte[] data) {
			return HexUtil.encodeToString(data);
		}

		@Override
		public byte[] decode(String data) {
			return HexUtil.decode(data);
		}
	},
	base64 {
		@Override
		public String encode(byte[] data) {
			return Base64.getEncoder().encodeToString(data);
		}

		@Override
		public byte[] decode(String data) {
			return Base64.getDecoder().decode(data);
		}
	};

	/**
	 * 编码
	 *
	 * @return byte array
	 */
	public abstract String encode(byte[] data);

	/**
	 * 解码
	 *
	 * @return byte array
	 */
	public abstract byte[] decode(String data);

	/**
	 * 解码
	 *
	 * @param data     data
	 * @param encoding encoding
	 * @return byte array
	 */
	public static byte[] decode(String data, String encoding) {
		return PayloadEncode.getEncode(encoding).decode(data);
	}

	/**
	 * 获取解码器
	 *
	 * @param encoding encoding
	 * @return PayloadEncode
	 */
	public static PayloadEncode getEncode(String encoding) {
		if (StrUtil.isBlank(encoding)) {
			return PayloadEncode.plain;
		}
		PayloadEncode[] values = PayloadEncode.values();
		for (PayloadEncode encode : values) {
			if (encode.name().equalsIgnoreCase(encoding)) {
				return encode;
			}
		}
		return PayloadEncode.plain;
	}

}
