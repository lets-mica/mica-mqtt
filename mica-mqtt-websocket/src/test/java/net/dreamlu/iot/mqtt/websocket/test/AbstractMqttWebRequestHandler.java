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

package net.dreamlu.iot.mqtt.websocket.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tio.http.common.*;
import org.tio.http.common.handler.HttpRequestHandler;

/**
 * mqtt http 消息处理
 *
 * @author L.cm
 */
public abstract class AbstractMqttWebRequestHandler implements HttpRequestHandler {
	protected static final Logger logger = LoggerFactory.getLogger(AbstractMqttWebRequestHandler.class);

	@Override
	public HttpResponse resp404(HttpRequest request, RequestLine requestLine) {
		logger.error("t-io http {} path:{} 404", requestLine.getMethod().name(), requestLine.getPathAndQuery());
		HttpResponse httpResponse = new HttpResponse(request);
		httpResponse.setStatus(HttpResponseStatus.C404);
		return httpResponse;
	}

	@Override
	public HttpResponse resp500(HttpRequest request, RequestLine requestLine, Throwable throwable) {
		logger.error("t-io http {} path:{} error", requestLine.getMethod().name(), requestLine.getPathAndQuery(), throwable);
		HttpResponse httpResponse = new HttpResponse(request);
		httpResponse.setStatus(HttpResponseStatus.C500);
		return httpResponse;
	}

	@Override
	public HttpConfig getHttpConfig(HttpRequest request) {
		return request.getHttpConfig();
	}

	@Override
	public void clearStaticResCache() {

	}

}
