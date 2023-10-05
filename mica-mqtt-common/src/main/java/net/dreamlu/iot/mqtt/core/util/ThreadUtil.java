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

import org.tio.utils.thread.ThreadUtils;
import org.tio.utils.thread.pool.SynThreadPoolExecutor;

import java.util.concurrent.ExecutorService;

/**
 * mqtt 线程工具类，已弃用，使用 org.tio.utils.thread.ThreadUtils
 *
 * @author L.cm
 */
@Deprecated
public final class ThreadUtil {

	/**
	 * 挂起当前线程，建议使用 ThreadUtils#sleep
	 *
	 * @param millis 挂起的毫秒数
	 * @return 被中断返回false，否则true
	 */
	public static boolean sleep(long millis) {
		return ThreadUtils.sleep(millis);
	}

	/**
	 * 获取 tio group 线程池，建议使用 ThreadUtils#getGroupExecutor
	 *
	 * @param groupPoolSize group 线程大小
	 * @return ThreadPoolExecutor
	 */
	public static ExecutorService getGroupExecutor(int groupPoolSize) {
		return ThreadUtils.getGroupExecutor(groupPoolSize);
	}

	/**
	 * 获取 getTioExecutor 线程池，建议使用 ThreadUtils#getTioExecutor
	 *
	 * @param tioPoolSize tio 线程池大小
	 * @return SynThreadPoolExecutor
	 */
	public static SynThreadPoolExecutor getTioExecutor(int tioPoolSize) {
		return ThreadUtils.getTioExecutor(tioPoolSize);
	}

	/**
	 * 获取 mqtt 业务线程池，建议使用 ThreadUtils#getBizExecutor
	 *
	 * @param poolSize 业务线程池大小
	 * @return ThreadPoolExecutor
	 */
	public static ExecutorService getMqttExecutor(int poolSize) {
		return ThreadUtils.getBizExecutor(poolSize);
	}

}
