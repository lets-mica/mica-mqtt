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

package org.dromara.mica.mqtt.broker.rule.matcher;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Matcher 注册表：按 (type + matcherProps) 缓存 matcher。
 *
 * @author L.cm
 */
public class MatcherRegistry {

	private final ConcurrentMap<String, RuleMatcherFactory> factories = new ConcurrentHashMap<>();
	private final ConcurrentMap<String, RuleMatcher> cache = new ConcurrentHashMap<>();

	/**
	 * 注册工厂。
	 *
	 * @param factory 工厂
	 */
	public void registerFactory(RuleMatcherFactory factory) {
		factories.putIfAbsent(factory.getType(), factory);
	}

	/**
	 * 物化 matcher。
	 *
	 * @param type  类型名（null 表示仅 topic 命中）
	 * @param props 属性
	 * @return matcher 实例（type 为空返回 null）
	 */
	public RuleMatcher get(String type, java.util.Map<String, String> props) {
		if (type == null || type.isEmpty()) {
			return null;
		}
		String key = type + "|" + props;
		RuleMatcher matcher = cache.get(key);
		if (matcher != null) {
			return matcher;
		}
		RuleMatcherFactory factory = factories.get(type);
		if (factory == null) {
			throw new IllegalStateException("No RuleMatcherFactory for type: " + type);
		}
		return cache.computeIfAbsent(key, k -> factory.create(props));
	}
}
