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

package net.dreamlu.iot.mqtt.spring.server.config;

import io.micrometer.core.instrument.binder.MeterBinder;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;


/**
 * mica mqtt Metrics 配置
 *
 * @author L.cm
 */
@AutoConfiguration
@ConditionalOnProperty(
	prefix = MqttServerProperties.PREFIX,
	name = "enabled",
	havingValue = "true"
)
@ConditionalOnClass(MeterBinder.class)
@AutoConfigureAfter(MqttServerConfiguration.class)
public class MqttServerMetricsConfiguration {

	@Bean
	public MqttServerMetrics micaMqttMetrics() {
		return new MqttServerMetrics();
	}

}
