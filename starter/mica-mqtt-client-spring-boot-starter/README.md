# mica-mqtt-client-spring-boot-starter 使用文档

## 一、添加依赖

```xml
<dependency>
    <groupId>net.dreamlu</groupId>
    <artifactId>mica-mqtt-client-spring-boot-starter</artifactId>
    <version>${最新版本}</version>
</dependency>
```

## 二、mqtt 客户端（使用到的场景有限，非必要请不要启用）

### 2.1 配置项
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
| mqtt.client.buffer-allocator | 堆内存 | ByteBuffer Allocator，支持堆内存和堆外内存，默认为：堆内存 |
| mqtt.client.read-buffer-size | 8092 | t-io 每次消息读取长度，超过这个长度的消息会多次读取，默认：8092 |
| mqtt.client.max-bytes-in-message | 8092 | 消息解析最大 bytes 长度，默认：8092 |
| mqtt.client.max-client-id-length | 23 | mqtt 3.1 会校验此参数，其它协议版本不会 |
| mqtt.client.reconnect | true | 自动重连 |
| mqtt.client.re-interval | 5000 | 重连重试时间，单位毫秒 |
| mqtt.client.retry-count | 0 | 连续重连次数，当连续重连这么多次都失败时，不再重连。0和负数则一直重连 |
| mqtt.client.timeout | 5 | 连接超时时间，单位秒，t-io 配置，可为 null |
| mqtt.client.keep-alive-secs | 60 | Keep Alive (s) 心跳维持时间 |
| mqtt.client.version | MQTT_3_1_1 | mqtt 协议，默认：MQTT_3_1_1 |
| mqtt.client.stat-enable | false     | 是否开启监控，默认：关闭 |

### 2.2 配置项示例
```yaml
mqtt:
  client:
    enabled: true               # 是否开启客户端，默认：false
    ip: 127.0.0.1               # 连接的服务端 ip ，默认：127.0.0.1
    port: 3883                  # 端口：默认：1883
    name: Mica-Mqtt-Client      # 名称，默认：Mica-Mqtt-Client
    clientId: 000001            # 客户端Id（非常重要，一般为设备 sn，不可重复）
    user-name: mica             # 认证的用户名
    password: 123456            # 认证的密码
    timeout: 5                  # 连接超时时间，单位：秒，默认：5秒
    reconnect: true             # 是否重连，默认：true
    re-interval: 5000           # 重连时间，默认 5000 毫秒
    version: MQTT_5             # mqtt 协议版本，默认：3.1.1
    read-buffer-size: 8092      # t-io 每次消息读取长度，超过这个长度的消息会多次读取，默认：8092
    max-bytes-in-message: 8092  # 消息解析最大 bytes 长度，默认：8092
    buffer-allocator: heap      # 堆内存和堆外内存，默认：堆内存
    keep-alive-secs: 60         # keep-alive 心跳维持时间，单位：秒
    clean-session: true         # mqtt clean session，默认：true
```

### 2.3 可实现接口（注册成 Spring Bean 即可）

| 接口                           | 是否必须       | 说明                        |
| ---------------------------   | -------------- | ------------------------- |
| IMqttClientConnectListener    | 是             | 客户端连接成功监听            |

### 2.4 自定义 java 配置（可选）

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

### 2.5 订阅示例
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

### 2.6 共享订阅 topic 说明
mica-mqtt client 支持**两种共享订阅**方式：

1. 共享订阅：订阅前缀 `$queue/`，多个客户端订阅了 `$queue/topic`，发布者发布到topic，则只有一个客户端会接收到消息。
2. 分组订阅：订阅前缀 `$share/<group>/`，组客户端订阅了`$queue/group1/topic`、`$queue/group2/topic`..，发布者发布到topic，则消息会发布到每个group中，但是每个group中只有一个客户端会接收到消息。

### 2.7 MqttClientTemplate 使用示例
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
