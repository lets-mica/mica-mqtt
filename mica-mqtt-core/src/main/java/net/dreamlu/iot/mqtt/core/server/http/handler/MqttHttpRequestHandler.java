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

import net.dreamlu.iot.mqtt.core.server.http.api.code.ResultCode;
import net.dreamlu.iot.mqtt.core.server.http.api.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.*;
import org.tio.http.common.handler.HttpRequestHandler;

import java.util.List;

/**
 * mqtt http 消息处理
 *
 * @author L.cm
 */
public class MqttHttpRequestHandler implements HttpRequestHandler {
	private static final Logger logger = LoggerFactory.getLogger(MqttHttpRequestHandler.class);

	@Override
	public HttpResponse handler(HttpRequest request) {
		RequestLine requestLine = request.getRequestLine();
		// 1. 处理过滤器
		List<HttpFilter> httpFilters = MqttHttpRoutes.getFilters();
		try {
			for (HttpFilter filter : httpFilters) {
				if (!filter.filter(request)) {
					return filter.response(request);
				}
			}
		} catch (Exception e) {
			return resp500(request, requestLine, e);
		}
		// 2. 路由处理
		HandlerInfo handler = MqttHttpRoutes.getHandler(requestLine);
		if (handler == null) {
			return resp404(request, requestLine);
		}
		Method method = requestLine.getMethod();
		if (handler.getMethod() != method) {
			return Result.fail(new HttpResponse(request), ResultCode.E104);
		}
		if (logger.isInfoEnabled()) {
			logger.info("mqtt http api {} path:{}", method.name(), requestLine.getPathAndQuery());
		}
		try {
			return handler.getHandler().apply(request);
		} catch (Exception e) {
			return resp500(request, requestLine, e);
		}
	}

	@Override
	public HttpResponse resp404(HttpRequest request, RequestLine requestLine) {
		if (logger.isErrorEnabled()) {
			logger.error("mqtt http {} path:{} 404", requestLine.getMethod().name(), requestLine.getPathAndQuery());
		}
		HttpResponse response = new HttpResponse(request);
		response.setStatus(HttpResponseStatus.C404);
		return response;
	}

	@Override
	public HttpResponse resp500(HttpRequest request, RequestLine requestLine, Throwable throwable) {
		if (logger.isErrorEnabled()) {
			logger.error("mqtt http {} path:{} error", requestLine.getMethod().name(), requestLine.getPathAndQuery(), throwable);
		}
		HttpResponse response = new HttpResponse(request);
		return Result.fail(response, ResultCode.E105);
	}

	@Override
	public HttpConfig getHttpConfig(HttpRequest request) {
		return request.getHttpConfig();
	}

	@Override
	public void clearStaticResCache() {
		// ignore
	}

}
