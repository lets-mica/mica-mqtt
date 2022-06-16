# mica-mqtt-server-spring-boot-starter 使用文档

## 一、添加依赖

```xml
<dependency>
    <groupId>net.dreamlu</groupId>
    <artifactId>mica-mqtt-server-spring-boot-starter</artifactId>
    <version>${最新版本}</version>
</dependency>
```

## 二、mqtt 服务

### 2.1 配置项

| 配置项 | 默认值              | 说明                                                                   |
| ----- |------------------|----------------------------------------------------------------------|
| mqtt.server.enabled | true             | 是否启用，默认：true                                                         |
| mqtt.server.name | Mica-Mqtt-Server | 名称                                                                   |
| mqtt.server.port | 1883             | 端口                                                                   |
| mqtt.server.ip | 0.0.0.0          | 服务端 ip 默认为空，0.0.0.0，建议不要设置                                           |
| mqtt.server.buffer-allocator | 堆内存              | 堆内存和堆外内存                                                             |
| mqtt.server.heartbeat-timeout | 1000 * 120       | 心跳超时时间(单位: 毫秒 默认: 1000 * 120)                                        |
| mqtt.server.keepalive-backoff | 0.75F            | MQTT 客户端 keepalive 系数，连接超时缺省为连接设置的 keepalive * keepaliveBackoff * 2，不得小于 0.5 |
| mqtt.server.read-buffer-size | 132476             | 一次读取接收数据的 buffer size，超过这个长度的消息会多次读取，默认：132476                         |
| mqtt.server.max-bytes-in-message | 10M              | 消息解析最大 bytes 长度，默认：10M                                              |
| mqtt.server.max-client-id-length | 23               | mqtt 3.1 会校验此参数，其它协议版本不会                                             |
| mqtt.server.debug | false            | debug，如果开启 prometheus 指标收集建议关闭                                       |
| mqtt.server.web-port | 8083             | http、websocket 端口，默认：8083                                            |
| mqtt.server.websocket-enable | true             | 开启 websocket 服务，默认：true                                              |
| mqtt.server.http-enable | false            | 开启 http 服务，默认：true                                                   |
| mqtt.server.http-basic-auth.enable | false            | 是否启用，默认：关闭                                                           |
| mqtt.server.http-basic-auth.password |                  | http Basic 认证密码                                                      |
| mqtt.server.http-basic-auth.username |                  | http Basic 认证账号                                                      |
| mqtt.server.node-name | pid@ip:port      | 集群节点名                                                                |
| mqtt.server.stat-enable | false            | 是否开启监控，默认：关闭，注意如果开启 Prometheus 监控，需要设置为 true                         |

### 2.2 配置项示例

```yaml
mqtt:
  server:
    enabled: true               # 是否开启服务端，默认：true
#    ip: 0.0.0.0                 # 服务端 ip 默认为空，0.0.0.0，建议不要设置
    port: 5883                  # 端口，默认：1883
    name: Mica-Mqtt-Server      # 名称，默认：Mica-Mqtt-Server
    buffer-allocator: HEAP      # 堆内存和堆外内存，默认：堆内存
    heartbeat-timeout: 120000   # 心跳超时，单位毫秒，默认: 1000 * 120
    read-buffer-size: 132476    # 一次读取接收数据的 buffer size，超过这个长度的消息会多次读取，默认：132476
    max-bytes-in-message: 10485760  # 消息解析最大 bytes 长度，默认：10M
    debug: true                 # 如果开启 prometheus 指标收集建议关闭
    web-port: 8083              # http、websocket 端口，默认：8083
    websocket-enable: true      # 是否开启 websocket，默认： true
    http-enable: false          # 是否开启 http api，默认： false
    http-basic-auth:
      enable: false             # 是否开启 http basic auth，默认： false
      username: "mica"          # http basic auth 用户名
      password: "mica"          # http basic auth 密码
```

### 2.3 可实现接口（注册成 Spring Bean 即可）

| 接口                            | 是否必须       | 说明                                            |
|-------------------------------|------------|-----------------------------------------------|
| IMqttServerUniqueIdService    | 否          | 用于 clientId 不唯一时，自定义实现唯一标识，后续接口使用它替代 clientId |
| IMqttServerAuthHandler        | 是          | 用于服务端认证                                       |
| IMqttServerSubscribeValidator | 否（建议实现）    | 1.1.3 新增，用于对客户端订阅校验                           |
| IMqttServerPublishPermission  | 否（建议实现）    | 1.2.2 新增，用于对客户端发布权限校验                         |
| IMqttMessageListener          | 否（1.3.x为否） | 消息监听                                          |
| IMqttConnectStatusListener    | 是          | 连接状态监听                                        |
| IMqttSessionManager           | 否          | session 管理                                    |
| IMqttSessionListener          | 否          | session 监听                                    |
| IMqttMessageStore             | 集群是，单机否    | 遗嘱和保留消息存储                                     |
| AbstractMqttMessageDispatcher | 集群是，单机否    | 消息转发，（遗嘱、保留消息转发）                              |
| IpStatListener                | 否          | t-io ip 状态监听                                  |

### 2.4 IMqttMessageListener (用于监听客户端上传的消息) 使用示例

```java
@Service
public class MqttServerMessageListener implements IMqttMessageListener {
    private static final Logger logger = LoggerFactory.getLogger(MqttServerMessageListener.class);

    @Override
    public void onMessage(ChannelContext context, String clientId, Message message) {
        logger.info("clientId:{} message:{} payload:{}", clientId, message, ByteBufferUtil.toString(message.getPayload()));
    }
}
```

### 2.5 自定义配置（可选）

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

### 2.6 MqttServerTemplate 使用示例

```java

import net.dreamlu.iot.mqtt.spring.server.MqttServerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;

/**
 * @author wsq
 */
@Service
public class ServerService {
    @Autowired
    private MqttServerTemplate server;

    public boolean publish(String body) {
        server.publishAll("/test/123", ByteBuffer.wrap(body.getBytes()));
        return true;
    }
}
```

### 2.7 客户端上下线监听
使用 Spring event 解耦客户端上下线监听，注意： `1.3.4` 开始支持。会跟自定义的 `IMqttConnectStatusListener` 实现冲突，取一即可。

```java
@Service
public class MqttConnectStatusListener {
	private static final Logger logger = LoggerFactory.getLogger(MqttConnectStatusListener.class);

	@EventListener
	public void online(MqttClientOnlineEvent event) {
		logger.info("MqttClientOnlineEvent:{}", event);
	}

	@EventListener
	public void offline(MqttClientOfflineEvent event) {
		logger.info("MqttClientOfflineEvent:{}", event);
	}

}
```

### 2.8 基于 mq 消息广播集群处理

详见: [mica-mqtt-broker](../../mica-mqtt-broker)

### 2.9 Prometheus + Grafana 监控对接
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
