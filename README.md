# mica mqtt 组件
![JAVA 8](https://img.shields.io/badge/JDK-1.8+-brightgreen.svg)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/30dad82f79f34e41bafbc3cef6b68fc3)](https://www.codacy.com/gh/lets-mica/mica-mqtt/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lets-mica/mica-mqtt&amp;utm_campaign=Badge_Grade)
[![GitHub](https://img.shields.io/github/license/lets-mica/mica-mqtt.svg?style=flat-square)](https://github.com/lets-mica/mica-mqtt/blob/master/LICENSE)

基于 `t-io` 实现的**低延迟**、**高性能**的 `mqtt` 物联网组件。  

## 功能
- [x] 支持 MQTT v3.1、v3.1.1 以及 v5.0 协议。
- [x] MQTT client 客户端。
- [x] MQTT 客户端 topic sub，添加阿里云 mqtt demo。
- [x] MQTT server 服务端（演示）。
- [ ] MQTT 服务端接续完善，精力有限，周期可能会长一些。

## 文档
- [t-io 官方问](https://www.tiocloud.com/doc/tio/85)
- [mqtt 协议文档](https://github.com/mcxiaoke/mqtt)

##  快速开始
查看 `mica-mqtt-example` 中有 `mqtt` 服务端和客户端演示代码， `main` 方法运行即可。

### 1. 启动 Server 端

运行 `mica-mqtt-example/src/main/java/net/dreamlu/iot/mqtt/server/MqttServerTest.java` 的 `main` 方法

控制台打印如下内容：

```text
2021-07-05 20:42:36,869 INFO  server.TioServer - 
|----------------------------------------------------------------------------------------|
| t-io site         | https://www.tiocloud.com                                           |
| t-io on gitee     | https://gitee.com/tywo45/t-io                                      |
| t-io on github    | https://github.com/tywo45/t-io                                     |
| t-io version      | 3.7.3.v20210706-RELEASE                                            |
| ---------------------------------------------------------------------------------------|
| TioConfig name    | Mica-Mqtt-Server                                                   |
| Started at        | 2021-07-05 20:42:36                                                |
| Listen on         | 127.0.0.1:1883                                                     |
| Main Class        | net.dreamlu.iot.mqtt.server.MqttServerTest                         |
| Jvm start time    | 2715ms                                                             |
| Tio start time    | 16ms                                                               |
| Pid               | 3588                                                               |
|----------------------------------------------------------------------------------------|

2021-07-05 20:42:37,884 WARN  server.MqttServer - Mqtt publish to all ChannelContext is empty.
```

`Mqtt publish to all ChannelContext is empty.` 通道上下文为空，即没有客户端。

```text
Mica-Mqtt-Server
 ├ 当前时间:1625489086843
 ├ 连接统计
 │ 	 ├ 共接受过连接数  :0
 │ 	 ├ 当前连接数            :0
 │ 	 ├ 异IP连接数           :0
 │ 	 └ 关闭过的连接数  :0
 ├ 消息统计
 │ 	 ├ 已处理消息  :0
 │ 	 ├ 已接收消息(packet/byte):0/0
 │ 	 ├ 已发送消息(packet/byte):0/0b
 │ 	 ├ 平均每次TCP包接收的字节数  :0.0
 │ 	 └ 平均每次TCP包接收的业务包  :0.0
 └ IP统计时段 
   	 └ 没有设置ip统计时间
 ├ 节点统计
 │ 	 ├ clientNodes :0
 │ 	 ├ 所有连接               :0
 │ 	 ├ 绑定user数         :0
 │ 	 ├ 绑定token数       :0
 │ 	 └ 等待同步消息响应 :0
 ├ 群组
 │ 	 └ groupmap:0
 └ 拉黑IP 
   	 └ []
2021-07-05 20:44:46,925 WARN  server.ServerTioConfig - Mica-Mqtt-Server, 检查心跳, 共0个连接, 取锁耗时0ms, 循环耗时71ms, 心跳超时时间:120000ms
```

### 2. 启动 Client 端

运行 `mica-mqtt-example/src/main/java/net/dreamlu/iot/mqtt/client/MqttClientTest.java` 的 `main` 方法

控制台打印如下内容，表示客户端连接成功：
```text
2021-07-05 20:46:10,972 ERROR client.TioClient - closeds:0, connections:0
2021-07-05 20:46:10,972 INFO  client.TioClient - [1]: curr:0, closed:0, received:(0p)(0b), handled:0, sent:(0p)(0b)
2021-07-05 20:46:12,566 INFO  client.ConnectionCompletionHandler - connected to 127.0.0.1:1883
2021-07-05 20:46:12,586 INFO  client.MqttClient - MqttClient reconnect send connect result:true
2021-07-05 20:46:12,630 INFO  client.DefaultMqttClientProcessor - MqttClient connection succeeded!
2021-07-05 20:46:13,932 INFO  client.MqttClientTest - /test/123	mica最牛皮
```

此时的 Server 端会打印出如下内容：

```text
2021-07-05 20:46:45,654 INFO  server.MqttServerTest - subscribe:	/test/client	mica最牛皮
2021-07-05 20:46:46,926 WARN  server.ServerTioConfig - 
Mica-Mqtt-Server
 ├ 当前时间:1625489206923
 ├ 连接统计
 │ 	 ├ 共接受过连接数  :1
 │ 	 ├ 当前连接数            :1
 │ 	 ├ 异IP连接数           :1
 │ 	 └ 关闭过的连接数  :0
 ├ 消息统计
 │ 	 ├ 已处理消息  :20
 │ 	 ├ 已接收消息(packet/byte):20/584
 │ 	 ├ 已发送消息(packet/byte):37/935b
 │ 	 ├ 平均每次TCP包接收的字节数  :29.2
 │ 	 └ 平均每次TCP包接收的业务包  :1.0
 └ IP统计时段 
   	 └ 没有设置ip统计时间
 ├ 节点统计
 │ 	 ├ clientNodes :1
 │ 	 ├ 所有连接               :1
 │ 	 ├ 绑定user数         :0
 │ 	 ├ 绑定token数       :0
 │ 	 └ 等待同步消息响应 :0
 ├ 群组
 │ 	 └ groupmap:0
 └ 拉黑IP 
   	 └ []
2021-07-05 20:46:46,926 WARN  server.ServerTioConfig - Mica-Mqtt-Server, 检查心跳, 共1个连接, 取锁耗时0ms, 循环耗时0ms, 心跳超时时间:120000ms
```

### 3. Client 接入 Aliyun MQTT 服务（示例）

详见 `mica-mqtt-example/src/main/java/net/dreamlu/iot/mqtt/aliyun/MqttClientTest.java`

## 参考vs借鉴
- [netty codec-mqtt](https://github.com/netty/netty/tree/4.1/codec-mqtt)
- [jmqtt](https://github.com/Cicizz/jmqtt)
- [iot-mqtt-server](https://gitee.com/recallcode/iot-mqtt-server)
- [moquette](https://github.com/moquette-io/moquette)
- [netty-mqtt-client](https://github.com/jetlinks/netty-mqtt-client)

## 工具
- [mqttx 优雅的跨平台 MQTT 5.0 客户端工具](https://mqttx.app/cn/)
- [mqttx.fx mqtt 客户端](http://mqttfx.org/)

## 微信公众号

![如梦技术](docs/img/dreamlu-weixin.jpg)

精彩内容每日推荐！