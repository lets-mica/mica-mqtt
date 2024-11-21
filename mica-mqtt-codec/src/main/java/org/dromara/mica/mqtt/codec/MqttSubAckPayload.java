/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.dromara.mica.mqtt.codec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Payload of the {@link MqttSubAckMessage}
 *
 * @author netty
 */
public class MqttSubAckPayload {
	private final List<Integer> reasonCodes;

	public MqttSubAckPayload(int... reasonCodes) {
		Objects.requireNonNull(reasonCodes, "reasonCodes is null.");
		List<Integer> list = new ArrayList<>(reasonCodes.length);
		for (int v : reasonCodes) {
			list.add(v);
		}
		this.reasonCodes = Collections.unmodifiableList(list);
	}

	public MqttSubAckPayload(Iterable<Integer> reasonCodes) {
		Objects.requireNonNull(reasonCodes, "reasonCodes is null.");
		List<Integer> list = new ArrayList<>();
		for (Integer v : reasonCodes) {
			if (v == null) {
				break;
			}
			list.add(v);
		}
		this.reasonCodes = Collections.unmodifiableList(list);
	}

	public List<Integer> grantedQoSLevels() {
		List<Integer> qosLevels = new ArrayList<>(reasonCodes.size());
		for (int code : reasonCodes) {
			if (code > MqttQoS.QOS2.value()) {
				qosLevels.add(MqttQoS.FAILURE.value());
			} else {
				qosLevels.add(code);
			}
		}
		return qosLevels;
	}

	public List<Integer> reasonCodes() {
		return reasonCodes;
	}

	@Override
	public String toString() {
		return "MqttSubAckPayload[" +
			"reasonCodes=" + reasonCodes +
			']';
	}
}
