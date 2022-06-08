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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

/**
 * TimingWheelThread
 *
 * @author L.cm
 */
public class TimingWheelThread extends Thread {
	private static final Logger log = LoggerFactory.getLogger(TimingWheelThread.class);

	private final Timer timer;
	private final long workTimeoutMs;
	private final CountDownLatch shutdownInitiated;
	private final CountDownLatch shutdownComplete;
	private volatile boolean isStarted = false;

	public TimingWheelThread(Timer timer) {
		this(timer, 200L);
	}

	public TimingWheelThread(Timer timer, long workTimeoutMs) {
		super();
		this.timer = timer;
		this.workTimeoutMs = workTimeoutMs;
		this.shutdownInitiated = new CountDownLatch(1);
		this.shutdownComplete = new CountDownLatch(1);
		this.setDaemon(false);
	}

	public boolean isShutdownInitiated() {
		return shutdownInitiated.getCount() == 0;
	}

	public boolean isRunning() {
		return !isShutdownInitiated();
	}

	@Override
	public void run() {
		isStarted = true;
		log.info("Starting");
		try {
			while (isRunning()) {
				timer.advanceClock(workTimeoutMs);
			}
		} catch (Exception e) {
			if (isRunning()) {
				log.error("Error due to", e);
			}
		} finally {
			shutdownComplete.countDown();
		}
		log.info("Stopped");
	}

	public void shutdown() {
		initiateShutdown();
		awaitShutdown();
	}

	private void initiateShutdown() {
		synchronized (this) {
			if (isRunning()) {
				shutdownInitiated.countDown();
			}
		}
	}

	/**
	 * After calling initiateShutdown(), use this API to wait until the shutdown is complete
	 */
	private void awaitShutdown() {
		if (!isShutdownInitiated()) {
			throw new IllegalStateException("initiateShutdown() was not called before awaitShutdown()");
		} else {
			if (isStarted) {
				try {
					shutdownComplete.await();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}

}
