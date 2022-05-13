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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;

/**
 * @author kafka、guest
 */
public class SystemTimer implements Timer, Function<TimerTaskEntry, Void> {
	private static final Logger logger = LoggerFactory.getLogger(SystemTimer.class);
	/**
	 * 任务执行线程
	 */
	private final ExecutorService taskExecutor;
	/**
	 * 一个Timer只有一个delayQueue
	 */
	private final DelayQueue<TimerTaskList> delayQueue = new DelayQueue<>();
	/**
	 * 任务计数器
	 */
	private final LongAdder taskCounter = new LongAdder();
	/**
	 * 底层时间轮
	 */
	private final TimingWheel timingWheel;

	/**
	 * Locks used to protect data structures while ticking
	 */
	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

	private final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();

	private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

	public SystemTimer(String executeName) {
		this(1L, Timer.getHiresClockMs(), 20, executeName);
	}

	public SystemTimer(long tickMs, long startMs, int wheelSize, String executeName) {
		taskExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
			new LinkedBlockingQueue<>(Integer.MAX_VALUE), r -> new Thread(r, "SystemTimerExecutor" + executeName));
		timingWheel = new TimingWheel(tickMs, wheelSize, startMs, taskCounter, delayQueue);
	}

	/**
	 * Add a new task to this executor. It will be executed after the task's delay
	 * (beginning from the time of submission)
	 *
	 * @param timerTask the task to add
	 */
	@Override
	public void add(TimerTask timerTask) {
		readLock.lock();
		try {
			addTimerTaskEntry(new TimerTaskEntry(timerTask, timerTask.getDelayMs() + Timer.getHiresClockMs()));
		} finally {
			readLock.unlock();
		}
	}

	/**
	 * Advance the internal clock, executing any tasks whose expiration has been
	 * reached within the duration of the passed timeout.
	 *
	 * @param timeoutMs timeoutMs
	 * @return whether or not any tasks were executed
	 */
	@Override
	public boolean advanceClock(long timeoutMs) {
		try {
			TimerTaskList bucket = delayQueue.poll(timeoutMs, TimeUnit.MILLISECONDS);
			if (bucket != null) {
				writeLock.lock();
				try {
					while (bucket != null) {
						// 推进时间
						timingWheel.advanceClock(bucket.getExpiration());
						// 执行过期任务（包含降级操作）
						bucket.flush(this);
						bucket = delayQueue.poll();
					}
				} finally {
					writeLock.unlock();
				}
				return true;
			} else {
				return false;
			}
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * Get the number of tasks pending execution
	 *
	 * @return the number of tasks
	 */
	@Override
	public long size() {
		return taskCounter.sum();
	}

	/**
	 * ;     * Shutdown the timer service, leaving pending tasks unexpected
	 */
	@Override
	public void shutdown() {
		taskExecutor.shutdown();
	}

	private void addTimerTaskEntry(TimerTaskEntry timerTaskEntry) {
		// 添加失败任务直接执行
		if (!timingWheel.add(timerTaskEntry)) {
			// Already expired or cancelled
			if (!timerTaskEntry.cancelled()) {
				taskExecutor.submit(timerTaskEntry.getTimerTask());
			}
		}
	}

	/**
	 * Applies this function to the given argument.
	 *
	 * @param timerTaskEntry the function argument
	 * @return the function result
	 */
	@Override
	public Void apply(TimerTaskEntry timerTaskEntry) {
		addTimerTaskEntry(timerTaskEntry);
		return null;
	}
}
