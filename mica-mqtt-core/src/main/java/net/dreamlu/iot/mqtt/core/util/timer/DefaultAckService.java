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

package net.dreamlu.iot.mqtt.core.util.timer;

/**
 * Default AckService
 *
 * @author L.cm
 */
public class DefaultAckService implements AckService {
	private final SystemTimer systemTimer;
	private final TimingWheelThread timingWheelThread;

	public DefaultAckService() {
		this(100L, 60);
	}

	public DefaultAckService(long tickMs, int wheelSize) {
		this(new SystemTimer(tickMs, wheelSize, "DefaultTimerTaskService"));
	}

	public DefaultAckService(SystemTimer systemTimer) {
		this(systemTimer, new TimingWheelThread(systemTimer));
	}

	public DefaultAckService(SystemTimer systemTimer, TimingWheelThread timingWheelThread) {
		this.systemTimer = systemTimer;
		this.timingWheelThread = timingWheelThread;
	}

	@Override
	public AckTimerTask addTask(Runnable command, int maxRetryCount, int retryIntervalSecs) {
		AckTimerTask ackTimerTask = new AckTimerTask(this.systemTimer, command, maxRetryCount, retryIntervalSecs);
		this.systemTimer.add(ackTimerTask);
		return ackTimerTask;
	}

	@Override
	public void start() {
		timingWheelThread.start();
	}

	@Override
	public void stop() {
		timingWheelThread.shutdown();
		systemTimer.shutdown();
	}

}
