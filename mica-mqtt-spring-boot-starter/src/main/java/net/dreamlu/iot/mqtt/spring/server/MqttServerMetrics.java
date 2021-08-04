/*
 * Copyright (c) 2019-2029, Dreamlu 卢春梦 (596392912@qq.com & www.dreamlu.net).
 * <p>
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE 3.0;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl.html
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.dreamlu.iot.mqtt.spring.server;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;
import org.tio.server.ServerTioConfig;

import java.util.Collections;

/**
 * mica mqtt Metrics
 *
 * @author L.cm
 */
@RequiredArgsConstructor
public class MqttServerMetrics implements MeterBinder {
	/**
	 * Prefix used for all mica-mqtt metric names.
	 */
	public static final String MQTT_METRIC_NAME_PREFIX = "mqtt";
	/**
	 * groupStat
	 */
	private static final String MQTT_GROUP_STAT_HANDLED_PACKETS = MQTT_METRIC_NAME_PREFIX + ".group.handled.packets";
	private static final String MQTT_GROUP_STAT_HANDLED_BYTES = MQTT_METRIC_NAME_PREFIX + ".group.handled.bytes";

	private final ServerTioConfig serverTioConfig;
	private final Iterable<Tag> tags;

	public MqttServerMetrics(ServerTioConfig serverTioConfig) {
		this.serverTioConfig = serverTioConfig;
		this.tags = Collections.emptyList();
	}

	@Override
	public void bindTo(MeterRegistry meterRegistry) {
		Gauge.builder(MQTT_GROUP_STAT_HANDLED_PACKETS, serverTioConfig, (serverLauncher) -> serverTioConfig.getGroupStat().getHandledPackets().get())
			.description("Mqtt handled packets")
			.tags(tags)
			.register(meterRegistry);
		Gauge.builder(MQTT_GROUP_STAT_HANDLED_BYTES, serverTioConfig, (serverLauncher) -> serverTioConfig.getGroupStat().getHandledBytes().get())
			.description("Mqtt handled bytes")
			.tags(tags)
			.register(meterRegistry);
	}

}
