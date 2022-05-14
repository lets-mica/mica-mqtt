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


/**
 * 封装定时任务
 *
 * @author kafka、guest、L.cm
 */
public class TimerTaskEntry implements Comparable<TimerTaskEntry> {
	private volatile TimerTaskList list;

	public TimerTaskEntry next;

	public TimerTaskEntry prev;

	private final TimerTask timerTask;

	private final long expirationMs;

	/**
	 * 构造器
	 *
	 * @param timerTask    定时任务
	 * @param expirationMs 到期时间
	 */
	public TimerTaskEntry(TimerTask timerTask, long expirationMs) {
		if (timerTask != null) {
			timerTask.setTimerTaskEntry(this);
		}
		this.timerTask = timerTask;
		this.expirationMs = expirationMs;
	}

	public boolean cancelled() {
		return timerTask.getTimerTaskEntry() != this;
	}

	public void remove() {
		TimerTaskList currentList = list;
		// If remove is called when another thread is moving the entry from a task entry list to another,
		// this may fail to remove the entry due to the change of value of list. Thus, we retry until the list becomes null.
		// In a rare case, this thread sees null and exits the loop, but the other thread insert the entry to another list later.
		while (currentList != null) {
			currentList.remove(this);
			currentList = list;
		}
	}

	@Override
	public int compareTo(TimerTaskEntry that) {
		if (that == null) {
			throw new NullPointerException("TimerTaskEntry is null");
		}
		return Long.compare(this.expirationMs, that.expirationMs);
	}


	public long getExpirationMs() {
		return expirationMs;
	}

	public TimerTask getTimerTask() {
		return timerTask;
	}

	public TimerTaskList getList() {
		return list;
	}

	public void setList(TimerTaskList list) {
		this.list = list;
	}

}
