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

package net.dreamlu.iot.mqtt.core.util;

/**
 * Mqtt Topic 工具
 *
 * @author L.cm
 */
public final class TopicUtil {

	/**
	 * 判断 topicFilter topicName 是否匹配
	 *
	 * @param topicFilter topicFilter
	 * @param topicName   topicName
	 * @return 是否匹配
	 */
	public static boolean match(String topicFilter, String topicName) {
		char[] topicFilterChars = topicFilter.toCharArray();
		char[] topicNameChars = topicName.toCharArray();
		int topicFilterLength = topicFilterChars.length;
		int topicNameLength = topicNameChars.length;
		int topicFilterIdxEnd = topicFilterLength - 1;
		int topicNameIdxEnd = topicNameLength - 1;
		char ch;
		// 是否进入 + 号层级通配符
		boolean inLayerWildcard = false;
		for (int i = 0; i < topicFilterLength; i++) {
			ch = topicFilterChars[i];
			if (ch == '#') {
				// 校验: # 通配符只能在最后一位
				if (i < topicFilterIdxEnd) {
					throw new IllegalArgumentException("Mqtt subscribe topicFilter illegal:" + topicFilter);
				}
				return true;
			} else if (ch == '+') {
				// 校验: 单独 + 是允许的，判断 + 号前一位是否为 /
				if (i > 0 && topicFilterChars[i - 1] != '/') {
					throw new IllegalArgumentException("Mqtt subscribe topicFilter illegal:" + topicFilter);
				}
				// 如果 + 是最后一位，判断 topicName 中是否还存在层级 /
				if (i == topicFilterIdxEnd && topicNameLength > i) {
					for (int j = i; j < topicNameLength; j++) {
						if (topicNameChars[j] == '/') {
							return false;
						}
					}
				}
				inLayerWildcard = true;
				continue;
			} else if (ch == '/') {
				if (inLayerWildcard) {
					inLayerWildcard = false;
				}
				// 预读下一位，如果是 #，并且 topicName 位数已经不足
				int next = i + 1;
				if ((topicFilterLength > next) && topicFilterChars[next] == '#' && topicNameLength < next) {
					return true;
				}
			}
			// topicName 长度不够了
			if (topicNameIdxEnd < i) {
				return false;
			}
			// 进入通配符
			if (!inLayerWildcard && ch != topicNameChars[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取处理完成之后的 topic
	 *
	 * @param topicTemplate topic 模板
	 * @return 获取处理完成之后的 topic
	 */
	public static String getTopicFilter(String topicTemplate) {
		// 替换 ${name} 为 + 替换 #{name} 为 #
		return topicTemplate.replaceAll("\\$\\{[\\s\\w.]+}", "+")
			.replaceAll("#\\{[\\s\\w.]+}", "#");
	}

}
