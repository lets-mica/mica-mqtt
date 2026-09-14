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

package org.dromara.mica.mqtt.broker.rule.sink;

import org.dromara.mica.mqtt.codec.MqttQoS;
import org.dromara.mica.mqtt.core.client.MqttClient;

/**
 * {@link MqttSink} 工厂。
 *
 * @author L.cm
 */
public class MqttSinkFactory implements SinkFactory {

	public static final String TYPE = "mqtt";

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	public Sink create(SinkRef ref) {
		String name = ref.getName();
		MqttClient client = MqttSink.buildClient(ref);
		String template = MqttSink.stringProp(ref, "topicTemplate");
		MqttQoS qos = MqttQoS.QOS0;
		String qosStr = MqttSink.stringProp(ref, "qos");
		if (qosStr != null) {
			try {
				qos = MqttQoS.valueOf(MqttSink.intProp(ref, "qos", 0));
			} catch (Exception ignore) {
				// 默认 QOS0
			}
		}
		boolean retain = MqttSink.booleanProp(ref, "retain", false);
		return new MqttSink(name, client, template, qos, retain);
	}
}
