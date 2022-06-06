# 变更记录

## 发行版本
### v1.3.4 - 2022-06-12
- :sparkles: mica-mqtt starter 使用 Spring event 解耦 mqtt client 断连事件。
- :sparkles: mica-mqtt server `IMqttConnectStatusListener#offline` 方法添加 `reason` 断开原因字段。
- :sparkles: 添加赞助计划。**捐助共勉，让你我走的更远！！！**
- :bug: 修复 http api 响应问题。

### v1.3.3 - 2022-05-28
- :sparkles: mica-mqtt 优化线程池。
- :sparkles: mica-mqtt 添加 Compression 压缩接口。
- :sparkles: mica-mqtt 添加 kafka TimingWheel 重构 ack。
- :sparkles: mica-mqtt server 添加 `MqttClusterMessageListener` 方便集群消息处理。
- :sparkles: mica-mqtt client 优化客户端取消订阅逻辑，gitee #I5779A 感谢 `@杨钊` 同学反馈。
- :arrow_up: 升级 fastjson 到 1.2.83。

### v1.3.2 - 2022-05-09
- :sparkles: mica-mqtt topic 匹配完善。
- :sparkles: mica-mqtt 订阅、发布时添加 topicFilter、topicName 校验。

### v1.3.1 - 2022-05-08
- :sparkles: mica-mqtt-broker 默认开启 http 和 basic auth。
- :sparkles: mica-mqtt server 添加服务端共享订阅接口，方便开源之夏学生参与。
- :sparkles: mica-mqtt server 添加 IMqttSessionListener。
- :sparkles: mica-mqtt server publish 保留消息存储。
- :sparkles: mica-mqtt server 统一 http 响应模型、优化 http 请求判断。
- :sparkles: mica-mqtt server 优化 MqttHttpRoutes，添加获取所有路由的方法。
- :sparkles: mica-mqtt server 完善 Result 和 http api。
- :sparkles: mica-mqtt server http api 添加 endpoints 列表接口。
- :sparkles: mica-mqtt client 添加同步连接 connectSync 方法。
- :sparkles: mica-mqtt client 优化 bean 依赖，减少循环依赖可能性。
- :bug: 重构 mqtt topic 匹配规则，提升性能减少内存占用，修复 gitee #I56BTC
- :arrow_up: spring boot、mica 版本升级

### v1.3.0 - 2022-04-17
- :sparkles: mica-mqtt mqtt-server 简化，默认多设备可以直接互相订阅和处理消息。
- :sparkles: mica-mqtt server、client 添加 `tioConfigCustomize` 方法，方便更大程度的自定义 TioConfig。
- :sparkles: 拆分 mica-mqtt-client-spring-boot-starter 和 mica-mqtt-server-spring-boot-starter gitee #I4OTC5
- :sparkles: mica-mqtt-client-spring-boot-example 添加重连动态更新 clientId、username、password 示例。
- :sparkles: mica-mqtt server 添加根据踢出指定 clientId 的 http api 接口。
- :sparkles: mica-mqtt server IMqttConnectStatusListener api 调整，添加用户名字段。
- :sparkles: mica-mqtt server IMqttMessageListener 不再强制要求实现。
- :sparkles: 使用 netty IntObjectHashMap 优化默认 session 存储。
- :sparkles: 添加 github action，用于自动构建开发阶段的 SNAPSHOT 版本。
- :sparkles: 示例项目拆分到 example 目录，mica-mqtt client、server starter 拆分到 starter 目录。
- :arrow_up: 依赖升级.

### v1.2.10 - 2022-03-20
- :sparkles: mica-mqtt server 添加 MQTT 客户端 keepalive 系数 `keepalive-backoff`。
- :sparkles: mica-mqtt client、server 调整发布的日志级别为 debug。
- :sparkles: mica-mqtt client 优化 javadoc。
- :sparkles: mica-mqtt client 重连时，支持重新设置新的鉴权密码。

