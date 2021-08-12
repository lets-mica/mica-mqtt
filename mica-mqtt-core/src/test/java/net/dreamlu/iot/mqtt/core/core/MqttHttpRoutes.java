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

package net.dreamlu.iot.mqtt.core.core;

import org.tio.http.common.Method;
import org.tio.http.common.RequestLine;

import java.util.HashMap;
import java.util.Map;

/**
 * mqtt http api 路由
 *
 * @author L.cm
 */
public final class MqttHttpRoutes {
	private static final Map<MappingInfo, HttpHandler> ROUTS = new HashMap<>();

	/**
	 * 注册路由
	 *
	 * @param method  请求方法
	 * @param path    路径
	 * @param handler HttpHandler
	 */
	public static void register(Method method, String path, HttpHandler handler) {
		ROUTS.put(new MappingInfo(method, path), handler);
	}

	/**
	 * 读取路由
	 *
	 * @param method 请求方法
	 * @param path   路径
	 * @return HttpHandler
	 */
	public static HttpHandler getHandler(Method method, String path) {
		return ROUTS.get(new MappingInfo(method, path));
	}

	/**
	 * 读取路由
	 *
	 * @param requestLine RequestLine
	 * @return HttpHandler
	 */
	public static HttpHandler getHandler(RequestLine requestLine) {
		return getHandler(requestLine.getMethod(), requestLine.getPath());
	}

}
