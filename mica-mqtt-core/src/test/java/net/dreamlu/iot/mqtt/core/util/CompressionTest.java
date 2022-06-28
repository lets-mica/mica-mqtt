package net.dreamlu.iot.mqtt.core.util;

import net.dreamlu.iot.mqtt.core.util.compression.GzipCompression;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Compression 测试
 *
 * @author L.cm
 */
public class CompressionTest {

	@Test
	public void test() throws IOException {
		String text = "如梦技术——你的编程加油站！";
		GzipCompression compression = new GzipCompression();
		byte[] bytes = compression.compress(text.getBytes(StandardCharsets.UTF_8));
		byte[] decompress = compression.decompress(bytes);
		String s = new String(decompress, StandardCharsets.UTF_8);
		Assertions.assertEquals(text, s);
	}

}
