package net.dreamlu.iot.mqtt.codec;

import org.tio.utils.hutool.FastByteBuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * 写出的 buffer
 *
 * @author L.cm
 */
public class WriteBuffer {
	private final FastByteBuffer buffer;

	public WriteBuffer() {
		this(new FastByteBuffer());
	}

	public WriteBuffer(int size) {
		this(new FastByteBuffer(size));
	}

	public WriteBuffer(FastByteBuffer buffer) {
		this.buffer = buffer;
	}

	/**
	 * 输出一个byte类型的数据
	 *
	 * @param b 待输出数值
	 */
	public void writeByte(byte b) {
		buffer.append(b);
	}

	/**
	 * 输出一个bytes类型的数据
	 *
	 * @param bytes 待输出数值
	 */
	public void writeBytes(byte[] bytes) {
		buffer.append(bytes);
	}

	/**
	 * 输出一个bytes类型的数据
	 *
	 * @param bytes 待输出数值
	 * @param off   off
	 * @param len   len
	 */
	public void writeBytes(byte[] bytes, int off, int len) {
		buffer.append(bytes, off, len);
	}

	/**
	 * 输出一个short类型的数据
	 *
	 * @param v short数值
	 */
	public void writeShort(short v) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) ((v >>> 8) & 0xFF);
		bytes[1] = (byte) (v & 0xFF);
		buffer.append(bytes);
	}

	/**
	 * 输出int数值,占用4个字节
	 *
	 * @param v int数值
	 */
	public void writeInt(int v) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) ((v >>> 24) & 0xFF);
		bytes[1] = (byte) ((v >>> 16) & 0xFF);
		bytes[2] = (byte) ((v >>> 8) & 0xFF);
		bytes[3] = (byte) (v & 0xFF);
		buffer.append(bytes);
	}

	/**
	 * 输出long数值,占用8个字节
	 *
	 * @param v long数值
	 */
	public void writeLong(long v) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) ((v >>> 56) & 0xFF);
		bytes[1] = (byte) ((v >>> 48) & 0xFF);
		bytes[2] = (byte) ((v >>> 40) & 0xFF);
		bytes[3] = (byte) ((v >>> 32) & 0xFF);
		bytes[4] = (byte) ((v >>> 24) & 0xFF);
		bytes[5] = (byte) ((v >>> 16) & 0xFF);
		bytes[6] = (byte) ((v >>> 8) & 0xFF);
		bytes[7] = (byte) (v & 0xFF);
		buffer.append(bytes);
	}

	/**
	 * 写可变长度整数
	 *
	 * @param num num
	 */
	public void writeVarLengthInt(int num) {
		do {
			int digit = num % 128;
			num /= 128;
			if (num > 0) {
				digit |= 0x80;
			}
			writeByte((byte) digit);
		} while (num > 0);
	}

	/**
	 * 转换成数组
	 *
	 * @return byte array
	 */
	public byte[] toArray() {
		return buffer.toArray();
	}

	/**
	 * 转换成数组
	 *
	 * @return ByteBuffer
	 */
	public ByteBuffer toBuffer() {
		return ByteBuffer.wrap(this.toArray());
	}

	/**
	 * 重置
	 */
	public void reset() {
		buffer.reset();
	}

}