### v1.2.9 - 2022-03-06
- :sparkles: mqttServer#publishAll() 日志级别调整 gitee #I4W4IS
- :sparkles: @MqttClientSubscribe 支持 springboot 配置 gitee #I4UOR3
- :sparkles: mica-mqtt client 代码优化
- :sparkles: mica-mqtt-spring-boot-example 拆分

### v1.2.8 - 2022-02-20
- :sparkles: mica-mqtt server 优化连接 connect 日志。
- :sparkles: mica-mqtt server 代码优化。
- :sparkles: mica-mqtt server 添加 statEnable 配置，默认关闭，开启 Prometheus 监控，需要设置为 true。
- :sparkles: mica-mqtt client 添加 statEnable 配置，默认关闭。
- :sparkles: mica-mqtt client 优化默认线程池。

### v1.2.7 - 2022-02-13
- :sparkles: mica-mqtt-spring-boot-starter 完善。
- :sparkles: mica-mqtt client 考虑一开始就没有连接上服务端的情况。
- :sparkles: mica-mqtt client 添加 isConnected 方法
- :sparkles: mica-mqtt client、server connectListener 改为异步
- :sparkles: mica-mqtt server ChannelContext 添加用户名，使用 (String) context.get(MqttConst.USER_NAME_KEY) 获取。
- :sparkles: websocket ssl 配置
- :sparkles: 尝试新版 graalvm
- :bug: 修复多个 mica mqtt client 消息id生成器隔离。

### v1.2.6 - 2022-01-19
- :sparkles: mica-mqtt-client 支持 `$share`、`$queue` 共享订阅

### v1.2.5 - 2022-01-16
- :sparkles: mica mqtt server 调整发布权限规则。
- :sparkles: mica mqtt server 自定义接口的异常处理。
- :sparkles: mica mqtt server 放开 tio 队列配置。
- :sparkles: mica mqtt client publish 添加一批 byte[] payload 参数方法。
- :sparkles: mica-mqtt-model DefaultMessageSerializer 重构，**不兼容**。
- :memo: 添加日志，避免遗忘。
- :bug: http websocket 都不开启并排除 tio-websocket-server 依赖时 gitee  #I4Q3CP

### v1.2.4 - 2022-01-09
- :fire: mica-mqtt-core 排除一些不需要的依赖。
- :fire: mica-mqtt-core http websocket 都不开启时，可以排除 tio-websocket-server 依赖。
- :sparkles: mica-mqtt-core MqttTopicUtil 改名为 TopicUtil。
- :sparkles: mica-mqtt-spring-boot-starter `@MqttClientSubscribe` 支持 IMqttClientMessageListener bean。
- :sparkles: mica-mqtt-spring-boot-starter `@MqttClientSubscribe` 支持自定义 MqttClientTemplate Bean。
- :sparkles: mica-mqtt-spring-boot-starter 完善。
- :sparkles: mica-mqtt-codec 缩短 mqtt 版本 key。
- :bug: mica-mqtt-codec 修复 will message。

### v1.2.3 - 2022-01-03
- :sparkles: mica-mqtt-spring-boot-starter `@MqttClientSubscribe` value 改为数组，支持同时订阅多 topic。
- :sparkles: mica-mqtt-core 缓存 TopicFilter Pattern。
- :sparkles: mica-mqtt-core 优化客户端和服务端订阅逻辑 `IMqttServerSubscribeValidator` 接口调整。
- :sparkles: mica-mqtt client 添加批量订阅。
- :sparkles: mica-mqtt client 添加批量取消订阅。
- :sparkles: mica-mqtt client 添加客户端是否断开连接。
- :sparkles: mica-mqtt client 客户端断开重新订阅时支持配置批次大小。
- :bookmark: mica-mqtt client 订阅 `IMqttClientMessageListener` 添加 `onSubscribed` 默认方法。
- :arrow_up: mica-mqtt-example 升级 log4j2 到 2.17.1

