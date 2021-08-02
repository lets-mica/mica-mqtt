# 变更记录

## 发行版本
### v1.0.1 - 2021-08-02
- :sparkles: 订阅管理集成到 session 管理中。
- :sparkles:  MqttProperties.MqttPropertyType 添加注释，考虑 mqtt V5.0 新特性处理。
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