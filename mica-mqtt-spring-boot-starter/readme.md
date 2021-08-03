# mica-mqtt-spring-boot-starter 使用文档

## 添加依赖
```xml
<dependency>
    <groupId>net.dreamlu</groupId>
    <artifactId>mica-mqtt-spring-boot-starter</artifactId>
    <version>${最新版本}</version>
</dependency>
```

## 配置项
| 配置项 | 默认值 | 说明 |
| ----- | ------ | ------ |
| mqtt.server.name | Mica-Mqtt-Server | 名称 |
| mqtt.server.port | 1883 | 端口 |
| mqtt.server.ip | 127.0.0.1 | 服务端 ip |
| mqtt.server.buffer-allocator | 堆内存 | 堆内存和堆外内存 |
| mqtt.server.heartbeat-timeout | 120s | 心跳超时时间(单位: 毫秒 默认: 1000 * 120)，如果用户不希望框架层面做心跳相关工作，请把此值设为0或负数 |
| mqtt.server.read-buffer-size | 8092 | 接收数据的 buffer size，默认：8092 |
| mqtt.server.max-bytes-in-message | 8092 | 消息解析最大 bytes 长度，默认：8092 |
| mqtt.server.debug | false | debug |


## 配置项示例
```yaml
spring:

mqtt:
    server:
        enable: true
        name: Mica-Mqtt-Server
        ip: 127.0.0.1
        port: 5883
        buffer-allocator: HEAP
        heartbeat-timeout: 120000
        read-buffer-size: 8092
        max-bytes-in-message: 8092
        debug: true
    client:
        enable: true
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

## 可实现接口（注册成 Spring Bean 即可）

| 接口                        | 是否必须       | 说明                 |
| --------------------------- | -------------- | ------------------ |
| IMqttServerAuthHandler      | 是             | 用于客户端认证       |
| IMqttMessageListener        | 是             | 消息监听            |
| IMqttConnectStatusListener  | 是             | 连接状态监听         |
| IMqttSessionManager         | 否             | session 管理        |
| IMqttMessageStore           | 集群是，单机否   | 遗嘱和保留消息存储    |
| IMqttMessageDispatcher      | 集群是，单机否   | 消息转发             |
| IpStatListener              | 否             | t-io ip 转态监听     |

## IMqttMessageListener (用于监听客户端上传的消息) 使用示例
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


## 自定义配置（可选）
```java
@Configuration(proxyBeanMethods = false)
public class MqttServerCustomizerConfiguration {

	@Bean
	public MqttServerCustomizer activeRecordPluginCustomizer() {
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

## MqttClientTemplate 使用示例
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

        /*client.subQos0("/#", (topic, payload) -> {
            logger.info(topic + '\t' + ByteBufferUtil.toString(payload));
        });*/

        return true;
    }

}
```

## MqttServerTemplate 使用示例
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