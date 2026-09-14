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

/**
 * Token 校验器，用于 OAuth2 / JWT / 自建 token 服务等场景。
 * <p>
 * 用户可自行实现该接口,在实现中调用第三方校验服务(同步或异步)。
 *
 * @author L.cm
 */
@FunctionalInterface
public interface ITokenValidator {

	/**
	 * 校验 token
	 *
	 * @param request 当前 HTTP 请求,便于读取 header / param 等附加信息
	 * @param token   从 Authorization: Bearer xxx 中提取的 token
	 * @return 是否校验通过
	 */
	boolean validate(HttpRequest request, String token);
}
