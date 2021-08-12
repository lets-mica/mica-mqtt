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

package net.dreamlu.iot.mqtt.core;

import net.dreamlu.iot.mqtt.core.api.code.ResultCode;
import net.dreamlu.iot.mqtt.core.api.result.Result;
import net.dreamlu.iot.mqtt.core.core.HttpHandler;
import net.dreamlu.iot.mqtt.core.core.MqttHttpRoutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.*;
import org.tio.http.common.handler.HttpRequestHandler;

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
		HttpHandler handler = MqttHttpRoutes.getHandler(requestLine);
		if (handler == null) {
			return resp404(request, requestLine);
		}
		try {
			return handler.apply(request);
		} catch (Exception e) {
			return resp500(request, requestLine, e);
		}
	}

	@Override
	public HttpResponse resp404(HttpRequest request, RequestLine requestLine) {
		logger.error("mqtt http {} path:{} 404", requestLine.getMethod().name(), requestLine.getPathAndQuery());
		HttpResponse response = new HttpResponse(request);
		response.setStatus(HttpResponseStatus.C404);
		return response;
	}

	@Override
	public HttpResponse resp500(HttpRequest request, RequestLine requestLine, Throwable throwable) {
		logger.error("mqtt http {} path:{} error", requestLine.getMethod().name(), requestLine.getPathAndQuery(), throwable);
		HttpResponse response = new HttpResponse(request);
		return Result.fail(response, ResultCode.E105);
	}

	@Override
	public HttpConfig getHttpConfig(HttpRequest request) {
		return request.getHttpConfig();
	}

	@Override
	public void clearStaticResCache() {

	}

}
