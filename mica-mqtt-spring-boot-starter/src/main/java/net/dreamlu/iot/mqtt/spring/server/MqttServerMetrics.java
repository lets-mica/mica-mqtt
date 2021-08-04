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
import org.tio.core.Tio;
import org.tio.server.ServerGroupStat;
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
	 * 连接统计
	 */
	private static final String MQTT_CONNECTIONS_ACCEPTED 		= MQTT_METRIC_NAME_PREFIX + ".connections.accepted";
	private static final String MQTT_CONNECTIONS_SIZE 			= MQTT_METRIC_NAME_PREFIX + ".connections.size";
	private static final String MQTT_CONNECTIONS_CLOSED 		= MQTT_METRIC_NAME_PREFIX + ".connections.closed";
	/**
	 * 消息统计
	 */
	private static final String MQTT_MESSAGES_HANDLED_PACKETS 	= MQTT_METRIC_NAME_PREFIX + ".messages.handled.packets";
	private static final String MQTT_MESSAGES_HANDLED_BYTES 	= MQTT_METRIC_NAME_PREFIX + ".messages.handled.bytes";
	private static final String MQTT_MESSAGES_RECEIVED_PACKETS 	= MQTT_METRIC_NAME_PREFIX + ".messages.received.packets";
	private static final String MQTT_MESSAGES_RECEIVED_BYTES 	= MQTT_METRIC_NAME_PREFIX + ".messages.received.bytes";
	private static final String MQTT_MESSAGES_SEND_PACKETS 		= MQTT_METRIC_NAME_PREFIX + ".messages.send.packets";
	private static final String MQTT_MESSAGES_SEND_BYTES 		= MQTT_METRIC_NAME_PREFIX + ".messages.send.bytes";

	private final ServerTioConfig serverTioConfig;
	private final Iterable<Tag> tags;

	public MqttServerMetrics(ServerTioConfig serverTioConfig) {
		this.serverTioConfig = serverTioConfig;
		this.tags = Collections.emptyList();
	}

	@Override
	public void bindTo(MeterRegistry meterRegistry) {
		// 连接统计
		Gauge.builder(MQTT_CONNECTIONS_ACCEPTED, serverTioConfig, (config) -> ((ServerGroupStat) config.getGroupStat()).getAccepted().get())
			.description("Mqtt server connections accepted")
			.tags(tags)
			.register(meterRegistry);
		Gauge.builder(MQTT_CONNECTIONS_SIZE, serverTioConfig, (config) -> Tio.getAll(config).size())
			.description("Mqtt server connections size")
			.tags(tags)
			.register(meterRegistry);
		Gauge.builder(MQTT_CONNECTIONS_CLOSED, serverTioConfig, (config) -> config.getGroupStat().getClosed().get())
			.description("Mqtt server connections closed")
			.tags(tags)
			.register(meterRegistry);
		// 消息统计
		Gauge.builder(MQTT_MESSAGES_HANDLED_PACKETS, serverTioConfig, (config) -> config.getGroupStat().getHandledPackets().get())
			.description("Mqtt server handled packets")
			.tags(tags)
			.register(meterRegistry);
		Gauge.builder(MQTT_MESSAGES_HANDLED_BYTES, serverTioConfig, (config) -> config.getGroupStat().getHandledBytes().get())
			.description("Mqtt server handled bytes")
			.tags(tags)
			.register(meterRegistry);
		// 接收的消息
		Gauge.builder(MQTT_MESSAGES_RECEIVED_PACKETS, serverTioConfig, (config) -> config.getGroupStat().getReceivedPackets().get())
			.description("Mqtt server received packets")
			.tags(tags)
			.register(meterRegistry);
		Gauge.builder(MQTT_MESSAGES_RECEIVED_BYTES, serverTioConfig, (config) -> config.getGroupStat().getReceivedBytes().get())
			.description("Mqtt server received bytes")
			.tags(tags)
			.register(meterRegistry);
		// 发送的消息
		Gauge.builder(MQTT_MESSAGES_SEND_PACKETS, serverTioConfig, (config) -> config.getGroupStat().getSentPackets().get())
			.description("Mqtt server send packets")
			.tags(tags)
			.register(meterRegistry);
		Gauge.builder(MQTT_MESSAGES_SEND_BYTES, serverTioConfig, (config) -> config.getGroupStat().getSentPackets().get())
			.description("Mqtt server send bytes")
			.tags(tags)
			.register(meterRegistry);
	}

}
