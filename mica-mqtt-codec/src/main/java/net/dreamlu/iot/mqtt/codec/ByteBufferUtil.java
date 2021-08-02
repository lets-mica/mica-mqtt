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

package net.dreamlu.iot.mqtt.codec;

import java.nio.Buffer;
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
	public static final byte[] EMPTY_BYTES = new byte[0];

	/**
	 * read byte
	 *
	 * @param buffer ByteBuffer
	 * @return byte
	 */
	public static byte readByte(ByteBuffer buffer) {
		return buffer.get();
	}

	/**
	 * read unsigned byte
	 *
	 * @param buffer ByteBuffer
	 * @return short
	 */
	public static short readUnsignedByte(ByteBuffer buffer) {
		return (short) (readByte(buffer) & 0xFF);
	}

	/**
	 * skip bytes
	 *
	 * @param buffer ByteBuffer
	 * @param skip   skip bytes
	 * @return ByteBuffer
	 */
	public static ByteBuffer skipBytes(ByteBuffer buffer, int skip) {
		position(buffer, buffer.position() + skip);
		return buffer;
	}

	/**
	 * position 为了兼容 java8，详见： https://gitee.com/596392912/mica-mqtt/issues/I43EZT
	 *
	 * @param buffer      Buffer
	 * @param newPosition newPosition
	 */
	public static void position(Buffer buffer, int newPosition) {
		((Buffer) buffer).position(newPosition);
	}

	/**
	 * limit 为了兼容 java8，详见： https://gitee.com/596392912/mica-mqtt/issues/I43EZT
	 *
	 * @param buffer   Buffer
	 * @param newLimit newLimit
	 */
	public static void limit(Buffer buffer, int newLimit) {
		((Buffer) buffer).limit(newLimit);
	}

	/**
	 * flip 为了兼容 java8，详见： https://gitee.com/596392912/mica-mqtt/issues/I43EZT
	 *
	 * @param buffer Buffer
	 */
	public static void flip(Buffer buffer) {
		((Buffer) buffer).flip();
	}

	/**
	 * rewind 为了兼容 java8，详见： https://gitee.com/596392912/mica-mqtt/issues/I43EZT
	 *
	 * @param buffer Buffer
	 */
	public static void rewind(Buffer buffer) {
		((Buffer) buffer).rewind();
	}

	/**
	 * mark 为了兼容 java8，详见： https://gitee.com/596392912/mica-mqtt/issues/I43EZT
	 *
	 * @param buffer Buffer
	 */
	public static void mark(Buffer buffer) {
		((Buffer) buffer).mark();
	}


	/**
	 * reset 为了兼容 java8，详见： https://gitee.com/596392912/mica-mqtt/issues/I43EZT
	 *
	 * @param buffer Buffer
	 */
	public static void reset​(Buffer buffer) {
		((Buffer) buffer).reset();
	}

	/**
	 * clear 为了兼容 java8，详见： https://gitee.com/596392912/mica-mqtt/issues/I43EZT
	 *
	 * @param buffer Buffer
	 */
	public static void clear(Buffer buffer) {
		((Buffer) buffer).clear();
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
		rewind(original);
		clone.put(original);
		rewind(original);
		flip(clone);
		return clone;
	}

}
