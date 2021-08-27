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

package net.dreamlu.iot.mqtt.core.server.http.api;

import com.alibaba.fastjson.JSON;
import net.dreamlu.iot.mqtt.codec.MqttMessageType;
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.core.server.dispatcher.IMqttMessageDispatcher;
import net.dreamlu.iot.mqtt.core.server.http.api.code.ResultCode;
import net.dreamlu.iot.mqtt.core.server.http.api.form.BaseForm;
import net.dreamlu.iot.mqtt.core.server.http.api.form.PublishForm;
import net.dreamlu.iot.mqtt.core.server.http.api.form.SubscribeForm;
import net.dreamlu.iot.mqtt.core.server.http.api.result.Result;
import net.dreamlu.iot.mqtt.core.server.http.handler.MqttHttpRoutes;
import net.dreamlu.iot.mqtt.core.server.model.Message;
import net.dreamlu.iot.mqtt.core.server.session.IMqttSessionManager;
import net.dreamlu.iot.mqtt.core.util.PayloadEncode;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.http.common.Method;
import org.tio.utils.hutool.StrUtil;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

/**
 * mqtt http api
 *
 * @author L.cm
 */
public class MqttHttpApi {
	private final IMqttMessageDispatcher messageDispatcher;
	private final IMqttSessionManager sessionManager;

	public MqttHttpApi(IMqttMessageDispatcher messageDispatcher,
					   IMqttSessionManager sessionManager) {
		this.messageDispatcher = messageDispatcher;
		this.sessionManager = sessionManager;
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
		PublishForm form = readForm(request, (requestBody) ->
			JSON.parseObject(requestBody, PublishForm.class)
		);
		HttpResponse response = new HttpResponse();
		if (form == null) {
			return Result.fail(response, ResultCode.E101);
		}
		// 表单校验
		HttpResponse validResponse = validForm(form, response);
		if (validResponse != null) {
			return validResponse;
		}
		send(form);
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
		List<PublishForm> formList = readForm(request, (requestBody) -> {
			String jsonBody = new String(requestBody, StandardCharsets.UTF_8);
			return JSON.parseArray(jsonBody, PublishForm.class);
		});
		HttpResponse response = new HttpResponse();
		if (formList == null || formList.isEmpty()) {
			return Result.fail(response, ResultCode.E101);
		}
		// 参数校验，保证一个批次同时不成功，所以先校验
		for (PublishForm form : formList) {
			// 表单校验
			HttpResponse validResponse = validForm(form, response);
			if (validResponse != null) {
				return validResponse;
			}
		}
		// 批量发送
		for (PublishForm form : formList) {
			send(form);
		}
		return Result.ok(response);
	}

