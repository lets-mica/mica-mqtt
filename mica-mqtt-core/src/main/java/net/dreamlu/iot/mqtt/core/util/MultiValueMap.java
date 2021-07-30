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

import java.util.*;

/**
 * 多值得 map
 *
 * @param <K> key 泛型
 * @param <V> value 泛型
 * @author L.cm
 */
public class MultiValueMap<K, V> implements Map<K, List<V>> {
	private final Map<K, List<V>> targetMap;

	public MultiValueMap() {
		this(new LinkedHashMap<>());
	}

	public MultiValueMap(Map<K, List<V>> targetMap) {
		this.targetMap = Objects.requireNonNull(targetMap);
	}

	public V getFirst(K key) {
		List<V> values = this.targetMap.get(key);
		return (values != null && !values.isEmpty() ? values.get(0) : null);
	}

	public void add(K key, V value) {
		List<V> values = this.targetMap.computeIfAbsent(key, k -> new LinkedList<>());
		values.add(value);
	}

	public void addAll(K key, List<? extends V> values) {
		List<V> currentValues = this.targetMap.computeIfAbsent(key, k -> new LinkedList<>());
		currentValues.addAll(values);
	}

	public void addAll(MultiValueMap<K, V> values) {
		for (Entry<K, List<V>> entry : values.entrySet()) {
			addAll(entry.getKey(), entry.getValue());
		}
	}

	public void set(K key, V value) {
		List<V> values = new LinkedList<>();
		values.add(value);
		this.targetMap.put(key, values);
	}

	public void setAll(Map<K, V> values) {
		values.forEach(this::set);
	}

	public Map<K, V> toSingleValueMap() {
		Map<K, V> singleValueMap = new LinkedHashMap<>(this.targetMap.size());
		this.targetMap.forEach((key, values) -> {
			if (values != null && !values.isEmpty()) {
				singleValueMap.put(key, values.get(0));
			}
		});
		return singleValueMap;
	}

	@Override
	public int size() {
		return this.targetMap.size();
	}

	@Override
	public boolean isEmpty() {
		return this.targetMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object o) {
		return this.targetMap.containsKey(o);
	}

	@Override
	public boolean containsValue(Object o) {
		return this.targetMap.containsValue(o);
	}

	@Override
	public List<V> get(Object o) {
		return this.targetMap.get(o);
	}

	@Override
	public List<V> put(K k, List<V> vs) {
		return this.targetMap.put(k, vs);
	}

	@Override
	public List<V> remove(Object o) {
		return this.targetMap.remove(o);
	}

	@Override
	public void putAll(Map<? extends K, ? extends List<V>> map) {
		this.targetMap.putAll(map);
	}

	@Override
	public void clear() {
		this.targetMap.clear();
	}

	@Override
	public Set<K> keySet() {
		return this.targetMap.keySet();
	}

	@Override
	public Collection<List<V>> values() {
		return this.targetMap.values();
	}

	@Override
	public Set<Entry<K, List<V>>> entrySet() {
		return this.targetMap.entrySet();
	}

	@Override
	public boolean equals(Object o) {
		return this.targetMap.equals(o);
	}

	@Override
	public int hashCode() {
		return this.targetMap.hashCode();
	}
}
