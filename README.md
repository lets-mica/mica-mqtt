# 🌐 mica mqtt 组件
[![Java CI](https://github.com/lets-mica/mica-mqtt/workflows/Java%20CI/badge.svg)](https://github.com/lets-mica/mica-mqtt/actions)
![JAVA 8](https://img.shields.io/badge/JDK-1.8+-brightgreen.svg)
[![Mica Maven release](https://img.shields.io/nexus/r/https/oss.sonatype.org/net.dreamlu/mica-mqtt-codec.svg?style=flat-square)](https://mvnrepository.com/artifact/net.dreamlu/mica-mqtt-codec/)
[![Mica-mqtt maven snapshots](https://img.shields.io/nexus/s/https/oss.sonatype.org/net.dreamlu/mica-mqtt-codec.svg?style=flat-square)](https://oss.sonatype.org/content/repositories/snapshots/net/dreamlu/mica-mqtt-codec/)

[![star](https://gitee.com/596392912/mica-mqtt/badge/star.svg?theme=dark)](https://gitee.com/596392912/mica-mqtt/stargazers)
[![GitHub Repo stars](https://img.shields.io/github/stars/lets-mica/mica-mqtt?label=Github%20Stars)](https://github.com/lets-mica/mica-mqtt)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/30dad82f79f34e41bafbc3cef6b68fc3)](https://www.codacy.com/gh/lets-mica/mica-mqtt/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=lets-mica/mica-mqtt&amp;utm_campaign=Badge_Grade)
[![GitHub](https://img.shields.io/github/license/lets-mica/mica-mqtt.svg?style=flat-square)](https://github.com/lets-mica/mica-mqtt/blob/master/LICENSE)

---

`mica-mqtt` **低延迟**、**高性能**的 `mqtt` 物联网组件。更多使用方式详见： **mica-mqtt-example** 模块。

## 🍱 使用场景

- 物联网（云端 mqtt broker）
- 物联网（边缘端消息通信）
- 群组类 IM
- 消息推送
- 简单易用的 mqtt 客户端

## 🚀 优势
- 平凡却不单调，简单却不失精彩。
- 手动档（更加易于二次开发或扩展）。
- 牛犊初生，无限可能。

## ✨ 功能
- [x] 支持 MQTT v3.1、v3.1.1 以及 v5.0 协议。
- [x] 支持 websocket mqtt 子协议（支持 mqtt.js）。
- [x] 支持 http rest api，[http api 文档详见](docs/http-api.md)。
- [x] 支持 MQTT client 客户端。
- [x] 支持 MQTT server 服务端。
- [x] 支持 MQTT 遗嘱消息。
- [x] 支持 MQTT 保留消息。
- [x] 支持自定义消息（mq）处理转发实现集群。
- [x] MQTT 客户端 阿里云 mqtt 连接 demo。
- [x] 支持 GraalVM 编译成本机可执行程序。
- [x] 支持 Spring boot 项目快速接入。
- [x] 支持对接 Prometheus + Grafana 实现监控。
- [x] 基于 redis pub/sub 实现集群，详见 [mica-mqtt-broker 模块](mica-mqtt-broker)。

## 🌱 待办

- [ ] 优化处理 mqtt session，以及支持 v5.0 
- [ ] 基于 easy-rule + druid sql 解析，实现规则引擎。

## 🚨 默认端口

| 端口号 | 协议            | 说明                             |
| ------ | --------------- | -------------------------------- |
| 1883   | tcp             | mqtt tcp 端口                    |
| 8083   | http、websocket | http api 和 websocket mqtt 子协议端口 |

**演示地址**：mqtt.dreamlu.net 端口同上。

## 📦️ 依赖

### Spring boot 项目
**客户端：**
```xml
<dependency>
  <groupId>net.dreamlu</groupId>
  <artifactId>mica-mqtt-client-spring-boot-starter</artifactId>
  <version>${mica-mqtt.version}</version>
</dependency>
```

**配置详见**：[mica-mqtt-client-spring-boot-starter 使用文档](starter/mica-mqtt-client-spring-boot-starter/README.md)

**服务端：**
```xml
<dependency>
  <groupId>net.dreamlu</groupId>
  <artifactId>mica-mqtt-server-spring-boot-starter</artifactId>
  <version>${mica-mqtt.version}</version>
</dependency>
```

**配置详见**：[mica-mqtt-server-spring-boot-starter 使用文档](starter/mica-mqtt-server-spring-boot-starter/README.md)

### 非 Spring boot 项目

### 客户端
```xml
<dependency>
  <groupId>net.dreamlu</groupId>
  <artifactId>mica-mqtt-client</artifactId>
  <version>${mica-mqtt.version}</version>
</dependency>
```

### 服务端
```xml
<dependency>
  <groupId>net.dreamlu</groupId>
  <artifactId>mica-mqtt-server</artifactId>
  <version>${mica-mqtt.version}</version>
</dependency>
```

**配置详见**：[mica-mqtt 使用文档](mica-mqtt-core/README.md)

## 📝 文档
- [mqtt科普、mqttx、mica-mqtt的使用**视频**](https://b23.tv/VJ8yc7v)
- [mica-mqtt 快速开始](example/README.md)
- [mica-mqtt-client-spring-boot-starter 使用文档](starter/mica-mqtt-client-spring-boot-starter/README.md)
- [mica-mqtt-server-spring-boot-starter 使用文档](starter/mica-mqtt-server-spring-boot-starter/README.md)
- [jfinal-mica-mqtt-client 使用文档](starter/jfinal-mica-mqtt-client/README.md)
- [jfinal-mica-mqtt-server 使用文档](starter/jfinal-mica-mqtt-server/README.md)
- [mica-mqtt 使用文档](mica-mqtt-core/README.md)
- [mica-mqtt http api 文档详见](docs/http-api.md)
- [mica-mqtt 使用常见问题汇总](https://gitee.com/596392912/mica-mqtt/issues/I45GO7)
- [mica-mqtt 发行版本](CHANGELOG.md)
- [mqtt 协议文档](https://github.com/mcxiaoke/mqtt)

## 💡 参考vs借鉴
- [netty codec mqtt](https://github.com/netty/netty/tree/4.1/codec-mqtt)
- [jmqtt](https://github.com/Cicizz/jmqtt)
- [iot-mqtt-server](https://gitee.com/recallcode/iot-mqtt-server)
- [netty-mqtt-client](https://github.com/jetlinks/netty-mqtt-client)

## 🏗️ mqtt 客户端工具
- [mqttx 优雅的跨平台 MQTT 5.0 客户端工具](https://mqttx.app/cn/)
- [mqtt websocket 调试](http://tools.emqx.io/)
- [mqttx.fx mqtt 客户端](http://mqttfx.org/)

## 🍻 开源推荐
- `Avue` 基于 vue 可配置化的前端框架：[https://gitee.com/smallweigit/avue](https://gitee.com/smallweigit/avue)
- `pig` 上央视的微服务框架（架构必备）：[https://gitee.com/log4j/pig](https://gitee.com/log4j/pig)
- `SpringBlade` 企业级解决方案（企业开发必备）：[https://gitee.com/smallc/SpringBlade](https://gitee.com/smallc/SpringBlade)
- `IJPay` 支付 SDK，让支付触手可及：[https://gitee.com/javen205/IJPay](https://gitee.com/javen205/IJPay)
- `JustAuth` 史上最全的第三方登录开源库: [https://github.com/zhangyd-c/JustAuth](https://github.com/zhangyd-c/JustAuth)
- `spring-boot-demo` Spring boot 深度学习实战: [https://github.com/xkcoding/spring-boot-demo](https://github.com/xkcoding/spring-boot-demo)

## 💚 鸣谢
感谢 JetBrains 提供的免费开源 License：

[![JetBrains](docs/img/jetbrains.png)](https://www.jetbrains.com/?from=mica-mqtt)

感谢 `如梦技术 VIP` **小伙伴们**的鼎力支持，更多 **VIP** 信息详见：https://www.dreamlu.net/vip/index.html

## 🍱 赞助计划
mica-mqtt 始于一份热爱，也得到不少朋友的认可，为了更好的发展，特推出赞助计划。**知识付费**，让你我走的更远！！！

| 类型    | ￥   | 权益（永久）                                                |
|-------|-----|-------------------------------------------------------|
| 🥈赞助人 | 199 | mica-mqttx 源码。                                        |
| 🏅赞助人 | 599 | 提供 emqx kafka 插件，支持 kakfa 集群和分区。                      |
| 💎赞助人 | 699 | mica-mqttx 源码 + mica-links(物联网平台源码，开发中价优，后续会涨价) 相关资源。 |

**注意：** 加微信 **DreamLuTech** 详聊。

## 📱 微信

![如梦技术](docs/img/dreamlu-weixin.jpg)

加微信暗号 **mica-mqtt** 拉入群，精彩内容每日推荐！