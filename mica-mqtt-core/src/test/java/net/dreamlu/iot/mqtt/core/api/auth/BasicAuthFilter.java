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

package net.dreamlu.iot.mqtt.core.api.auth;

import net.dreamlu.iot.mqtt.core.api.code.ResultCode;
import net.dreamlu.iot.mqtt.core.api.result.Result;
import net.dreamlu.iot.mqtt.core.core.HttpFilter;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.utils.hutool.StrUtil;

import java.util.Objects;

/**
 * Basic 认证
 *
 * @author L.cm
 */
public class BasicAuthFilter implements HttpFilter {
	public static final String BASIC_AUTH_HEADER_NAME = "Authorization";
	public static final String AUTHORIZATION_PREFIX = "Basic ";
	private final String token;

	public BasicAuthFilter(String token) {
		this.token = Objects.requireNonNull(token, "Basic auth token is null");
	}

	@Override
	public boolean filter(HttpRequest request) throws Exception {
		String authorization = request.getHeader(BASIC_AUTH_HEADER_NAME);
		if (StrUtil.isBlank(authorization)) {
			return false;
		}
		int length = AUTHORIZATION_PREFIX.length();
		if (length >= authorization.length()) {
			return false;
		}
		return token.equals(authorization.substring(length));
	}

	@Override
	public HttpResponse response(HttpRequest request, HttpResponse response) {
		return Result.fail(response, ResultCode.E103);
	}

}
