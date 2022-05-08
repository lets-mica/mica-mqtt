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


import org.junit.Assert;
import org.junit.Test;

/**
 * TopicUtil 测试
 *
 * @author L.cm
 */
public class TopicUtilTest {

	@Test
	public void test() {
		// gitee issues #I56BTC /iot/test/# 无法匹配到 /iot/test 和 /iot/test/
		Assert.assertFalse(TopicUtil.match("+", "/iot/test"));
		Assert.assertFalse(TopicUtil.match("+", "iot/test"));
		Assert.assertFalse(TopicUtil.match("+", "/iot/test"));
		Assert.assertFalse(TopicUtil.match("+", "/iot"));
		Assert.assertFalse(TopicUtil.match("+/test", "/iot/test"));
		Assert.assertFalse(TopicUtil.match("/iot/test/+/", "/iot/test/123"));

		Assert.assertTrue(TopicUtil.match("/iot/test/+", "/iot/test/123"));
		Assert.assertFalse(TopicUtil.match("/iot/test/+", "/iot/test/123/"));

		Assert.assertTrue(TopicUtil.match("#", "/iot/test"));
		Assert.assertTrue(TopicUtil.match("/iot/test/#", "/iot/test"));
		Assert.assertTrue(TopicUtil.match("/iot/test/#", "/iot/test/"));
		Assert.assertTrue(TopicUtil.match("/iot/test/#", "/iot/test/1"));
		Assert.assertTrue(TopicUtil.match("/iot/test/#", "/iot/test/123123/12312"));

		Assert.assertTrue(TopicUtil.match("/iot/test/123", "/iot/test/123"));
	}

}
