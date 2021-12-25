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

### mica-mqtt 多个客户端直接交互

- A APP 端订阅 `/a/door/open`，
- B web 网页端 mqtt.js 订阅 `/a/door/open`，
- Mqtt 服务端实现 `IMqttMessageListener`，将消息转交给 `AbstractMqttMessageDispatcher`（自定义实现）处理。
- C 发布 `/a/door/open`
- 结果：A 和 B 将收到 C 发布的消息，并完成相应的效果展示。

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
    .maxBytesInMessage(1024 * 10)   // 最大包体长度,如果包体过大需要设置此参数，默认为： 8092
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
    client.subQos0("/test/#", (topic, payload) -> {
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

## 服务端使用
```java
// 注意：为了能接受更多链接（降低内存），请添加 jvm 参数 -Xss129k
MqttServer mqttServer = MqttServer.create()
    // 服务端 ip 默认为空，0.0.0.0，建议不要设置
    .ip("0.0.0.0")
    // 默认：1883
    .port(1883)
    // 默认为： 8092（mqtt 默认最大消息大小），为了降低内存可以减小小此参数，如果消息过大 t-io 会尝试解析多次（建议根据实际业务情况而定）
    .readBufferSize(512)
    // 最大包体长度，如果包体过大需要设置此参数，默认为： 8092
    .maxBytesInMessage(1024 * 100)
    // 自定义认证
    .authHandler((clientId, userName, password) -> true)
    // 消息监听
    .messageListener((context, clientId, message) -> {
        logger.info("clientId:{} message:{} payload:{}", clientId, message, ByteBufferUtil.toString(message.getPayload()));
    })
    // 堆内存和堆外内存选择，默认：堆内存
    .bufferAllocator(ByteBufferAllocator.HEAP)
    // 心跳超时时间，默认：120s
    .heartbeatTimeout(120_1000L)
    // ssl 配置
    .useSsl("", "", "")
    // 自定义客户端上下线监听
    .connectStatusListener(new IMqttConnectStatusListener() {
        @Override
        public void online(String clientId) {

        }

        @Override
        public void offline(String clientId) {

        }
    })
    // 自定义消息转发，可用 mq 广播实现集群化处理
    .messageDispatcher(new IMqttMessageDispatcher() {
        @Override
        public void config(MqttServer mqttServer) {

        }

        @Override
        public boolean send(Message message) {
            return false;
        }

        @Override
        public boolean send(String clientId, Message message) {
            return false;
        }
    })
    .debug() // 开启 debug 信息日志
    .start();

// 发送给某个客户端
mqttServer.publish("clientId","/test/123", ByteBuffer.wrap("mica最牛皮".getBytes()));

// 发送给所有在线监听这个 topic 的客户端
mqttServer.publishAll("/test/123", ByteBuffer.wrap("mica最牛皮".getBytes()));

// 停止服务
mqttServer.stop();
```
