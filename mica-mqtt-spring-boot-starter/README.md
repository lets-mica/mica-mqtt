# mica-mqtt-spring-boot-starter 使用文档

## 添加依赖

```xml
<dependency>
    <groupId>net.dreamlu</groupId>
    <artifactId>mica-mqtt-spring-boot-starter</artifactId>
    <version>${最新版本}</version>
</dependency>
```

## mqtt 服务

### 配置项

| 配置项 | 默认值 | 说明 |
| ----- | ------ | ------ |
| mqtt.server.name | Mica-Mqtt-Server | 名称 |
| mqtt.server.port | 1883 | 端口 |
| mqtt.server.ip | 127.0.0.1 | 服务端 ip |
| mqtt.server.buffer-allocator | 堆内存 | 堆内存和堆外内存 |
| mqtt.server.heartbeat-timeout | 120s | 心跳超时时间(单位: 毫秒 默认: 1000 * 120)，如果用户不希望框架层面做心跳相关工作，请把此值设为0或负数 |
| mqtt.server.read-buffer-size | 8092 | 接收数据的 buffer size，默认：8092 |
| mqtt.server.max-bytes-in-message | 8092 | 消息解析最大 bytes 长度，默认：8092 |
| mqtt.server.debug | false | debug，如果开启 prometheus 指标收集建议关闭 |

### 配置项示例

```yaml
mqtt:
  server:
    enabled: true
    name: Mica-Mqtt-Server
    ip: 127.0.0.1
    port: 5883
    buffer-allocator: HEAP
    heartbeat-timeout: 120000
    read-buffer-size: 8092
    max-bytes-in-message: 8092
    debug: true
```

### 可实现接口（注册成 Spring Bean 即可）

| 接口                        | 是否必须       | 说明                 |
| --------------------------- | -------------- | ------------------ |
| IMqttServerAuthHandler      | 是             | 用于客户端认证       |
| IMqttMessageListener        | 是             | 消息监听            |
| IMqttConnectStatusListener  | 是             | 连接状态监听         |
| IMqttSessionManager         | 否             | session 管理        |
| IMqttMessageStore           | 集群是，单机否   | 遗嘱和保留消息存储    |
| IMqttMessageDispatcher      | 集群是，单机否   | 消息转发             |
| IpStatListener              | 否             | t-io ip 状态监听     |

### IMqttMessageListener (用于监听客户端上传的消息) 使用示例

```java
@Service
public class MqttServerMessageListener implements IMqttMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MqttServerMessageListener.class);

    @Override
    public void onMessage(String clientId, String topic, MqttQoS mqttQoS, ByteBuffer byteBuffer) {
        logger.info("clientId:{} topic:{} mqttQoS:{} message:{}", clientId, topic, mqttQoS, ByteBufferUtil.toString(byteBuffer));
    }
}
```

### 自定义配置（可选）

```java
@Configuration(proxyBeanMethods = false)
public class MqttServerCustomizerConfiguration {

	@Bean
	public MqttServerCustomizer mqttServerCustomizer() {
		return new MqttServerCustomizer() {
			@Override
			public void customize(MqttServerCreator creator) {
				// 此处可自定义配置 creator，会覆盖 yml 中的配置
				System.out.println("----------------MqttServerCustomizer-----------------");
			}
		};
	}

}
```

### MqttServerTemplate 使用示例

```java
import net.dreamlu.iot.mqtt.codec.MqttQoS;
import net.dreamlu.iot.mqtt.spring.server.MqttServerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

/**
 * @author wsq
 */
@Service
public class ServerService {
    private static final Logger logger = LoggerFactory.getLogger(ServerService.class);
    @Autowired
    private MqttServerTemplate server;

    public boolean publish(String body) {
        server.publishAll("/test/123", ByteBuffer.wrap(body.getBytes()), MqttQoS.EXACTLY_ONCE);
        return true;
    }
}
```

### Prometheus + Grafana 监控对接
```xml
<!-- 开启 prometheus 指标收集 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>io.micrometer</groupId>
    <artifactId>micrometer-registry-prometheus</artifactId>
</dependency>
```

