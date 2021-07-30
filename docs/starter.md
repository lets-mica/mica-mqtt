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

## 可实现接口（注册成 Spring Bean 即可）

| 接口                        | 是否必须       | 说明                 |
| --------------------------- | -------------- | ------------------ |
| IMqttServerAuthHandler      | 是             | 用于客户端认证       |
| IMqttMessageListener        | 是             | 消息监听            |
| IMqttConnectStatusListener  | 是             | 连接状态监听         |
| IMqttServerSubscribeManager | 否             | 订阅管理            |
| IMqttSessionManager         | 否             | session 管理        |
| IMqttMessageStore           | 集群是，单机否   | 遗嘱和保留消息存储    |
| IMqttMessageDispatcher      | 集群是，单机否   | 消息转发             |
| IpStatListener              | 否             | t-io ip 转态监听     |

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