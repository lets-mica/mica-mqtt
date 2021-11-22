# mica-mqtt-broker 文档

## 说明
- 服务器需要安装 redis

## 功能
- 基于 redis pub/sub 实现 mqtt 集群。
- 基于 redis 实现客户端状态存储。
- 基于 redis 实现遗嘱、保留消息存储。

## 二开说明
- 修改 auth 包下的 MqttAuthHandler、MqttSubscribeValidator 和 MqttHttpAuthFilter 认证实现。
- 在 MqttMessageServiceImpl 中添加业务逻辑。

## 依赖文档
- [mica-lite 文档](https://gitee.com/596392912/mica/tree/master/mica-lite)
- [mica-redis 文档](https://gitee.com/596392912/mica/tree/master/mica-redis)
- [mica-logging 文档](https://gitee.com/596392912/mica/tree/master/mica-logging)
- [mica-swagger 文档](https://gitee.com/596392912/mica/tree/master/mica-swagger)
