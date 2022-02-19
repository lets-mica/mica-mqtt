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

import org.tio.utils.Threads;
import org.tio.utils.thread.pool.DefaultThreadFactory;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;
import org.tio.utils.thread.pool.TioCallerRunsPolicy;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * mqtt 线程工具类
 *
 * @author L.cm
 */
public final class ThreadUtil {

	/**
	 * 获取 tio group 线程池
	 *
	 * @param groupPoolSize group 线程大小
	 * @return ThreadPoolExecutor
	 */
	public static ThreadPoolExecutor getGroupExecutor(int groupPoolSize) {
		String threadName = "tio-group";
		DefaultThreadFactory threadFactory = DefaultThreadFactory.getInstance(threadName, Thread.MAX_PRIORITY);
		LinkedBlockingQueue<Runnable> runnableQueue = new LinkedBlockingQueue<>();
		ThreadPoolExecutor groupExecutor = new ThreadPoolExecutor(groupPoolSize, groupPoolSize,
			Threads.KEEP_ALIVE_TIME, TimeUnit.SECONDS, runnableQueue, threadFactory, new TioCallerRunsPolicy());
		groupExecutor.prestartCoreThread();
		return groupExecutor;
	}

	/**
	 * 获取 getTioExecutor 线程池
	 *
	 * @param tioPoolSize tio 线程池大小
	 * @return SynThreadPoolExecutor
	 */
	public static SynThreadPoolExecutor getTioExecutor(int tioPoolSize) {
		String threadName = "tio-worker";
		LinkedBlockingQueue<Runnable> runnableQueue = new LinkedBlockingQueue<>();
		DefaultThreadFactory defaultThreadFactory = DefaultThreadFactory.getInstance(threadName, Thread.MAX_PRIORITY);
		SynThreadPoolExecutor tioExecutor = new SynThreadPoolExecutor(tioPoolSize, tioPoolSize,
			Threads.KEEP_ALIVE_TIME, runnableQueue, defaultThreadFactory, threadName, new TioCallerRunsPolicy());
		tioExecutor.prestartCoreThread();
		return tioExecutor;
	}

}
