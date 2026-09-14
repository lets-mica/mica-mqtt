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

package org.dromara.mica.mqtt.core.server.http.api.auth;

import net.dreamlu.mica.net.http.common.HttpRequest;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * Basic Auth 校验器,实现 {@link ITokenValidator},
 * 将 username:password 编码为 Base64 后与请求 token 比较。
 *
 * @author L.cm
 */
public class BasicAuthValidator implements ITokenValidator {
	private final String token;

	public BasicAuthValidator(String username, String password) {
		Objects.requireNonNull(username, "Basic auth username is null");
		Objects.requireNonNull(password, "Basic auth password is null");
		this.token = Base64.getEncoder().encodeToString((username + ':' + password).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public boolean validate(HttpRequest request, String token) {
		return this.token.equals(token);
	}
}
