/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dreamlu.iot.mqtt.core.util.timer;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.atomic.LongAdder;

/**
 * Hierarchical Timing Wheels
 * <p>
 * A simple timing wheel is a circular list of buckets of timer tasks. Let u be the time unit.
 * A timing wheel with size n has n buckets and can hold timer tasks in n * u time interval.
 * Each bucket holds timer tasks that fall into the corresponding time range. At the beginning,
 * the first bucket holds tasks for [0, u), the second bucket holds tasks for [u, 2u), …,
 * the n-th bucket for [u * (n -1), u * n). Every interval of time unit u, the timer ticks and
 * moved to the next bucket then expire all timer tasks in it. So, the timer never insert a task
 * into the bucket for the current time since it is already expired. The timer immediately runs
 * the expired task. The emptied bucket is then available for the next round, so if the current
 * bucket is for the time t, it becomes the bucket for [t + u * n, t + (n + 1) * u) after a tick.
 * A timing wheel has O(1) cost for insert/delete (start-timer/stop-timer) whereas priority queue
 * based timers, such as java.util.concurrent.DelayQueue and java.util.Timer, have O(log n)
 * insert/delete cost.
 * <p>
 * A major drawback of a simple timing wheel is that it assumes that a timer request is within
 * the time interval of n * u from the current time. If a timer request is out of this interval,
 * it is an overflow. A hierarchical timing wheel deals with such overflows. It is a hierarchically
 * organized timing wheels. The lowest level has the finest time resolution. As moving up the
 * hierarchy, time resolutions become coarser. If the resolution of a wheel at one level is u and
 * the size is n, the resolution of the next level should be n * u. At each level overflows are
 * delegated to the wheel in one level higher. When the wheel in the higher level ticks, it reinsert
 * timer tasks to the lower level. An overflow wheel can be created on-demand. When a bucket in an
 * overflow bucket expires, all tasks in it are reinserted into the timer recursively. The tasks
 * are then moved to the finer grain wheels or be executed. The insert (start-timer) cost is O(m)
 * where m is the number of wheels, which is usually very small compared to the number of requests
 * in the system, and the delete (stop-timer) cost is still O(1).
 * <p>
 * Example
 * Let's say that u is 1 and n is 3. If the start time is c,
 * then the buckets at different levels are:
 * <p>
 * level    buckets
 * 1        [c,c]   [c+1,c+1]  [c+2,c+2]
 * 2        [c,c+2] [c+3,c+5]  [c+6,c+8]
 * 3        [c,c+8] [c+9,c+17] [c+18,c+26]
 * <p>
 * The bucket expiration is at the time of bucket beginning.
 * So at time = c+1, buckets [c,c], [c,c+2] and [c,c+8] are expired.
 * Level 1's clock moves to c+1, and [c+3,c+3] is created.
 * Level 2 and level3's clock stay at c since their clocks move in unit of 3 and 9, respectively.
 * So, no new buckets are created in level 2 and 3.
 * <p>
 * Note that bucket [c,c+2] in level 2 won't receive any task since that range is already covered in level 1.
 * The same is true for the bucket [c,c+8] in level 3 since its range is covered in level 2.
 * This is a bit wasteful, but simplifies the implementation.
 * <p>
 * 1        [c+1,c+1]  [c+2,c+2]  [c+3,c+3]
 * 2        [c,c+2]    [c+3,c+5]  [c+6,c+8]
 * 3        [c,c+8]    [c+9,c+17] [c+18,c+26]
 * <p>
 * At time = c+2, [c+1,c+1] is newly expired.
 * Level 1 moves to c+2, and [c+4,c+4] is created,
 * <p>
 * 1        [c+2,c+2]  [c+3,c+3]  [c+4,c+4]
 * 2        [c,c+2]    [c+3,c+5]  [c+6,c+8]
 * 3        [c,c+8]    [c+9,c+17] [c+18,c+26]
 * <p>
 * At time = c+3, [c+2,c+2] is newly expired.
 * Level 2 moves to c+3, and [c+5,c+5] and [c+9,c+11] are created.
 * Level 3 stay at c.
 * <p>
 * 1        [c+3,c+3]  [c+4,c+4]  [c+5,c+5]
 * 2        [c+3,c+5]  [c+6,c+8]  [c+9,c+11]
 * 3        [c,c+8]    [c+9,c+17] [c+18,c+26]
 * <p>
 * The hierarchical timing wheels works especially well when operations are completed before they time out.
 * Even when everything times out, it still has advantageous when there are many items in the timer.
 * Its insert cost (including reinsert) and delete cost are O(m) and O(1), respectively while priority
 * queue based timers takes O(log N) for both insert and delete where N is the number of items in the queue.
 * <p>
 * This class is not thread-safe. There should not be any add calls while advanceClock is executing.
 * It is caller's responsibility to enforce it. Simultaneous add calls are thread-safe.
 *
 * @author kafka、guest、L.cm
 */