| 支持得指标                     | 说明             |
| ------------------------------ | ---------------- |
| mqtt_connections_accepted      | 共接受过连接数   |
| mqtt_connections_closed        | 关闭过的连接数   |
| mqtt_connections_size          | 当前连接数       |
| mqtt_messages_handled_packets  | 已处理消息数     |
| mqtt_messages_handled_bytes    | 已处理消息字节数  |
| mqtt_messages_received_packets | 已接收消息数      |
| mqtt_messages_received_bytes   | 已处理消息字节数 |
| mqtt_messages_send_packets     | 已发送消息数      |
| mqtt_messages_send_bytes       | 已发送消息字节数  |

## mqtt 客户端

### 配置项
| 配置项 | 默认值 | 说明 |
| ----- | ------ | ------ |
| mqtt.client.enabled | false | 是否启用，默认：false |
| mqtt.client.ip | 127.0.0.1 | 服务端 ip，默认：127.0.0.1 |
| mqtt.client.port | 1883 | 端口，默认：1883 |
| mqtt.client.name | Mica-Mqtt-Client | 名称，默认：Mica-Mqtt-Client |
| mqtt.client.user-name |  | 用户名 |
| mqtt.client.password |  | 密码 |
| mqtt.client.client-id |  | 客户端ID，非常重要， 默认为：MICA-MQTT- 前缀和 36进制的纳秒数 |
| mqtt.client.clean-session | true | 清除会话 <p> false 表示如果订阅的客户机断线了，那么要保存其要推送的消息，如果其重新连接时，则将这些消息推送。 true 表示消除，表示客户机是第一次连接，消息所以以前的连接信息。 </p> |
| mqtt.client.buffer-allocator |  | ByteBuffer Allocator，支持堆内存和堆外内存，默认为：堆内存 |
| mqtt.client.read-buffer-size |  | t-io 每次消息读取长度，跟 maxBytesInMessage 相关 |
| mqtt.client.reconnect | true | 自动重连 |
| mqtt.client.re-interval |  | 重连重试时间 |
| mqtt.client.timeout |  | 超时时间，t-io 配置，可为 null |
| mqtt.client.keep-alive-secs | 60 | Keep Alive (s) |
| mqtt.client.version |  | mqtt 协议，默认：3_1_1 |

### 配置项示例
```yaml
mqtt:
  client:
    enabled: true
    name: Mica-Mqtt-Client
    ip: 127.0.0.1
    port: 3883
    user-name: mica
    password: 123456
    timeout: 120000
    readBufferSize: 8092
    keepAliveSecs: 60
    reconnect: true
    reInterval: 120000
    clientId: 000001
    cleanSession: true
    version: MQTT_5
    bufferAllocator: HEAP
```

### 自定义 java 配置（可选）

```java
@Configuration(proxyBeanMethods = false)
public class MqttClientCustomizerConfiguration {

	@Bean
	public MqttClientCustomizer mqttClientCustomizer() {
		return new MqttClientCustomizer() {
			@Override
			public void customize(MqttClientCreator creator) {
				// 此处可自定义配置 creator，会覆盖 yml 中的配置
				System.out.println("----------------MqttServerCustomizer-----------------");
			}
		};
	}

}
```

### 订阅示例
```java
@Service
public class MqttClientSubscribeListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttClientSubscribeListener.class);

	@MqttClientSubscribe("/test/#")
	public void subQos0(String topic, ByteBuffer payload) {
		logger.info("topic:{} payload:{}", topic, ByteBufferUtil.toString(payload));
	}

	@MqttClientSubscribe(value = "/qos1/#", qos = MqttQoS.AT_LEAST_ONCE)
	public void subQos1(String topic, ByteBuffer payload) {
		logger.info("topic:{} payload:{}", topic, ByteBufferUtil.toString(payload));
	}

}
```

### MqttClientTemplate 使用示例
```java
import net.dreamlu.iot.mqtt.codec.ByteBufferUtil;
import net.dreamlu.iot.mqtt.spring.client.MqttClientTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author wsq
 */
@Service
public class MainService {
    private static final Logger logger = LoggerFactory.getLogger(MainService.class);
    @Autowired
    private MqttClientTemplate client;

    public boolean publish() {
        client.publish("/test/client", ByteBuffer.wrap("mica最牛皮".getBytes(StandardCharsets.UTF_8)));
        return true;
    }

    public boolean sub() {
        client.subQos0("/test/#", (topic, payload) -> {
            logger.info(topic + '\t' + ByteBufferUtil.toString(payload));
        });
        return true;
    }

}
```
