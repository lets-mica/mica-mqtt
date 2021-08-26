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

package net.dreamlu.iot.mqtt.core.server.http.handler;

import org.tio.http.common.Method;

import java.util.Objects;

/**
 * Handler info
 *
 * @author L.cm
 */
public class HandlerInfo {
	private final Method method;
	private final HttpHandler handler;

	public HandlerInfo(Method method, HttpHandler handler) {
		this.method = method;
		this.handler = handler;
	}

	public Method getMethod() {
		return method;
	}

	public HttpHandler getHandler() {
		return handler;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof HandlerInfo)) {
			return false;
		}
		HandlerInfo that = (HandlerInfo) o;
		return method == that.method &&
			Objects.equals(handler, that.handler);
	}

	@Override
	public int hashCode() {
		return Objects.hash(method, handler);
	}
}
