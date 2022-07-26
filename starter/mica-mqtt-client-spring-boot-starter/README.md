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

### 2.1 配置项示例
```yaml
mqtt:
  client:
    enabled: true               # 是否开启客户端，默认：false 使用到的场景有限，非必要请不要启用
    ip: 127.0.0.1               # 连接的服务端 ip ，默认：127.0.0.1
    port: 1883                  # 端口：默认：1883
    name: Mica-Mqtt-Client      # 名称，默认：Mica-Mqtt-Client
    clientId: 000001            # 客户端Id（非常重要，一般为设备 sn，不可重复）
    user-name: mica             # 认证的用户名
    password: 123456            # 认证的密码
    timeout: 5                  # 超时时间，单位：秒，默认：5秒
    reconnect: true             # 是否重连，默认：true
    re-interval: 5000           # 重连时间，默认 5000 毫秒
    version: MQTT_5             # mqtt 协议版本，默认：3.1.1
    read-buffer-size: 8KB       # 接收数据的 buffer size，默认：8k
    max-bytes-in-message: 10MB  # 消息解析最大 bytes 长度，默认：10M
    buffer-allocator: heap      # 堆内存和堆外内存，默认：堆内存
    keep-alive-secs: 60         # keep-alive 时间，单位：秒
    clean-session: true         # mqtt clean session，默认：true
    use-ssl: false              # 是否启用 ssl，默认：false
```

### 2.2 可实现接口（注册成 Spring Bean 即可）

| 接口                           | 是否必须 | 说明                        |
| ---------------------------   |------| ------------------------- |
| IMqttClientConnectListener    | 否    | 客户端连接成功监听            |

### 2.3 客户端上下线监听
使用 Spring event 解耦客户端上下线监听，注意： `1.3.4` 开始支持。会跟自定义的 `IMqttClientConnectListener` 实现冲突，取一即可。

```java
/**
 * 示例：客户端连接状态监听
 *
 * @author L.cm
 */
@Service
public class MqttClientConnectListener {
    private static final Logger logger = LoggerFactory.getLogger(MqttClientConnectListener.class);

    @Autowired
    private MqttClientCreator mqttClientCreator;

    @EventListener
    public void onConnected(MqttConnectedEvent event) {
        logger.info("MqttConnectedEvent:{}", event);
    }

    @EventListener
    public void onDisconnect(MqttDisconnectEvent event) {
        // 离线时更新重连时的密码，适用于类似阿里云 mqtt clientId 连接带时间戳的方式 
        logger.info("MqttDisconnectEvent:{}", event);
        // 在断线时更新 clientId、username、password
        mqttClientCreator.clientId("newClient" + System.currentTimeMillis())
            .username("newUserName")
            .password("newPassword");
    }

}
```

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
2. 分组订阅：订阅前缀 `$share/<group>/`，组客户端订阅了`$share/group1/topic`、`$share/group2/topic`..，发布者发布到topic，则消息会发布到每个group中，但是每个group中只有一个客户端会接收到消息。

### 2.8 MqttClientTemplate 使用示例
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
