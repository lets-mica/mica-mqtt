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

package net.dreamlu.iot.mqtt.core.server.support;

import net.dreamlu.iot.mqtt.core.server.auth.IMqttServerAuthHandler;
import org.tio.core.ChannelContext;

import java.util.Objects;

/**
 * 默认的认证处理
 *
 * @author L.cm
 */
public class DefaultMqttServerAuthHandler implements IMqttServerAuthHandler {
	private final boolean enabled;
	private final String authUserName;
	private final String authPassword;

	public DefaultMqttServerAuthHandler() {
		this(false, null, null);
	}

	public DefaultMqttServerAuthHandler(boolean enabled, String authUserName, String authPassword) {
		this.enabled = enabled;
		this.authUserName = enabled ? Objects.requireNonNull(authUserName, "Mqtt auth enabled but username is null.") : null;
		this.authPassword = enabled ? Objects.requireNonNull(authPassword, "Mqtt auth enabled but password is null.") : null;
	}

	@Override
	public boolean authenticate(ChannelContext context, String uniqueId, String clientId, String userName, String password) {
		return enabled && authUserName.equals(userName) && authPassword.equals(password);
	}

}
