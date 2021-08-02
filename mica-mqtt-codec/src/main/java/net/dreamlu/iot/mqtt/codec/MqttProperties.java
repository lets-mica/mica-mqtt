/*
 * Copyright 2020 The Netty Project
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

package net.dreamlu.iot.mqtt.codec;

import java.util.*;

/**
 * MQTT Properties container
 *
 * @author netty
 */
public final class MqttProperties {

	public enum MqttPropertyType {
		// single byte properties
		/**
		 * 有效载荷标识（Payload Format Indicator），该属性只存在于 PUBLISH 报文和 CONNECT 报文的遗嘱属性中。
		 */
		PAYLOAD_FORMAT_INDICATOR(0x01),
		/**
		 * 请求问题信息
		 */
		REQUEST_PROBLEM_INFORMATION(0x17),
		/**
		 * 请求响应信息
		 */
		REQUEST_RESPONSE_INFORMATION(0x19),
		/**
		 * 服务器支持得最高 qos 级别
		 */
		MAXIMUM_QOS(0x24),
		/**
		 * 保留消息可用
		 */
		RETAIN_AVAILABLE(0x25),
		/**
		 * 订阅通配符可用
		 */
		WILDCARD_SUBSCRIPTION_AVAILABLE(0x28),
		/**
		 * 订阅标识符可用
		 */
		SUBSCRIPTION_IDENTIFIER_AVAILABLE(0x29),
		/**
		 * $share 共享订阅可用
		 */
		SHARED_SUBSCRIPTION_AVAILABLE(0x2A),

		// two bytes properties
		/**
		 * 服务器 keep alive
		 */
		SERVER_KEEP_ALIVE(0x13),
		/**
		 * 告知对方自己希望处理未决的最大的 Qos1 或者 Qos2 PUBLISH消息个数，如果不存在，则默认是65535。作用：流控。
		 */
		RECEIVE_MAXIMUM(0x21),
		/**
		 * topic 别名最大值
		 */
		TOPIC_ALIAS_MAXIMUM(0x22),
		/**
		 * topic 别名
		 */
		TOPIC_ALIAS(0x23),

		// four bytes properties
		PUBLICATION_EXPIRY_INTERVAL(0x02),
		/**
		 * session 超时时间，连接时使用
		 */
		SESSION_EXPIRY_INTERVAL(0x11),
		/**
		 * 遗嘱消息延迟时间
		 */
		WILL_DELAY_INTERVAL(0x18),
		/**
		 * 最大包体大小
		 */
		MAXIMUM_PACKET_SIZE(0x27),

		// Variable Byte Integer
		/**
		 * 订阅标识符
		 */
		SUBSCRIPTION_IDENTIFIER(0x0B),

		// UTF-8 Encoded String properties
		/**
		 * 内容类型（Content Type），只存在于 PUBLISH 报文和 CONNECT 报文的遗嘱属性中。
		 * 例如：存放 MIME 类型，比如 text/plain 表示文本文件，audio/aac 表示音频文件。
		 */
		CONTENT_TYPE(0x03),
		/**
		 * 响应的 topic
		 */
		RESPONSE_TOPIC(0x08),
		/**
		 * 指定的客户标识符
		 */
		ASSIGNED_CLIENT_IDENTIFIER(0x12),
		/**
		 * 身份验证方法
		 */
		AUTHENTICATION_METHOD(0x15),
		/**
		 * 响应信息
		 */
		RESPONSE_INFORMATION(0x1A),
		/**
		 * 服务器参考
		 */
		SERVER_REFERENCE(0x1C),
		/**
		 * 所有的ACK以及DISCONNECT 都可以携带 Reason String属性告知对方一些特殊的信息，
		 * 一般来说是ACK失败的情况下会使用该属性告知对端为什么失败，可用来弥补Reason Code信息不够。
		 */
		REASON_STRING(0x1F),
		/**
		 * 用户属性
		 */
		USER_PROPERTY(0x26),

		// Binary Data
		/**
		 * 相关数据
		 */
		CORRELATION_DATA(0x09),
		/**
		 * 认证数据
		 */
		AUTHENTICATION_DATA(0x16);

		private static final MqttPropertyType[] VALUES;

		static {
			VALUES = new MqttPropertyType[43];
			for (MqttPropertyType v : values()) {
				VALUES[v.value] = v;
			}
		}

		private final int value;

		MqttPropertyType(int value) {
			this.value = value;
		}

		public int value() {
			return value;
		}

