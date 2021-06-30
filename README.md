# mica mqtt 组件
![JAVA 8](https://img.shields.io/badge/JDK-1.8+-brightgreen.svg)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/30dad82f79f34e41bafbc3cef6b68fc3)](https://www.codacy.com/gh/lets-mica/mica-mqtt/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lets-mica/mica-mqtt&amp;utm_campaign=Badge_Grade)
[![GitHub](https://img.shields.io/github/license/lets-mica/mica-mqtt.svg?style=flat-square)](https://github.com/lets-mica/mica-mqtt/blob/master/LICENSE)

基于 `t-io` 实现的 `mqtt` 物联网组件。  

## 使用
目前仅仅是试验性质，不过 `t-io` 确实很稳，`mica-mqtt-example` 中有 `mqtt` 服务端和客户端测试代码， `main` 方法运行即可。

## 功能
- [x] 支持 MQTT v3.1、v3.1.1 以及 v5.0 协议。
- [x] MQTT client 客户端。
- [x] MQTT 客户端 topic sub，添加阿里云 mqtt demo。
- [x] MQTT server 服务端（演示）。
- [ ] MQTT 服务端接续完善，精力有限，周期可能会长一些。

## 文档
- [mqtt 协议文档](https://github.com/mcxiaoke/mqtt)

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