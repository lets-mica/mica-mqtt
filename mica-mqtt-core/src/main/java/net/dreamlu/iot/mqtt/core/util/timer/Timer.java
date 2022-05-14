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

import java.util.concurrent.TimeUnit;

/**
 * Timer
 *
 * @author kafka、guest、L.cm
 */
public interface Timer {

	/**
	 * 添加新的任务到当前执行器（线程池），在任务过期后会执行任务。
	 *
	 * @param timerTask the task to add
	 */
	void add(TimerTask timerTask);

	/**
	 * 推进内部时钟，执行任何在走过的时间间隔内过期的任务
	 *
	 * @param timeoutMs timeoutMs
	 * @return whether any tasks were executed
	 */
	boolean advanceClock(long timeoutMs);

	/**
	 * 取得待执行的任务数量
	 *
	 * @return the number of tasks
	 */
	long size();

	/**
	 * 关闭定时器服务，待执行的任务将不会被执行
	 */
	void shutdown();

	static Long getHiresClockMs() {
		return TimeUnit.NANOSECONDS.toMillis(System.nanoTime());
	}

}