		public static MqttPropertyType valueOf(int type) {
			MqttPropertyType t = null;
			try {
				t = VALUES[type];
			} catch (ArrayIndexOutOfBoundsException ignored) {
				// nop
			}
			if (t == null) {
				throw new IllegalArgumentException("unknown property type: " + type);
			}
			return t;
		}
	}

	public static final MqttProperties NO_PROPERTIES = new MqttProperties(false);

	static MqttProperties withEmptyDefaults(MqttProperties properties) {
		if (properties == null) {
			return MqttProperties.NO_PROPERTIES;
		}
		return properties;
	}

	/**
	 * MQTT property base class
	 *
	 * @param <T> property type
	 */
	public abstract static class MqttProperty<T> {
		final T value;
		final int propertyId;

		protected MqttProperty(int propertyId, T value) {
			this.propertyId = propertyId;
			this.value = value;
		}

		/**
		 * Get MQTT property value
		 *
		 * @return property value
		 */
		public T value() {
			return value;
		}

		/**
		 * Get MQTT property ID
		 *
		 * @return property ID
		 */
		public int propertyId() {
			return propertyId;
		}

		@Override
		public int hashCode() {
			return propertyId + 31 * value.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			MqttProperty that = (MqttProperty) obj;
			return this.propertyId == that.propertyId && this.value.equals(that.value);
		}
	}

	public static final class IntegerProperty extends MqttProperty<Integer> {

		public IntegerProperty(int propertyId, Integer value) {
			super(propertyId, value);
		}

		@Override
		public String toString() {
			return "IntegerProperty(" + propertyId + ", " + value + ")";
		}
	}

	public static final class StringProperty extends MqttProperty<String> {

		public StringProperty(int propertyId, String value) {
			super(propertyId, value);
		}

		@Override
		public String toString() {
			return "StringProperty(" + propertyId + ", " + value + ")";
		}
	}

	public static final class StringPair {
		public final String key;
		public final String value;

		public StringPair(String key, String value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public int hashCode() {
			return key.hashCode() + 31 * value.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			StringPair that = (StringPair) obj;

			return that.key.equals(this.key) && that.value.equals(this.value);
		}
	}

	//User properties are the only properties that may be included multiple times and
	//are the only properties where ordering is required. Therefore, they need a special handling
	public static final class UserProperties extends MqttProperty<List<StringPair>> {
		public UserProperties() {
			super(MqttPropertyType.USER_PROPERTY.value, new ArrayList<>());
		}

		/**
		 * Create user properties from the collection of the String pair values
		 *
		 * @param values string pairs. Collection entries are copied, collection itself isn't shared
		 */
		public UserProperties(Collection<StringPair> values) {
			this();
			this.value.addAll(values);
		}

		private static UserProperties fromUserPropertyCollection(Collection<UserProperty> properties) {
			UserProperties userProperties = new UserProperties();
			for (UserProperty property : properties) {
				userProperties.add(new StringPair(property.value.key, property.value.value));
			}
			return userProperties;
		}

		public void add(StringPair pair) {
			this.value.add(pair);
		}

		public void add(String key, String value) {
			this.value.add(new StringPair(key, value));
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("UserProperties(");
			boolean first = true;
			for (StringPair pair : value) {
				if (!first) {
					builder.append(", ");
				}
				builder.append(pair.key).append("->").append(pair.value);
				first = false;
			}
			builder.append(")");
			return builder.toString();
		}
	}

	public static final class UserProperty extends MqttProperty<StringPair> {
		public UserProperty(String key, String value) {
			super(MqttPropertyType.USER_PROPERTY.value, new StringPair(key, value));
		}

		@Override
		public String toString() {
			return "UserProperty(" + value.key + ", " + value.value + ")";
		}
	}

	public static final class BinaryProperty extends MqttProperty<byte[]> {

		public BinaryProperty(int propertyId, byte[] value) {
			super(propertyId, value);
		}

		@Override
		public String toString() {
			return "BinaryProperty(" + propertyId + ", " + value.length + " bytes)";
		}
	}

	public MqttProperties() {
		this(true);
	}

	private MqttProperties(boolean canModify) {
		this.canModify = canModify;
	}

	private Map<Integer, MqttProperty> props;
	private List<UserProperty> userProperties;
	private List<IntegerProperty> subscriptionIds;
	private final boolean canModify;

