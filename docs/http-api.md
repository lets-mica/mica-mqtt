# Http Api 接口

### HTTP 状态码 (status codes)

接口在调用成功时总是返回 200 OK，响应内容则以 JSON 格式返回。

可能的状态码如下：

| Status Code | Description                                              |
| ----------- | -------------------------------------------------------- |
| 200         | 成功，返回的 JSON 数据将提供更多信息                     |
| 400         | 客户端请求无效，例如请求体或参数错误                     |
| 401         | 客户端未通过服务端认证，使用无效的身份验证凭据可能会发生 |
| 404         | 找不到请求的路径或者请求的对象不存在                     |
| 405         | 请求方法错误                     						 |
| 500         | 服务端处理请求时发生内部错误                             |

### 返回码 (result codes)

接口的响应消息体为 JSON 格式，其中总是包含返回码 `code`。

可能的返回码如下：

| Return Code | Description                |
| ----------- | -------------------------- |
| 1           | 成功                       |
| 101         | 关键请求参数缺失           |
| 102         | 请求参数错误               |
| 103         | 用户名或密码错误           |
| 104         | 请求方法错误               |
| 105         | 未知错误                   |



## 消息发布

### POST /api/v1/mqtt/publish

发布 MQTT 消息。

**Parameters (json):**

| Name     | Type    | Required | Default | Description                                                 |
| -------- | ------- | -------- | ------- | ----------------------------------------------------------- |
| topic    | String  | Required |         | 主题                                                         |
| clientId | String  | Required |         | 客户端标识符                                                  |
| payload  | String  | Required |         | 消息正文                                                      |
| encoding | String  | Optional | plain   | 消息正文使用的编码方式，目前仅支持 目前仅支持 `plain`、`hex`、`base64` |
| qos      | Integer | Optional | 0       | QoS 等级                                                    |
| retain   | Boolean | Optional | false   | 是否为保留消息                                              |

**Success Response Body (JSON):**

| Name | Type    | Description |
| ---- | ------- | ----------- |
| code | Integer | 0           |

**Examples:**

```bash
$ curl -i --basic -u mica:mica -X POST "http://localhost:8083/api/v1/mqtt/publish" -d '{"topic":"a/b/c","payload":"Hello World","qos":1,"retain":false,"clientId":"example"}'

{"code":0}
```

## 主题订阅

### POST /api/v1/mqtt/subscribe

订阅 MQTT 主题。

**Parameters (json):**

| Name     | Type    | Required | Default | Description                                           |
| -------- | ------- | -------- | ------- | ----------------------------------------------------- |
| topic    | String  | Required |         | 主题                                                  |
| clientId | String  | Required |         | 客户端标识符                                          |
| qos      | Integer | Optional | 0       | QoS 等级                                              |

**Success Response Body (JSON):**

| Name | Type    | Description |
| ---- | ------- | ----------- |
| code | Integer | 0           |

**Examples:**

同时订阅 `a`, `b`, `c` 三个主题

```bash
$ curl -i --basic -u mica:mica -X POST "http://localhost:8083/api/v1/mqtt/subscribe" -d '{"topic":"a/b/c","qos":1,"clientId":"example"}'

{"code":0}
```

### POST /api/v1/mqtt/unsubscribe

取消订阅。

**Parameters (json):**

| Name     | Type   | Required | Default | Description  |
| -------- | ------ | -------- | ------- | ------------ |
| topic    | String | Required |         | 主题         |
| clientId | String | Required |         | 客户端标识符 |

**Success Response Body (JSON):**

| Name | Type    | Description |
| ---- | ------- | ----------- |
| code | Integer | 0           |

**Examples:**

取消订阅 `a` 主题

```bash
$ curl -i --basic -u mica:mica -X POST "http://localhost:8083/api/v1/mqtt/unsubscribe" -d '{"topic":"a","clientId":"example"}'

{"code":0}
```

## 消息批量发布

### POST /api/v1/mqtt/publish/batch

批量发布 MQTT 消息。

**Parameters (json):**

| Name         | Type    | Required | Default | Description                                                 |
| ------------ | ------- | -------- | ------- | ----------------------------------------------------------- |
| [0].topic    | String  | Required |         | 主题                                                         |
| [0].clientId | String  | Required |         | 客户端标识符                                                |
| [0].payload  | String  | Required |         | 消息正文                                                    |
| [0].encoding | String  | Optional | plain   | 消息正文使用的编码方式，目前仅支持 `plain`、`hex`、`base64` |
| [0].qos      | Integer | Optional | 0       | QoS 等级                                                    |
| [0].retain   | Boolean | Optional | false   | 是否为保留消息                                              |

**Success Response Body (JSON):**

| Name | Type    | Description |
| ---- | ------- | ----------- |
| code | Integer | 0           |

**Examples:**

```bash
$ curl -i --basic -u mica:mica -X POST "http://localhost:8083/api/v1/mqtt/publish/batch" -d '[{"topic":"a/b/c","payload":"Hello World","qos":1,"retain":false,"clientId":"example"},{"topic":"a/b/c","payload":"Hello World Again","qos":0,"retain":false,"clientId":"example"}]'

{"code":0}
```

## 主题批量订阅

### POST /api/v1/mqtt/subscribe/batch

批量订阅 MQTT 主题。

**Parameters (json):**

| Name         | Type    | Required | Default | Description                                           |
| ------------ | ------- | -------- | ------- | ----------------------------------------------------- |
| [0].topic    | String  | Required |         | 主题                                                  |
| [0].clientId | String  | Required |         | 客户端标识符                                          |
| [0].qos      | Integer | Optional | 0       | QoS 等级                                              |

**Success Response Body (JSON):**

| Name | Type    | Description |
| ---- | ------- | ----------- |
| code | Integer | 0           |

**Examples:**

一次性订阅 `a`, `b`, `c` 三个主题

```bash
$ curl -i --basic -u mica:mica -X POST "http://localhost:8083/api/v1/mqtt/subscribe/batch" -d '[{"topic":"a","qos":1,"clientId":"example"},{"topic":"b","qos":1,"clientId":"example"},{"topic":"c","qos":1,"clientId":"example"}]'

{"code":0}
```

### POST /api/v1/mqtt/unsubscribe/batch

批量取消订阅。

**Parameters (json):**

| Name         | Type   | Required | Default | Description  |
| ------------ | ------ | -------- | ------- | ------------ |
| [0].topic    | String | Required |         | 主题         |
| [0].clientId | String | Required |         | 客户端标识符 |

**Success Response Body (JSON):**

| Name | Type    | Description |
| ---- | ------- | ----------- |
| code | Integer | 0           |

**Examples:**

一次性取消订阅 `a`, `b` 主题

```bash
$ curl -i --basic -u mica:mica -X POST "http://localhost:8083/api/v1/mqtt/unsubscribe/batch" -d '[{"topic":"a","clientId":"example"},{"topic":"b","clientId":"example"}]'

{"code":0}
```

## 踢除指定客户端

### POST /api/v1/clients/delete

踢除指定客户端。注意踢除客户端操作会将连接与会话一并终结。

**Query Parameters:**

| Name     | Type   | Required | Description |
| -------- | ------ | -------- | ----------- |
| clientId | String | True     | ClientID    |

**Success Response Body (JSON):**

| Name | Type    | Description |
| ---- | ------- | ----------- |
| code | Integer | 0           |

**Examples:**

踢除指定客户端

```bash
$ curl -i --basic -u mica:mica -X POST "http://localhost:8083/api/v1/clients/delete?clientId=123"

{"code":0}
```

