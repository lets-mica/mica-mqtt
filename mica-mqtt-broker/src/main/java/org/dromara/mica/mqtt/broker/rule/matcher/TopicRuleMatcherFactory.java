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

package org.dromara.mica.mqtt.broker.rule.matcher;

import java.util.Map;

/**
 * 默认 matcher：topic 命中即放行（实际判定由 MqttFunctionManager 完成）。
 *
 * @author L.cm
 */
public class TopicRuleMatcherFactory implements RuleMatcherFactory {

	public static final String TYPE = "topic";

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public RuleMatcher create(Map<String, String> props) {
		// 实际 topic 匹配在 MqttFunctionManager 已完成，此处直接放行。
		return ctx -> true;
	}
}
