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

import java.util.AbstractList;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;

/**
 * 集合工具
 *
 * @author L.cm
 */
public final class CollUtil {

	/**
	 * list 分片
	 *
	 * @param list List
	 * @param size 分片大小
	 * @param <T>  泛型
	 * @return List 分片
	 */
	public static <T> List<List<T>> partition(List<T> list, int size) {
		Objects.requireNonNull(list, "List to partition must not null.");
		if (size < 1) {
			throw new IllegalArgumentException("List to partition size must more then zero.");
		}
		return (list instanceof RandomAccess)
			? new RandomAccessPartition<>(list, size)
			: new Partition<>(list, size);
	}

	private static class RandomAccessPartition<T> extends Partition<T> implements RandomAccess {
		RandomAccessPartition(List<T> list, int size) {
			super(list, size);
		}
	}

	private static class Partition<T> extends AbstractList<List<T>> {
		final List<T> list;
		final int size;

		Partition(List<T> list, int size) {
			this.list = list;
			this.size = size;
		}

		@Override
		public List<T> get(int index) {
			if (index >= 0 && index < this.size()) {
				int start = index * this.size;
				int end = Math.min(start + this.size, this.list.size());
				return this.list.subList(start, end);
			}
			throw new IndexOutOfBoundsException(String.format("index (%s) must be less than size (%s)", index, this.size()));
		}

		@Override
		public int size() {
			return ceilDiv(this.list.size(), this.size);
		}

		@Override
		public boolean isEmpty() {
			return this.list.isEmpty();
		}

		private static int ceilDiv(int x, int y) {
			int r = x / y;
			if (r * y < x) {
				r++;
			}
			return r;
		}
	}

}
