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
 * 规则变更事件。
 *
 * @author L.cm
 */
public final class RuleEvent {

	public enum Type {
		ADDED, UPDATED, REMOVED
	}

	private final Type type;
	private final String ruleId;
	private final Rule rule;
	private final String oldRuleId;

	private RuleEvent(Type type, String ruleId, Rule rule, String oldRuleId) {
		this.type = type;
		this.ruleId = ruleId;
		this.rule = rule;
		this.oldRuleId = oldRuleId;
	}

	public static RuleEvent added(Rule rule) {
		return new RuleEvent(Type.ADDED, rule.getId(), rule, null);
	}

	public static RuleEvent updated(Rule rule, String oldRuleId) {
		return new RuleEvent(Type.UPDATED, rule.getId(), rule, oldRuleId);
	}

	public static RuleEvent removed(String ruleId) {
		return new RuleEvent(Type.REMOVED, ruleId, null, null);
	}

	public Type getType() {
		return type;
	}

	public String getRuleId() {
		return ruleId;
	}

	public Rule getRule() {
		return rule;
	}

	public String getOldRuleId() {
		return oldRuleId;
	}
}
