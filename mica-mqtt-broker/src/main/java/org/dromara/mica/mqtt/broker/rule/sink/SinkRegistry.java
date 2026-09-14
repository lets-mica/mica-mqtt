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

package org.dromara.mica.mqtt.broker.rule.sink;

import net.dreamlu.mica.net.utils.hutool.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Sink 物化 + 按 SinkRef 全量缓存的注册表。
 *
 * @author L.cm
 */
public class SinkRegistry {

	private static final Logger logger = LoggerFactory.getLogger(SinkRegistry.class);

	private final ConcurrentMap<String, SinkFactory> factories = new ConcurrentHashMap<>();
	private final ConcurrentMap<SinkRef, Sink> cache = new ConcurrentHashMap<>();

	/**
	 * 注册工厂实现。
	 *
	 * @param factory 工厂
	 */
	public void registerFactory(SinkFactory factory) {
		String type = factory.getType();
		if (StrUtil.isBlank(type)) {
			logger.warn("SinkFactory type is blank, ignore: {}", factory.getClass().getName());
			return;
		}
		SinkFactory prev = factories.putIfAbsent(type, factory);
		if (prev != null && prev.getClass() != factory.getClass()) {
			logger.warn("Duplicate SinkFactory type={}, keep existing {}", type, prev.getClass().getName());
		}
	}

	/**
	 * 物化 sink，按 SinkRef 全量缓存（type + name + props）。
	 *
	 * @param ref sink 引用
	 * @return sink 实例
	 */
	public Sink materialize(SinkRef ref) {
		Sink sink = cache.get(ref);
		if (sink != null) {
			return sink;
		}
		SinkFactory factory = factories.get(ref.getType());
		if (factory == null) {
			throw new IllegalStateException("No SinkFactory for type: " + ref.getType());
		}
		Sink newSink;
		try {
			newSink = factory.create(ref);
		} catch (Exception e) {
			throw new RuntimeException("Failed to create sink for type: " + ref.getType(), e);
		}
		Sink existing = cache.putIfAbsent(ref, newSink);
		return existing != null ? existing : newSink;
	}

	/**
	 * 主动失效指定 sink 缓存（主要用于 sink 关闭场景）。
	 *
	 * @param ref sink 引用
	 */
	public void invalidate(SinkRef ref) {
		Sink sink = cache.remove(ref);
		if (sink != null) {
			closeQuietly(sink);
		}
	}

	/**
	 * 清空所有缓存（broker 关闭时调用）。
	 */
	public void clear() {
		for (Sink sink : cache.values()) {
			closeQuietly(sink);
		}
		cache.clear();
	}

	private static void closeQuietly(Sink sink) {
		try {
			if (sink instanceof AutoCloseable) {
				((AutoCloseable) sink).close();
			}
		} catch (Exception e) {
			logger.warn("Failed to close sink {}", sink.getName(), e);
		}
	}
}
