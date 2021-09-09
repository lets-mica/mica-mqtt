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

package net.dreamlu.iot.mqtt.broker.util;

import net.dreamlu.mica.core.utils.CharPool;

/**
 * redis 工具
 *
 * @author L.cm
 */
public class RedisUtil {

	/**
	 * 转换成 redis 的 pattern 规则
	 *
	 * @return pattern
	 */
	public static String getTopicPattern(String topicFilter) {
		// mqtt 分享主题 $share/{ShareName}/{filter}
		return topicFilter
			.replace(CharPool.PLUS, CharPool.STAR)
			.replace(CharPool.HASH, CharPool.STAR);
	}

}