public class TimingWheel {

	/**
	 * 时间轮由多个时间格组成，每个时间格就是 tickMs，它代表当前时间轮的基本时间跨度。
	 */
	private final long tickMs;
	/**
	 * 代表每一层时间轮的格数
	 */
	private final int wheelSize;
	/**
	 * 当前时间轮的总体时间跨度，interval=tickMs × wheelSize
	 */
	private final long interval;
	/**
	 * 任务计数器
	 */
	private final LongAdder taskCounter;
	/**
	 * 一个Timer只有一个delayQueue
	 */
	private final DelayQueue<TimerTaskList> queue;
	/**
	 * 表示时间轮当前所处的时间
	 */
	private long currentTime;
	/**
	 * 上层时间轮
	 */
	private volatile TimingWheel overflowWheel;
	/**
	 * 时间槽
	 */
	private final TimerTaskList[] buckets;

	public TimingWheel(long tickMs, int wheelSize, long startMs, LongAdder taskCounter, DelayQueue<TimerTaskList> queue) {
		this.tickMs = tickMs;
		this.wheelSize = wheelSize;
		this.taskCounter = taskCounter;
		this.queue = queue;
		this.interval = tickMs * wheelSize;
		// currentTime为 tickMs 的整数倍 这里做取整操作
		this.currentTime = startMs - (startMs % tickMs);
		this.buckets = new TimerTaskList[wheelSize];
		for (int i = 0; i < buckets.length; i++) {
			buckets[i] = new TimerTaskList(taskCounter);
		}
	}

	/**
	 * 添加任务到时间轮
	 *
	 * @param timerTaskEntry TimerTaskEntry
	 * @return 是否成功
	 */
	public boolean add(TimerTaskEntry timerTaskEntry) {
		long expiration = timerTaskEntry.getExpirationMs();
		if (timerTaskEntry.cancelled()) {
			// Cancelled
			return false;
		} else if (expiration < currentTime + tickMs) {
			// Already expired
			return false;
		} else if (expiration < currentTime + interval) {
			// Put in its own bucket
			long virtualId = expiration / tickMs;
			TimerTaskList bucket = buckets[(int) (virtualId % wheelSize)];
			bucket.add(timerTaskEntry);
			// Set the bucket expiration time
			if (bucket.setExpiration(virtualId * tickMs)) {
				// The bucket needs to be enqueued because it was an expired bucket
				// We only need to enqueue the bucket when its expiration time has changed, i.e. the wheel has advanced
				// and the previous buckets gets reused; further calls to set the expiration within the same wheel cycle
				// will pass in the same value and hence return false, thus the bucket with the same expiration will not
				// be enqueued multiple times.
				queue.offer(bucket);
			}
			return true;
		} else {
			// 放到上一层的时间轮
			if (overflowWheel == null) {
				addOverflowWheel();
			}
			return overflowWheel.add(timerTaskEntry);
		}
	}

	/**
	 * 推进时间
	 *
	 * @param timeMs ms
	 */
	public void advanceClock(Long timeMs) {
		if (timeMs >= currentTime + tickMs) {
			currentTime = timeMs - (timeMs % tickMs);
			// Try to advance the clock of the overflow wheel if present
			if (overflowWheel != null) {
				// 推进上层时间轮时间
				overflowWheel.advanceClock(currentTime);
			}
		}
	}

	/**
	 * 增加溢出时间轮
	 */
	private void addOverflowWheel() {
		synchronized (this) {
			if (overflowWheel == null) {
				overflowWheel = new TimingWheel(interval, wheelSize, currentTime, taskCounter, queue);
			}
		}
	}
}
