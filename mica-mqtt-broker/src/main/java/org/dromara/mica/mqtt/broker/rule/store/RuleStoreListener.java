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

package org.dromara.mica.mqtt.broker.rule.store;

import org.dromara.mica.mqtt.broker.rule.Rule;

/**
 * 持久化变更监听接口（替代 Flux watch，避免依赖 reactor）。
 *
 * @author L.cm
 */
public interface RuleStoreListener {

	/**
	 * 规则被保存或新增。
	 *
	 * @param rule 规则
	 */
	void onSaved(Rule rule);

	/**
	 * 规则被删除。
	 *
	 * @param ruleId 规则 id
	 */
	void onDeleted(String ruleId);
}
