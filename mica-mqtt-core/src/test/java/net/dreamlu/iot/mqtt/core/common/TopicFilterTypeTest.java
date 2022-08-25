package net.dreamlu.iot.mqtt.core.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TopicFilterTypeTest {

	@Test
	void test() {
		boolean match1 = TopicFilterType.SHARE.match("$share/test/abc", "abc");
		Assertions.assertTrue(match1);
		boolean match2 = TopicFilterType.SHARE.match("$share/test/abc", "/abc");
		Assertions.assertTrue(match2);
	}

}
