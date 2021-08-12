# 变更记录

## 发行版本
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