	private void send(PublishForm form) {
		String payload = form.getPayload();
		Message message = new Message();
		message.setMessageType(MqttMessageType.PUBLISH.value());
		message.setClientId(form.getClientId());
		message.setTopic(form.getTopic());
		message.setQos(form.getQos());
		message.setRetain(form.isRetain());
		// payload 解码
		if (StrUtil.isNotBlank(payload)) {
			message.setPayload(PayloadEncode.decode(payload, form.getEncoding()));
		}
		messageDispatcher.send(message);
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
		SubscribeForm form = readForm(request, (requestBody) ->
			JSON.parseObject(requestBody, SubscribeForm.class)
		);
		HttpResponse response = new HttpResponse();
		if (form == null) {
			return Result.fail(response, ResultCode.E101);
		}
		// 表单校验
		HttpResponse validResponse = validForm(form, response);
		if (validResponse != null) {
			return validResponse;
		}
		int qos = form.getQos();
		if (qos < 0 || qos > 2) {
			return Result.fail(response, ResultCode.E101);
		}
		// 接口手动添加的订阅关系，可用来调试，不建议其他场景使用
		sessionManager.addSubscribe(form.getTopic(), form.getClientId(), MqttQoS.valueOf(qos));
		return Result.ok(response);
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
		List<SubscribeForm> formList = readForm(request, (requestBody) -> {
			String jsonBody = new String(requestBody, StandardCharsets.UTF_8);
			return JSON.parseArray(jsonBody, SubscribeForm.class);
		});
		HttpResponse response = new HttpResponse();
		if (formList == null || formList.isEmpty()) {
			return Result.fail(response, ResultCode.E101);
		}
		// 参数校验，保证一个批次同时不成功，所以先校验
		for (SubscribeForm form : formList) {
			// 表单校验
			HttpResponse validResponse = validForm(form, response);
			if (validResponse != null) {
				return validResponse;
			}
			int qos = form.getQos();
			if (qos < 0 || qos > 2) {
				return Result.fail(response, ResultCode.E101);
			}
		}
		// 批量处理
		for (SubscribeForm form : formList) {
			// 接口手动添加的订阅关系，可用来调试，不建议其他场景使用
			sessionManager.addSubscribe(form.getTopic(), form.getClientId(), MqttQoS.valueOf(form.getQos()));
		}
		return Result.ok(response);
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
		BaseForm form = readForm(request, (requestBody) ->
			JSON.parseObject(requestBody, BaseForm.class)
		);
		HttpResponse response = new HttpResponse();
		if (form == null) {
			return Result.fail(response, ResultCode.E101);
		}
		// 表单校验
		HttpResponse validResponse = validForm(form, response);
		if (validResponse != null) {
			return validResponse;
		}
		// 接口手动取消的订阅关系，可用来调试，不建议其他场景使用
		sessionManager.removeSubscribe(form.getTopic(), form.getClientId());
		return Result.ok(response);
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
		List<BaseForm> formList = readForm(request, (requestBody) -> {
			String jsonBody = new String(requestBody, StandardCharsets.UTF_8);
			return JSON.parseArray(jsonBody, BaseForm.class);
		});
		HttpResponse response = new HttpResponse();
		if (formList == null || formList.isEmpty()) {
			return Result.fail(response, ResultCode.E101);
		}
		// 参数校验，保证一个批次同时不成功，所以先校验
		for (BaseForm form : formList) {
			// 表单校验
			HttpResponse validResponse = validForm(form, response);
			if (validResponse != null) {
				return validResponse;
			}
		}
		// 批量处理
		for (BaseForm form : formList) {
			// 接口手动添加的订阅关系，可用来调试，不建议其他场景使用
			sessionManager.removeSubscribe(form.getTopic(), form.getClientId());
		}
		return Result.ok(response);
	}

	/**
	 * 读取表单
	 *
	 * @param request  HttpRequest
	 * @param function Function
	 * @param <T>      泛型
	 * @return 表单
	 */
	private static <T> T readForm(HttpRequest request, Function<byte[], T> function) {
		byte[] requestBody = request.getBody();
		if (requestBody == null) {
			return null;
		}
		return function.apply(requestBody);
	}

	/**
	 * 校验表单
	 *
	 * @param form     BaseForm
	 * @param response HttpResponse
	 * @return 表单
	 */
	private static HttpResponse validForm(BaseForm form, HttpResponse response) {
		// 必须的参数
		String clientId = form.getClientId();
		if (StrUtil.isBlank(clientId)) {
			return Result.fail(response, ResultCode.E101);
		}
		String topic = form.getTopic();
		if (StrUtil.isBlank(topic)) {
			return Result.fail(response, ResultCode.E101);
		}
		return null;
	}

	/**
	 * 注册路由
	 */
	public void register() {
		// @formatter:off
		MqttHttpRoutes.register(Method.POST, "/api/v1/mqtt/publish", this::publish);
		MqttHttpRoutes.register(Method.POST, "/api/v1/mqtt/publish/batch", this::publishBatch);
		MqttHttpRoutes.register(Method.POST, "/api/v1/mqtt/subscribe", this::subscribe);
		MqttHttpRoutes.register(Method.POST, "/api/v1/mqtt/subscribe/batch", this::subscribeBatch);
		MqttHttpRoutes.register(Method.POST, "/api/v1/mqtt/unsubscribe", this::unsubscribe);
		MqttHttpRoutes.register(Method.POST, "/api/v1/mqtt/unsubscribe/batch", this::unsubscribeBatch);
		// @formatter:on
	}

}