### v1.2.2 - 2021-12-26
- :sparkles: mica-mqtt server 添加发布权限接口，无权限直接断开连接，避免高级别 qos 重试浪费资源。
- :sparkles: mica-mqtt-broker 优化节点信息存储
- :sparkles: mica-mqtt client 重复订阅优化。感谢 `@一片小雨滴`
- :sparkles: mica-mqtt client 抽象 IMqttClientSession 接口。
- :bug: 修复重构 AbstractMqttMessageDispatcher 保持跟 mica-mqtt-broker 逻辑一致 gitee #I4MA6A 感谢 `@胡萝博`
- :arrow_up: mica-mqtt-example 升级 log4j2 到 2.17.0

### v1.2.1 - 2021-12-11
- :sparkles: mica-mqtt 优化 topic 匹配。
- :sparkles: mica-mqtt client disconnect 不再自动重连 gitee #I4L0WK 感谢 `@willianfu`。
- :sparkles: mica-mqtt client 添加 retryCount 配置 gitee #I4L0WK 感谢 `@willianfu`。
- :sparkles: mica-mqtt-model message 添加 json 序列化。
- :sparkles: mica-mqtt-broker 重新梳理逻辑。
- :bug: mica-mqtt-spring-boot-starter 在 boot 2.6.x 下 bean 循环依赖 gitee #I4LUZP 感谢 `@hongfeng11`。
- :bug: mica-mqtt server 同一个 clientId 踢出时清除老的 session。
- :bug: mica-mqtt server 集群下一个 clientId 只允许连接到一台服务器。
- :bug: mica-mqtt client 修复 IMqttClientConnectListener onDisconnect 空指针。
- :memo: mica-mqtt-model 添加 README.md

### v1.2.0 - 2021-11-28
- :sparkles: mqtt-mqtt-core client IMqttClientConnectListener 添加 onDisconnect 方法。gitee #I4JT1D 感谢 `@willianfu` 同学反馈。
- :sparkles: mica-mqtt-core server IMqttMessageListener 接口调整，不兼容老版本。
- :sparkles: mica-mqtt-broker 调整上下行消息通道。
- :sparkles: mica-mqtt-broker 添加节点管理。
- :sparkles: mica-mqtt-broker 调整默认的 Message 序列化方式，不兼容老版本。
- :sparkles: mica-mqtt-broker 优化设备上下线，处理节点停机的情况。
- :sparkles: 抽取 mica-mqtt-model 模块，方便后期支持消息桥接，Message 添加默认的消息序列化。 gitee #I4ECEO
- :sparkles: mica-mqtt-model 完善 Message 消息模型，方便集群。
- :bug: mica-mqtt-core MqttClient 修复 ssl 没有设置，感谢 `@hjkJOJO` 同学反馈。
- :bug: 修复 websocket mqtt.js 需要拆包 gitee #I4JYJX 感谢 `@Symous` 同学反馈。
- :memo: 完善 mica-mqtt-broker README.md，添加二开说明。
- :memo: 统一 mica-mqtt server ip 文档。
- :memo: 更新 README.md
- :arrow_up: 升级 tio 到 3.7.5.v20211028-RELEASE AioDecodeException 改为 TioDecodeException，

### v1.1.4 - 2021-10-16
- :sparkles: 添加 uniqueId 概念，用来处理 clientId 不唯一的场景。详见：gitee #I4DXQU
- :sparkles: 微调 `IMqttServerAuthHandler` 认证，添加 uniqueId 参数。

### v1.1.3 - 2021-10-13
- :sparkles: 状态事件接口 `IMqttConnectStatusListener` 添加 ChannelContext 参数。
- :sparkles: 从认证中拆分 `IMqttServerSubscribeValidator` 订阅校验接口，添加 ChannelContext、clientId 参数。
- :sparkles: 认证 `IMqttServerAuthHandler` 调整包、添加 ChannelContext 参数。
- :sparkles: 完善文档和示例，添加默认端口号说明。
- :arrow_up: 依赖升级

