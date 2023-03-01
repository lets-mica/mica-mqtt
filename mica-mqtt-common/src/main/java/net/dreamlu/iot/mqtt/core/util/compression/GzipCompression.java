/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.dreamlu.net).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dreamlu.iot.mqtt.core.util.compression;

import org.tio.utils.hutool.FastByteArrayOutputStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * gzip 压缩
 *
 * @author L.cm
 */
public class GzipCompression implements Compression {
	public static final int BUFFER_SIZE = 4096;
	private final int bufferSize;

	public GzipCompression() {
		this(BUFFER_SIZE);
	}

	public GzipCompression(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	@Override
	public byte[] compress(byte[] buffer) throws IOException {
		try (FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
			 GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
			 ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer)
		) {
			byte[] buf = new byte[bufferSize];
			int len;
			while ((len = inputStream.read(buf)) != -1) {
				gzip.write(buf, 0, len);
			}
			gzip.finish();
			return outputStream.toByteArray();
		}
	}

	@Override
	public byte[] decompress(byte[] buffer) throws IOException {
		try (ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer);
			 GZIPInputStream gzip = new GZIPInputStream(inputStream);
			 FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream()
		) {
			byte[] buf = new byte[bufferSize];
			int len;
			while ((len = gzip.read(buf)) > 0) {
				outputStream.write(buf, 0, len);
			}
			return outputStream.toByteArray();
		}
	}

}
