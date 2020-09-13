/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.net.dreamlu.net).
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

package net.dreamlu.iot.mqtt.codec;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * ByteBuffer 工具
 *
 * @author L.cm
 */
public class ByteBufferUtil {
	/**
	 * 空 byte 数组
	 */
	public static final byte[] EMPTY = new byte[0];

	/**
	 * read byte
	 * @param buffer ByteBuffer
	 * @return byte
	 */
	public static byte readByte(ByteBuffer buffer) {
		return buffer.get();
	}

	/**
	 * read unsigned byte
	 * @param buffer ByteBuffer
	 * @return short
	 */
	public static short readUnsignedByte(ByteBuffer buffer) {
		return (short) (readByte(buffer) & 0xFF);
	}

	/**
	 * skip bytes
	 * @param buffer ByteBuffer
	 * @param skip skip bytes
	 * @return ByteBuffer
	 */
	public static ByteBuffer skipBytes(ByteBuffer buffer, int skip) {
		buffer.position(buffer.position() + skip);
		return buffer;
	}

	public static String toString(ByteBuffer buffer) {
		return toString(buffer, StandardCharsets.UTF_8);
	}

	public static String toString(ByteBuffer buffer, Charset charset) {
		return new String(buffer.array(), buffer.position(), buffer.limit(), charset);
	}

	public static ByteBuffer clone(ByteBuffer original) {
		ByteBuffer clone = ByteBuffer.allocate(original.capacity());
		// copy from the beginning
		original.rewind();
		clone.put(original);
		original.rewind();
		clone.flip();
		return clone;
	}

}
