# jfinal mica-mqtt client

## 使用

#### 1. 添加依赖
```xml
<dependency>
	<groupId>net.dreamlu</groupId>
	<artifactId>mica-mqtt-client-jfinal-plugin</artifactId>
	<version>${最新版本}</version>
</dependency>
```

#### 2. 删除 jfinal-demo 中的 slf4j-nop 依赖

#### 3. 添加 slf4j-log4j12
```xml
<dependency>
	<groupId>org.slf4j</groupId>
	<artifactId>slf4j-log4j12</artifactId>
	<version>1.7.33</version>
</dependency>
```

#### 4. 在 jfinal Config configPlugin 中添加 mica-mqtt client 插件
```java
MqttClientPlugin mqttClientPlugin = new MqttClientPlugin();
mqttClientPlugin.config(mqttClientCreator -> {
	// 设置 mqtt 连接配置信息
	mqttClientCreator
			.clientId("clientId") // 按需配置，相同的会互踢
			.ip("mqtt.dreamlu.net")
			.port(1883)
			.connectListener(Aop.get(MqttClientConnectListener.class));
});
me.add(mqttClientPlugin);
```

#### 5. 在 jfinal Config onStart 启动完成之后添加 mqtt 订阅
```java
@Override
public void onStart() {
    IMqttClientMessageListener clientMessageListener = Aop.get(TestMqttClientMessageListener.class);
    MqttClientKit.subQos0("#", clientMessageListener);
}
```

#### 6. 使用 MqttClientKit 发送消息
```java
MqttClientKit.publish("mica", "hello".getBytes(StandardCharsets.UTF_8));
```

### 7. 示例代码 MqttClientConnectListener
```java
public class MqttClientConnectListener implements IMqttClientConnectListener {

    @Override
    public void onConnected(ChannelContext channelContext, boolean isReconnect) {
        if (isReconnect) {
            System.out.println("重连 mqtt 服务器重连成功...");
        } else {
            System.out.println("连接 mqtt 服务器成功...");
        }
    }

    @Override
    public void onDisconnect(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
        System.out.println("mqtt 链接断开 remark:" + remark + " isRemove:" + isRemove);
    }
}
```

### 8. 示例 TestMqttClientMessageListener
```java
public class TestMqttClientMessageListener implements IMqttClientMessageListener {
    @Override
    public void onMessage(String topic, MqttPublishMessage message, byte[] payload) {
        System.out.println("收到消息 topic:" + topic + "内容:\n" + new String(payload, StandardCharsets.UTF_8));
    }
}
```