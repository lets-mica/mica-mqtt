## SpringBoot + mica-mqtt 应用演示

## 启动步骤
1. 启动 MqttServerTest **Mqtt 服务端口：** 3883

2. 启动 MicaMqttApplication **Mqtt 服务端口：** 5883，**Mqtt 客户端连接：** 3883（连 MqttServerTest 服务）

3. 启动 MqttClientTest **Mqtt 客户端连接：** 5883（连 MicaMqttApplication 服务）

4. 查看控制器 swagger 地址：http://localhost:30012/doc.html

5. 可开启 prometheus 指标收集，详见： http://localhost:30012/actuator/prometheus

## 连接

mica Spring boot 开发组件集文档：https://www.dreamlu.net/components/mica-swagger.html
