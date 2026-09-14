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

package org.dromara.mica.mqtt.core.server.func;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * MqttFunctionManager 测试，验证 register / get / unregister 及 trie 节点回收。
 *
 * @author L.cm
 */
class MqttFunctionManagerTest {

	private static IMqttFunctionMessageListener newListener(AtomicInteger counter) {
		return (context, clientId, topic, qoS, message) -> counter.incrementAndGet();
	}

	@Test
	void registerAndGet() {
		MqttFunctionManager mgr = new MqttFunctionManager();
		IMqttFunctionMessageListener fn = newListener(new AtomicInteger());
		mgr.register("sensor/+/temperature", fn);
		List<IMqttFunctionMessageListener> got = mgr.get("sensor/room1/temperature");
		assertEquals(1, got.size());
		assertEquals(fn, got.get(0));
	}

	@Test
	void unregisterByReference() {
		MqttFunctionManager mgr = new MqttFunctionManager();
		IMqttFunctionMessageListener fn = newListener(new AtomicInteger());
		IMqttFunctionMessageListener other = newListener(new AtomicInteger());
		mgr.register("a/b/c", fn);
		mgr.register("a/b/c", other);
		assertTrue(mgr.unregister("a/b/c", fn));
		assertEquals(1, mgr.get("a/b/c").size());
		assertEquals(other, mgr.get("a/b/c").get(0));
	}

	@Test
	void unregisterNotExists() {
		MqttFunctionManager mgr = new MqttFunctionManager();
		IMqttFunctionMessageListener fn = newListener(new AtomicInteger());
		assertFalse(mgr.unregister("a/b/c", fn));
	}

	@Test
	void unregisterCleansTrieNodes() {
		MqttFunctionManager mgr = new MqttFunctionManager();
		IMqttFunctionMessageListener fn = newListener(new AtomicInteger());
		mgr.register("foo/bar/baz", fn);
		// 同一前缀注册另一个 listener，确保不会回收共用节点
		IMqttFunctionMessageListener another = newListener(new AtomicInteger());
		mgr.register("foo/bar/qux", another);

		assertTrue(mgr.unregister("foo/bar/baz", fn));
		// baz 路径已被清理
		assertTrue(mgr.get("foo/bar/baz").isEmpty());
		// qux 路径仍能匹配
		List<IMqttFunctionMessageListener> got = mgr.get("foo/bar/qux");
		assertEquals(1, got.size());
		assertEquals(another, got.get(0));
	}

	@Test
	void unregisterLastListenerOnPath() {
		MqttFunctionManager mgr = new MqttFunctionManager();
		IMqttFunctionMessageListener fn = newListener(new AtomicInteger());
		mgr.register("unique/topic/path", fn);
		assertTrue(mgr.unregister("unique/topic/path", fn));
		assertTrue(mgr.get("unique/topic/path").isEmpty());

		// 重复 unregister 应返回 false
		assertFalse(mgr.unregister("unique/topic/path", fn));
	}

	@Test
	void wildcardTopics() {
		MqttFunctionManager mgr = new MqttFunctionManager();
		IMqttFunctionMessageListener multi = newListener(new AtomicInteger());
		IMqttFunctionMessageListener single = newListener(new AtomicInteger());
		mgr.register("a/#", multi);
		mgr.register("a/+", single);

		List<IMqttFunctionMessageListener> got1 = mgr.get("a/b");
		assertNotNull(got1);
		assertEquals(2, got1.size());

		List<IMqttFunctionMessageListener> got2 = mgr.get("a/b/c");
		assertEquals(1, got2.size());
		assertEquals(multi, got2.get(0));
	}
}
