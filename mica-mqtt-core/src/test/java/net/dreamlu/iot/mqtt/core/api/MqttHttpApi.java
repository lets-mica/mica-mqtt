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

package net.dreamlu.iot.mqtt.core.api;

import com.alibaba.fastjson.JSON;
import net.dreamlu.iot.mqtt.core.api.form.PublishForm;
import net.dreamlu.iot.mqtt.core.api.result.Result;
import net.dreamlu.iot.mqtt.core.core.MqttHttpRoutes;
import net.dreamlu.iot.mqtt.core.server.MqttServer;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.HttpResponseStatus;
import org.tio.http.common.Method;

/**
 * mqtt http api
 *
 * @author L.cm
 */
public class MqttHttpApi {
	private final MqttServer mqttServer;

	public MqttHttpApi(MqttServer mqttServer) {
		this.mqttServer = mqttServer;
	}

	/**
	 * 消息发布
	 * <p>
	 * POST /api/v1/mqtt/publish
	 *
	 * @param request HttpRequest
	 * @return HttpResponse
	 */
	public HttpResponse publish(HttpRequest request) throws Exception {
		byte[] requestBody = request.getBody();
		HttpResponse response = new HttpResponse();
		if (requestBody == null) {
			response.setStatus(HttpResponseStatus.C400);
			return response;
		}
		PublishForm form = JSON.parseObject(requestBody, PublishForm.class);
		String clientId = form.getClientId();
		String topic = form.getTopic();
		String payload = form.getPayload();
//		mqttServer.publish()
		return Result.ok(response);
	}

	/**
	 * 消息批量发布
	 * <p>
	 * POST /api/v1/mqtt/publish/batch
	 *
	 * @param request HttpRequest
	 * @return HttpResponse
	 */
	public HttpResponse publishBatch(HttpRequest request) throws Exception {
		return null;
	}

	/**
	 * 主题订阅
	 * <p>
	 * POST /api/v1/mqtt/subscribe
	 *
	 * @param request HttpRequest
	 * @return HttpResponse
	 */
	public HttpResponse subscribe(HttpRequest request) throws Exception {
		return null;
	}

	/**
	 * 主题批量订阅
	 * <p>
	 * POST /api/v1/mqtt/subscribe/batch
	 *
	 * @param request HttpRequest
	 * @return HttpResponse
	 */
	public HttpResponse subscribeBatch(HttpRequest request) throws Exception {
		return null;
	}

	/**
	 * 取消订阅
	 * <p>
	 * POST /api/v1/mqtt/unsubscribe
	 *
	 * @param request HttpRequest
	 * @return HttpResponse
	 */
	public HttpResponse unsubscribe(HttpRequest request) throws Exception {
		return null;
	}

	/**
	 * 批量取消订阅
	 * <p>
	 * POST /api/v1/mqtt/unsubscribe/batch
	 *
	 * @param request HttpRequest
	 * @return HttpResponse
	 */
	public HttpResponse unsubscribeBatch(HttpRequest request) throws Exception {
		return null;
	}

	/**
	 * 注册路由
	 */
	public void register() {
		// @formatter:off
		MqttHttpRoutes.register(Method.POST, "/api/v1/mqtt/publish",			this::publish);
		MqttHttpRoutes.register(Method.POST, "/api/v1/mqtt/publish/batch",		this::publishBatch);
		MqttHttpRoutes.register(Method.POST, "/api/v1/mqtt/subscribe",			this::subscribe);
		MqttHttpRoutes.register(Method.POST, "/api/v1/mqtt/subscribe/batch",	this::subscribeBatch);
		MqttHttpRoutes.register(Method.POST, "/api/v1/mqtt/unsubscribe",		this::unsubscribe);
		MqttHttpRoutes.register(Method.POST, "/api/v1/mqtt/unsubscribe/batch",	this::unsubscribeBatch);
		// @formatter:on
	}

}
