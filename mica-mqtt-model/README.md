# mica-mqtt-model 说明

## 消息类型(MessageType)

注意：非 mqtt 的消息类型，属于自定义，用于集群消息分发。

| 属性        | type | 说明     |
| ----------- | ---- | -------- |
| CONNECT     | 1    | 连接     |
| SUBSCRIBE   | 2    | 订阅     |
| UNSUBSCRIBE | 3    | 取消订阅 |
| UP_STREAM   | 4    | 上行消息 |
| DOWN_STREAM | 5    | 下行消息 |
| DISCONNECT  | 6    | 断开连接 |

## 消息模型(Message)

| 属性              | not null | 说明                                                     |
| ----------------- | -------- | -------------------------------------------------------- |
| node              |          | 节点Id，用于集群                                         |
| id                |          | mqtt 消息 id                                             |
| fromClientId      |          | 消息来源 客户端 id                                       |
| fromUsername      |          | 消息来源 用户名                                          |
| clientId          |          | 消息目的地 客户端 ID，主要是在遗嘱消息用、集群内部通信等 |
| username          |          | 消息目的用户名，主要是在遗嘱消息用                       |
| topic             |          | topic                                                    |
| messageType       |          | 消息类型                                                 |
| dup               |          | 是否重发                                                 |
| qos               |          | qos                                                      |
| retain            |          | retain                                                   |
| payload           |          | 消息内容                                                 |
| peerHost          | 是       | 客户端的 IPAddress                                       |
| timestamp         | 是       | 存储时间                                                 |
| publishReceivedAt |          | PUBLISH 消息到达 Broker 的时间 (ms)                      |

## 消息序列化(IMessageSerializer)

| 实现                      | 是否默认 | 说明                                  |
| ------------------------- | -------- | ------------------------------------- |
| DefaultMessageSerializer  | 是       | 默认的序列化，包体更小、速度更快      |
| FastJsonMessageSerializer | 否       | fastjson 序列化，数据信息更加清晰明了 |