### v1.1.2 - 2021-09-12
- :sparkles: 添加 mica-mqtt-broker 模块，基于 redis pub/sub 实现 mqtt 集群。
- :sparkles: mica-mqtt-broker 基于 redis 实现客户端状态存储。
- :sparkles: mica-mqtt-broker 基于 redis 实现遗嘱、保留消息存储。
- :sparkles: mqtt-server http api 调整订阅和取消订阅，方便集群处理。
- :sparkles: mica-mqtt-spring-boot-example 添加 mqtt 和 http api 认证示例。
- :sparkles: 添加 mqtt 5 所有 ReasonCode。
- :sparkles: 优化解码 PacketNeededLength 计算。
- :bug: 修复遗嘱消息，添加消息类型。
- :bug: 修复 mqtt-server 保留消息匹配规则。

### v1.1.1 - 2021-09-05
- :sparkles: mqtt-server 优化连接关闭日志。
- :sparkles: mqtt-server 优化订阅，相同 topicFilter 订阅对 qos 判断。
- :sparkles: mqtt-server 监听器添加 try catch，避免因业务问题导致连接断开。
- :sparkles: mqtt-server 优化 topicFilters 校验。
- :sparkles: mqtt-client 优化订阅 reasonCodes 判断。
- :sparkles: mqtt-client 监听器添加 try catch，避免因业务问题导致连接断开。
- :sparkles: mqtt-client 添加 session 有效期。
- :sparkles: 代码优化，减少 codacy 上的问题。
- :bug: mqtt-server 修复心跳时间问题。
- :bug: 修复 mqtt-server 多个订阅同时匹配时消息重复的问题。
- :bug: mqtt-client 优化连接处理的逻辑，mqtt 连接之后再订阅。
- :bug: 修复 MqttProperties 潜在的一个空指针。

### v1.1.0 - 2021-08-29
- :sparkles: 重构，内置 http，http 和 websocket 公用端口。
- :sparkles: 添加 mica-core 中的 HexUtil。
- :sparkles: 添加 PayloadEncode 工具。
- :sparkles: ServerTioConfig#share 方法添加 groupStat。
- :sparkles: 考虑使用 udp 多播做集群。
- :sparkles: MqttServer、MqttServerTemplate 添加 close、getChannelContext 等方法。
- :sparkles: 重构 MqttServerConfiguration 简化代码。
- :sparkles: 配置项 `mqtt.server.websocket-port` 改为 `mqtt.server.web-port`。
- :memo: 添加 JetBrains 连接。
- :bug: 修复默认的消息转发器逻辑。
- :bug: 修复 websocket 下线无法触发offline gitee #I47K13 感谢 `@willianfu` 同学反馈。 

### v1.0.6 - 2021-08-21
- :sparkles: 添加订阅 topicFilter 校验。
- :sparkles: 优化压测工具，更新压测说明，添加 tcp 连接数更改文档地址。
- :sparkles: mica-mqtt-example 添加多设备交互示例。
- :sparkles: 优化 mica-mqtt-spring-boot-example。
- :sparkles: 优化 deploy.sh 脚本。
- :bug: 优化解码异常处理。
- :bug: 修复心跳超时处理。
- :arrow_up: 升级 spring boot 到 2.5.4

### v1.0.5 - 2021-08-15
- :bug: 修复编译导致的 java8 运行期间的部分问题，NoSuchMethodError: java.nio.ByteBuffer.xxx

### v1.0.3 - 2021-08-15
- :sparkles: mica-mqtt server 添加 websocket mqtt 子协议支持（支持 mqtt.js）。
- :sparkles: mica-mqtt server ip，默认为空，可不设置。
- :sparkles: mica-mqtt client去除 CountDownLatch 避免启动时未连接上服务端卡住。
- :sparkles: mica-mqtt client 添加最大包体长度字段，避免超过 8092 长度的包体导致解析异常。
- :sparkles: mica-mqtt client 添加连接监听 IMqttClientConnectListener。
- :sparkles: mica-mqtt 3.1 协议会校验 clientId 长度，添加配置项 maxClientIdLength。
- :sparkles: mica-mqtt 优化 mqtt 解码异常处理。
- :sparkles: mica-mqtt 日志优化，方便查询。
- :sparkles: mica-mqtt 代码优化，部分 Tio.close 改为 Tio.remove。
- :sparkles: mica-mqtt-spring-boot-example 添加 Dockerfile，支持 `spring-boot:build-image`。
- :sparkles: 完善 mica-mqtt-spring-boot-starter，添加遗嘱消息配置。
- :arrow_up: 升级 t-io 到 3.7.4。

