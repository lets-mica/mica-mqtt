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

package net.dreamlu.iot.mqtt.websocket.test.api.result;

import com.alibaba.fastjson.JSONObject;
import net.dreamlu.iot.mqtt.websocket.test.api.code.ResultCode;
import org.tio.http.common.HeaderName;
import org.tio.http.common.HeaderValue;
import org.tio.http.common.HttpResponse;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * api Result
 *
 * @author L.cm
 */
public final class Result {
	private static final Charset UTF_8 = StandardCharsets.UTF_8;
	private static final String UTF_8_NAME = UTF_8.name();

	/**
	 * 响应成功
	 *
	 * @param response HttpResponse
	 */
	public static HttpResponse ok(HttpResponse response) {
		ResultCode resultCode = ResultCode.SUCCESS;
		JSONObject json = new JSONObject();
		json.put("code", resultCode.getResultCode());
		return result(response, resultCode, json);
	}

	/**
	 * 响应成功
	 *
	 * @param response HttpResponse
	 * @param data     Object
	 */
	public static HttpResponse ok(HttpResponse response, Object data) {
		ResultCode resultCode = ResultCode.SUCCESS;
		JSONObject json = new JSONObject();
		json.put("code", resultCode.getResultCode());
		json.put("data", data);
		return result(response, resultCode, json);
	}

	/**
	 * 响应失败
	 *
	 * @param response   HttpResponse
	 * @param resultCode ResultCode
	 */
	public static HttpResponse fail(HttpResponse response, ResultCode resultCode) {
		JSONObject json = new JSONObject();
		json.put("code", resultCode.getResultCode());
		return result(response, resultCode, json);
	}

	private static HttpResponse result(HttpResponse response, ResultCode resultCode, JSONObject json) {
		response.addHeader(HeaderName.Content_Type, HeaderValue.Content_Type.TEXT_PLAIN_JSON);
		response.setStatus(resultCode.getStatusCode());
		response.setBody(json.toJSONString().getBytes(UTF_8));
		response.setCharset(UTF_8_NAME);
		return response;
	}

}
