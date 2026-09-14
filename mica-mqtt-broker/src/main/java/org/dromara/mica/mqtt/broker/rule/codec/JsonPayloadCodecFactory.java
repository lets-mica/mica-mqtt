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

package org.dromara.mica.mqtt.broker.rule.codec;

/**
 * {@link JsonPayloadCodec} 工厂。
 * <p>
 * 实际 JSON 编解码通过 mica-net-utils 的 JsonUtil 完成，broker 模块不需要引入第三方 JSON 库。
 * </p>
 *
 * @author L.cm
 */
public class JsonPayloadCodecFactory implements PayloadCodecFactory {

	@Override
	public String getName() {
		return JsonPayloadCodec.NAME;
	}

	@Override
	public PayloadCodec create() {
		return new JsonPayloadCodec();
	}
}