### v1.0.3-RC - 2021-08-12
- :sparkles: 添加 websocket mqtt 子协议支持（支持 mqtt.js）。
- :sparkles: mqtt 客户端去除 CountDownLatch 避免启动时未连接上服务端卡住。
- :sparkles: mica-mqtt 服务端 ip，默认为空，可不设置。
- :sparkles: 完善 mica-mqtt-spring-boot-starter，添加遗嘱消息配置。
- :sparkles: mqtt 3.1 协议会校验 clientId 长度，添加设置。
- :sparkles: mqtt 日志优化，方便查询。
- :sparkles: 代码优化，部分 Tio.close 改为 Tio.remove。
- :arrow_up: 升级 t-io 到 3.7.4。

### v1.0.2 - 2021-08-08
- :memo: 文档添加集群处理步骤说明，添加遗嘱消息、保留消息的使用场景。
- :sparkles: 去除演示中的 qos2 参数，性能损耗大避免误用。
- :sparkles: 遗嘱、保留消息内部消息转发抽象。
- :sparkles: mqtt server 连接时先判断 clientId 是否存在连接关系，有则先关闭已有连接。
- :sparkles: 添加 mica-mqtt-spring-boot-example 。感谢 wsq（ @冷月宫主 ）pr。
- :sparkles: mica-mqtt-spring-boot-starter 支持客户端接入和服务端优化。感谢 wsq（ @冷月宫主 ）pr。
- :sparkles: mica-mqtt-spring-boot-starter 服务端支持指标收集。可对接 `Prometheus + Grafana` 监控。
- :sparkles: mqtt server 接受连接时，先判断该 clientId 是否存在其它连接，有则解绑并关闭其他连接。
- :arrow_up: 升级 mica-auto 到 2.1.3 修复 ide 多模块增量编译问题。

### v1.0.2-RC - 2021-08-04
- :sparkles: 添加 mica-mqtt-spring-boot-example 。感谢 wsq（ @冷月宫主 ）pr。
- :sparkles: mica-mqtt-spring-boot-starter 支持客户端接入和服务端优化。感谢 wsq（ @冷月宫主 ）pr。
- :sparkles: mica-mqtt-spring-boot-starter 服务端支持指标收集。可对接 `Prometheus + Grafana` 监控。
- :sparkles: mqtt server 接受连接时，先判断该 clientId 是否存在其它连接，有则解绑并关闭其他连接。

### v1.0.1 - 2021-08-02
- :sparkles: 订阅管理集成到 session 管理中。
- :sparkles: MqttProperties.MqttPropertyType 添加注释，考虑 mqtt V5.0 新特性处理。
- :sparkles: 添加 Spring boot starter 方便接入，兼容低版本 Spring boot。
- :sparkles: 调研 t-io websocket 子协议。
- :bug: 修复 java8 运行期间的部分问题，NoSuchMethodError: java.nio.ByteBuffer.xxx

### v1.0.1-RC - 2021-07-31
- :sparkles: 添加 Spring boot starter 方便接入。
- :sparkles: 调研 t-io websocket 子协议。

### v1.0.0 - 2021-07-29
- :sparkles: 基于低延迟高性能的 t-io AIO 框架。
- :sparkles: 支持 MQTT v3.1、v3.1.1 以及 v5.0 协议。
- :sparkles: 支持 MQTT client 客户端。
- :sparkles: 支持 MQTT server 服务端。
- :sparkles: 支持 MQTT 遗嘱消息。
- :sparkles: 支持 MQTT 保留消息。
- :sparkles: 支持自定义消息（mq）处理转发实现集群。
- :sparkles: 支持 GraalVM 编译成本机可执行程序。