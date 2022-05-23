/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.net.dreamlu.net).
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
 * 空的 AckService，可用于压测，减少资源占用
 *
 * @author L.cm
 */
public class EmptyAckService implements AckService {
	@Override
	public AckTimerTask addTask(Runnable command, int maxRetryCount, int retryIntervalSecs) {
		return null;
	}

	@Override
	public void start() {

	}

	@Override
	public void stop() {

	}
}
