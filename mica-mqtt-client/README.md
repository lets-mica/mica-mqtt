# 使用文档

## topic 通配符含义
- `/`：用来表示层次，比如 a/b，a/b/c。
- `#`：表示匹配 `>=0` 个层次，比如 a/# 就匹配 a/，a/b，a/b/c。单独的一个 # 表示匹配所有。不允许 a# 和 a/#/c。
- `+`：表示匹配一个层次，例如 a/+ 匹配 a/b，a/c，不匹配 a/b/c。单独的一个 + 是允许的，a+ 不允许，也可以和多层通配符一起使用，+/tennis/# 、sport/+/player1 都有有效的。

## 使用说明

### MQTT 遗嘱消息场景

- 当客户端断开连接时，发送给相关的订阅者的遗嘱消息。在设备 A 进行连接时候，遗嘱消息设定为 `offline`，手机App B 订阅这个遗嘱主题。
- 当 A 异常断开时，手机App B 会收到这个 `offline` 的遗嘱消息，从而知道设备 A 离线了。

### MQTT 保留消息场景

- 例如，某设备定期发布自身 GPS 坐标，但对于订阅者而言，从它发起订阅到第一次收到数据可能需要几秒钟，也可能需要十几分钟甚至更多，这样并不友好。因此 MQTT 引入了保留消息。
- 而每当有订阅者建立订阅时，服务端就会查找是否存在匹配该订阅的保留消息，如果保留消息存在，就会立即转发给订阅者。
- 借助保留消息，新的订阅者能够立即获取最近的状态。

### 共享订阅
mica-mqtt client 支持两种**共享订阅**方式：

1. 共享订阅：订阅前缀 `$queue/`，多个客户端订阅了 `$queue/topic`，发布者发布到topic，则只有一个客户端会接收到消息。
2. 分组订阅：订阅前缀 `$share/<group>/`，组客户端订阅了`$share/group1/topic`、`$share/group2/topic`..，发布者发布到topic，则消息会发布到每个group中，但是每个group中只有一个客户端会接收到消息。

**注意：** mica-mqtt server 暂时还不支持共享订阅。

## 客户端使用
```java
// 初始化 mqtt 客户端
MqttClient client = MqttClient.create()
    .ip("127.0.0.1")                // mqtt 服务端 ip 地址
    .port(1883)                     // 默认：1883
    .username("admin")              // 账号
    .password("123456")             // 密码
    .version(MqttVersion.MQTT_5)    // 默认：3_1_1
    .clientId("xxxxxx")             // 非常重要务必手动设置，一般设备 sn 号，默认：MICA-MQTT- 前缀和 36进制的纳秒数
    .bufferAllocator(ByteBufferAllocator.DIRECT) // 堆内存和堆外内存，默认：堆内存
    .readBufferSize(512)            // 消息一起解析的长度，默认：为 8092 （mqtt 消息最大长度）
    .maxBytesInMessage(1024 * 10)   // 最大包体长度,如果包体过大需要设置此参数，默认为： 10M (10*1024*1024)
    .keepAliveSecs(120)             // 默认：60s
    .timeout(10)                    // 超时时间，t-io 配置，可为 null，为 null 时，t-io 默认为 5
    .reconnect(true)                // 是否重连，默认：true
    .reInterval(5000)               // 重连重试时间，reconnect 为 true 时有效，t-io 默认为：5000
    .willMessage(builder -> {
        builder.topic("/test/offline").messageText("down");    // 遗嘱消息
    })
    .connectListener(new IMqttClientConnectListener() {
        @Override
        public void onConnected(ChannelContext context, boolean isReconnect) {
            logger.info("链接服务器成功...");
        }
        
        @Override
        public void onDisconnect(ChannelContext channelContext, Throwable throwable, String remark, boolean isRemove) {
            logger.info("与链接服务器断开连接...");
        }
    })
    .properties()                   // mqtt5 properties
    .connect();

    // 消息订阅，同类方法 subxxx
    client.subQos0("/test/#", (topic, message, payload) -> {
        logger.info(topic + '\t' + ByteBufferUtil.toString(payload));
    });
    // 取消订阅
    client.unSubscribe("/test/#");

    // 发送消息
    client.publish("/test/client", ByteBuffer.wrap("mica最牛皮".getBytes(StandardCharsets.UTF_8)));

    // 断开连接
    client.disconnect();
    // 重连
    client.reconnect();
    // 停止
    client.stop();
```