	public void add(MqttProperty property) {
		if (!canModify) {
			throw new UnsupportedOperationException("adding property isn't allowed");
		}
		Map<Integer, MqttProperty> props = this.props;
		if (property.propertyId == MqttPropertyType.USER_PROPERTY.value) {
			List<UserProperty> userProperties = this.userProperties;
			if (userProperties == null) {
				userProperties = new ArrayList<>(1);
				this.userProperties = userProperties;
			}
			if (property instanceof UserProperty) {
				userProperties.add((UserProperty) property);
			} else if (property instanceof UserProperties) {
				for (StringPair pair : ((UserProperties) property).value) {
					userProperties.add(new UserProperty(pair.key, pair.value));
				}
			} else {
				throw new IllegalArgumentException("User property must be of UserProperty or UserProperties type");
			}
		} else if (property.propertyId == MqttPropertyType.SUBSCRIPTION_IDENTIFIER.value) {
			List<IntegerProperty> subscriptionIds = this.subscriptionIds;
			if (subscriptionIds == null) {
				subscriptionIds = new ArrayList<>(1);
				this.subscriptionIds = subscriptionIds;
			}
			if (property instanceof IntegerProperty) {
				subscriptionIds.add((IntegerProperty) property);
			} else {
				throw new IllegalArgumentException("Subscription ID must be an integer property");
			}
		} else {
			if (props == null) {
				props = new HashMap<>();
				this.props = props;
			}
			props.put(property.propertyId, property);
		}
	}

	public Collection<? extends MqttProperty> listAll() {
		Map<Integer, MqttProperty> props = this.props;
		if (props == null && subscriptionIds == null && userProperties == null) {
			return Collections.emptyList();
		}
		if (subscriptionIds == null && userProperties == null) {
			return props.values();
		}
		if (props == null && userProperties == null) {
			return subscriptionIds;
		}
		List<MqttProperty> propValues = new ArrayList<>(props != null ? props.size() : 1);
		if (props != null) {
			propValues.addAll(props.values());
		}
		if (subscriptionIds != null) {
			propValues.addAll(subscriptionIds);
		}
		if (userProperties != null) {
			propValues.add(UserProperties.fromUserPropertyCollection(userProperties));
		}
		return propValues;
	}

	public boolean isEmpty() {
		Map<Integer, MqttProperty> props = this.props;
		return props == null || props.isEmpty();
	}

	/**
	 * Get property by ID. If there are multiple properties of this type (can be with Subscription ID)
	 * then return the first one.
	 *
	 * @param propertyId ID of the property
	 * @return a property if it is set, null otherwise
	 */
	public MqttProperty getProperty(int propertyId) {
		if (MqttPropertyType.USER_PROPERTY.value == propertyId) {
			//special handling to keep compatibility with earlier versions
			List<UserProperty> userProperties = this.userProperties;
			if (userProperties == null) {
				return null;
			}
			return UserProperties.fromUserPropertyCollection(userProperties);
		}
		if (MqttPropertyType.SUBSCRIPTION_IDENTIFIER.value == propertyId) {
			List<IntegerProperty> subscriptionIds = this.subscriptionIds;
			if (subscriptionIds == null || subscriptionIds.isEmpty()) {
				return null;
			}
			return subscriptionIds.get(0);
		}
		Map<Integer, MqttProperty> props = this.props;
		return props == null ? null : props.get(propertyId);
	}

	/**
	 * Get property by ID. If there are multiple properties of this type (can be with Subscription ID)
	 * then return the first one.
	 *
	 * @param mqttPropertyType Type of the property
	 * @return a property if it is set, null otherwise
	 */
	public MqttProperty getProperty(MqttPropertyType mqttPropertyType) {
		return getProperty(mqttPropertyType.value);
	}

	/**
	 * Get property by ID. If there are multiple properties of this type (can be with Subscription ID)
	 * then return the first one.
	 *
	 * @param mqttPropertyType Type of the property
	 * @return a property if it is set, null otherwise
	 */
	public <T> T getPropertyValue(MqttPropertyType mqttPropertyType) {
		MqttProperty property = getProperty(mqttPropertyType.value);
		if (property == null) {
			return null;
		}
		return (T) property.value();
	}

	/**
	 * Get properties by ID.
	 * Some properties (Subscription ID and User Properties) may occur multiple times,
	 * this method returns all their values in order.
	 *
	 * @param propertyId ID of the property
	 * @return all properties having specified ID
	 */
	public List<? extends MqttProperty> getProperties(int propertyId) {
		if (propertyId == MqttPropertyType.USER_PROPERTY.value) {
			return userProperties == null ? Collections.emptyList() : userProperties;
		}
		if (propertyId == MqttPropertyType.SUBSCRIPTION_IDENTIFIER.value) {
			return subscriptionIds == null ? Collections.emptyList() : subscriptionIds;
		}
		Map<Integer, MqttProperty> props = this.props;
		return (props == null || !props.containsKey(propertyId)) ?
			Collections.emptyList() :
			Collections.singletonList(props.get(propertyId));
	}
}
