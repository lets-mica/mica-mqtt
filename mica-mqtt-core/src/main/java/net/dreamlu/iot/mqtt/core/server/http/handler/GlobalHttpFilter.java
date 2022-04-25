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

import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;

/**
 * 全局 http 过滤器
 *
 * @author L.cm
 */
public interface GlobalHttpFilter {

	/**
	 * 处理请求
	 *
	 * @param request HttpRequest
	 * @return 可以为null
	 * @throws Exception Exception
	 */
	boolean filter(HttpRequest request) throws Exception;

	/**
	 * 响应
	 *
	 * @param request  HttpRequest
	 * @param response HttpResponse
	 * @return HttpResponse
	 */
	HttpResponse response(HttpRequest request, HttpResponse response);

}
