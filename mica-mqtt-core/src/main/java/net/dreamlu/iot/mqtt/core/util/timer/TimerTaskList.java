/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dreamlu.iot.mqtt.core.util.timer;


import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Function;

/**
 * TimerTaskList
 *
 * @author kafka、guest
 */
class TimerTaskList implements Delayed {
	private final LongAdder taskCounter;
	/**
	 * 根节点
	 */
	private final TimerTaskEntry root;
	/**
	 * 过期时间
	 */
	private final AtomicLong expiration;

	public TimerTaskList(LongAdder taskCounter) {
		// TimerTaskList forms a doubly linked cyclic list using a dummy root entry
		// root.next points to the head
		// root.prev points to the tail
		this.taskCounter = taskCounter;
		this.root = new TimerTaskEntry(null, -1L);
		this.root.next = root;
		this.root.prev = root;
		this.expiration = new AtomicLong(-1L);
	}

	/**
	 * 设置过期时间
	 *
	 * @return true if the expiration time is changed
	 */
	public boolean setExpiration(Long expirationMs) {
		return expiration.getAndSet(expirationMs) != expirationMs;
	}

	/**
	 * 获取过期时间
	 *
	 * @return expiration time
	 */
	public Long getExpiration() {
		return expiration.get();
	}

	/**
	 * Apply the supplied function to each of tasks in this list
	 */
	public void foreach(Function<TimerTask, Void> f) {
		synchronized (this) {
			TimerTaskEntry entry = root.next;
			while (entry != root) {
				TimerTaskEntry nextEntry = entry.next;
				if (!entry.cancelled()) {
					f.apply(entry.getTimerTask());
				}
				entry = nextEntry;
			}
		}
	}

	/**
	 * Add a timer task entry to this list
	 */
	public void add(TimerTaskEntry timerTaskEntry) {
		boolean done = false;
		while (!done) {
			// Remove the timer task entry if it is already in any other list
			// We do this outside of the sync block below to avoid deadlocking.
			// We may retry until timerTaskEntry.list becomes null.
			timerTaskEntry.remove();
			synchronized (this) {
				if (timerTaskEntry.getList() == null) {
					// put the timer task entry to the end of the list. (root.prev points to the tail entry)
					TimerTaskEntry tail = root.prev;
					timerTaskEntry.next = root;
					timerTaskEntry.prev = tail;
					timerTaskEntry.setList(this);
					tail.next = timerTaskEntry;
					root.prev = timerTaskEntry;
					taskCounter.increment();
					done = true;
				}
			}
		}
	}

	/**
	 * Remove the specified timer task entry from this list
	 */
	public void remove(TimerTaskEntry timerTaskEntry) {
		synchronized (this) {
			if (timerTaskEntry.getList() == this) {
				timerTaskEntry.next.prev = timerTaskEntry.prev;
				timerTaskEntry.prev.next = timerTaskEntry.next;
				timerTaskEntry.next = null;
				timerTaskEntry.prev = null;
				timerTaskEntry.setList(null);
				taskCounter.decrement();
			}
		}
	}

	/**
	 * Remove all task entries and apply the supplied function to each of them
	 */
	public void flush(Function<TimerTaskEntry, Void> f) {
		synchronized (this) {
			TimerTaskEntry head = root.next;
			while (head != root) {
				remove(head);
				f.apply(head);
				head = root.next;
			}
			expiration.set(-1L);
		}
	}

	@Override
	public long getDelay(TimeUnit unit) {
		return unit.convert(Long.max(getExpiration() - Timer.getHiresClockMs(), 0), TimeUnit.MILLISECONDS);
	}

	@Override
	public int compareTo(Delayed delayed) {
		if (delayed instanceof TimerTaskList) {
			return Long.compare(getExpiration(), ((TimerTaskList) delayed).getExpiration());
		} else {
			throw new ClassCastException("can not cast to TimerTaskList");
		}
	}

}
