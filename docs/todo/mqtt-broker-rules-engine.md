# mica-mqtt-broker 规则引擎设计

> 状态：设计稿（待评审）
> 模块：`mica-mqtt-broker`
> 依赖基础：`MqttFunctionManager`（位于 `mica-mqtt-server`）

---

## 1. 背景与目标

mica-mqtt-server 已经提供 `MqttFunctionManager`：基于前缀树 + 通配符（`+` / `#`）匹配 topic，
将命中的消息分发给 `IMqttFunctionMessageListener`。当前业务方通常用它做"收到消息后做点事"。

本设计在 `mica-mqtt-broker` 模块之上构建**轻量级规则引擎**，让业务方可以：

1. 按 topic 规则匹配消息（沿用 `MqttFunctionManager` 的 trie 路由能力）。
2. **顺序执行一组动作**（Sink），例如：日志 → 转发到另一台 MQTT → HTTP 回调 → 写 Kafka。
3. 规则可在**运行时新增 / 更新 / 删除**，无需重启 broker。
4. 通过 **JDK SPI** 让用户以最小成本扩展 Sink、Payload 解析、匹配条件、规则来源、规则存储。
5. 与 broker 集群共存：所有规则都挂在 `MqttServer` 的 publish pipeline，与现有 `ClusterPublishHandler` 协同。

设计原则：

- **薄内核**：broker 模块只提供抽象接口与最常用内置实现（Log / Mqtt / Http），其余由用户扩展。
- **零三方依赖**：broker 核心 jar 不引入任何三方库；Kafka / RocketMQ / DB 等交给 starter / 用户 jar SPI 引入；JSON 复用 server 已有的 `JsonAdapter`，YAML 解析（SnakeYAML）由 starter 层提供（见 6.5）。
- **不暴露网络层**：`Sink` API 只接触 broker 薄封装类型（`RuleContext` / `RuleChannelInfo`），不直接依赖 t-io。
- **约定优于配置**：默认实现可立刻跑起来，需要时可逐步替换。

---

## 2. 总体架构

```
                     ┌────────────────────────────────┐
   publish → topic ──▶ MqttServer publish pipeline │
                     └────────────────┬───────────────┘
                                      ▼ MqttFunctionMessageListener（已有）
                                      │
                                      │ MqttFunctionManager.get(topic)
                                      ▼
                          ┌───────────────────────┐
                          │      RuleEngine       │ ← 新增（broker 模块）
                          └──────────┬────────────┘
                                     │ 0..n 命中规则
                                     ▼
                       ┌─────────────────────────────┐
                       │  Rule { topic, matcher, │
                       │         codec, sinks[] } │
                       └─────────────────┬───────────┘
                                         │ 1..n sinks（顺序执行）
                                         ▼
                       ┌─────────────────────────────┐
                       │   Sink { type, name }       │ ← SPI 可扩展
                       │   └─ send(RuleContext)      │
                       └─────────────────────────────┘
```

核心概念：

| 概念 | 说明 |
|------|------|
| `Rule` | 不可变的规则定义，含 topic 模板、可选 matcher、payload codec、若干 Sink。 |
| `RuleContext` | 规则执行上下文，封装 clientId、topic、qos、payload、headers、用户属性。 |
| `RuleMatcher` | 可选匹配器；命中后才执行 sinks。默认仅按 topic 命中。 |
| `PayloadCodec` | payload 编解码，供 matcher / 调试使用。 |
| `Sink` | 真正干活的单元（转发 / 写库 / 调 HTTP …）。 |
| `SinkFactory` | 通过 type 字符串 + 配置 Map 物化 `Sink`，JDK SPI 注册。 |
| `RuleManager` | 增删改查 + 监听变更，对外暴露运行时 API。 |
| `RuleStore` | 规则持久化抽象，默认内存实现，用户可替换为 DB / 文件 / 配置中心。 |
| `RuleLoader` | 启动期 + 热加载规则来源（YAML / JSON / Nacos / Apollo 等）。 |
| `RuleEngine` | 启动入口：把 `RuleManager` 中的规则挂到 `MqttFunctionManager`。 |

---

## 3. 模块结构（新增文件）

```
mica-mqtt-broker/src/main/java/org/dromara/mica/mqtt/broker/rule/
├── RuleEngine.java                    # 启动入口，挂到 MqttFunctionManager
├── RuleManager.java                   # 增删改查 + 持久化 / 加载器编排
├── Rule.java                          # 不可变规则定义
├── RuleContext.java                   # 执行上下文
├── RuleChannelInfo.java               # 薄封装 ChannelContext，避免 Sink 直接依赖 t-io
├── SinkRegistry.java                  # Sink 物化 + 缓存（按 SinkRef 全量缓存）
├── codec/
│   ├── PayloadCodec.java              # payload 编解码接口
│   ├── PayloadCodecRegistry.java      # 按名称查找 codec（SPI 注册）
│   ├── PayloadCodecFactory.java       # SPI 工厂
│   ├── RawPayloadCodec.java           # 默认实现：透传字节
│   └── StringPayloadCodec.java        # 默认实现：UTF-8 字符串
├── matcher/
│   ├── RuleMatcher.java               # 匹配器接口
│   ├── RuleMatcherFactory.java        # SPI 工厂
│   └── TopicRuleMatcher.java          # 默认：topic 命中即放行
├── sink/
│   ├── Sink.java                      # 转发动作接口
│   ├── SinkFactory.java               # SPI 工厂
│   ├── SinkRef.java                   # 规则中描述 sink 的不可变引用（type + props）
│   ├── LogSink.java                   # 内置：日志
│   ├── LogSinkFactory.java
│   ├── MqttSink.java                  # 内置：复用 mica-mqtt-client
│   ├── MqttSinkFactory.java
│   ├── HttpSink.java                  # 内置：异步 HTTP，走 sink 线程池
│   └── HttpSinkFactory.java
├── store/
│   ├── RuleStore.java                 # 持久化接口
│   ├── RuleStoreListener.java         # 持久化变更监听接口（替代 Flux watch）
│   ├── InMemoryRuleStore.java         # 默认实现
│   └── RuleEvent.java                 # 变更事件（save / delete）
├── loader/
│   ├── RuleLoader.java                # 加载接口（启动 + 可选热加载）
│   ├── RuleLoaderFactory.java         # SPI 工厂
│   ├── YamlRuleLoader.java            # 内置：YAML 文件（依赖见第 6.5 节）
│   └── JsonRuleLoader.java            # 内置：JSON 文件（复用 server JsonAdapter）
├── metrics/
│   ├── RuleMetrics.java              # 每 rule / sink 的 success / failure / latency
│   └── RuleMetricsRecorder.java      # 轻量计数器实现（无 Micrometer 依赖）
└── api/
    └── RuleAdminHandler.java          # 可选：HTTP 管理 API（后续迭代）
```

---

## 4. 核心抽象

### 4.1 `Rule`

```java
public class Rule {
    private final String id;                 // UUID，启动期生成
    private final String name;               // 展示名
    private final String topicFilter;        // 支持 + / #
    private final boolean enabled;           // 软开关
    private final String codecType;          // payload codec 名称（可空 → raw）
    private final String matcherType;        // matcher 名称（可空 → 仅 topic 命中）
    private final Map<String, String> matcherProps; // matcher 配置
    private final List<SinkRef> sinks;       // 顺序执行
    private final boolean stopOnError;       // sink 失败是否中断后续
    private final long timeoutMs;             // 整条规则执行超时，默认 5000ms，<=0 表示不限
    private final Map<String, String> labels;// 自由标签
    // ... builder / getters / equals / hashCode
}
```

`Rule` 描述"如何运行"，**不持有运行时对象**。sink 实例由 `SinkRegistry` 通过 `SinkFactory` 物化，保证序列化与跨节点传递性。

> **缓存语义**：`SinkRegistry` 按 `SinkRef`（type + name + props 全量）缓存 `Sink` 实例。两条规则配置相同的 `SinkRef` 会共享同一个 `Sink` 实例（例如共用同一个 `MqttClient`）。因此 `Sink` 实现必须是**无 rule 状态**的——所有 rule 级数据从 `RuleContext` 取，不要把 rule 状态写到 `Sink` 字段里。

### 4.2 `Sink` / `SinkRef` / `SinkFactory`

```java
public interface Sink {
    String getType();            // 与 SinkFactory.getType() 对应
    String getName();            // 调试用，唯一实例名
    void send(RuleContext ctx) throws Exception;
}

public class SinkRef {
    private final String type;
    private final String name;
    private final Map<String, Object> props;

    /** 静态工厂：name 默认为 type */
    public static SinkRef of(String type) { /* ... */ }
    /** 指定 name，用于多实例区分 */
    public static SinkRef of(String type, String name) { /* ... */ }
    /** 链式设置属性，value 会被原样存入 props */
    public SinkRef prop(String key, Object value) { /* return this; */ }
    /** 从 Map 批量设置属性 */
    public SinkRef props(Map<String, Object> props) { /* return this; */ }
    // ... getters
}

public interface SinkFactory {
    String getType();
    Sink create(SinkRef ref) throws Exception; // 由 SinkRegistry 调用
}
```

用户自定义 Sink（典型 Kafka 实现）：

```java
public class KafkaSinkFactory implements SinkFactory {
    @Override public String getType() { return "kafka"; }
    @Override public Sink create(SinkRef ref) {
        return new KafkaSink(
            (String) ref.getProps().get("bootstrap"),
            (String) ref.getProps().get("topic")
        );
    }
}

public class KafkaSink implements Sink {
    private final Producer<String, byte[]> producer;
    private final String topic;

    public KafkaSink(String bootstrap, String topic) {
        this.topic = topic;
        this.producer = new KafkaProducer<>(propsOf(bootstrap));
    }

    @Override public String getType() { return "kafka"; }
    @Override public String getName() { return "kafka@" + topic; }

    @Override public void send(RuleContext ctx) {
        producer.send(new ProducerRecord<>(topic,
            ctx.getClientId(), ctx.getPayload()));
    }
}
```

`META-INF/services/org.dromara.mica.mqtt.broker.rule.sink.SinkFactory` 注册即可。

### 4.3 `RuleContext`

```java
public class RuleContext {
    private final RuleChannelInfo channel;       // 薄封装，避免 Sink 直接依赖 t-io
    private final String clientId;
    private final String topic;
    private final MqttQoS qos;
    private final byte[] payload;
    private final Map<String, String> headers;   // 来自 mqtt5 user properties 或外部注入
    private final Rule rule;                     // 当前规则
    private final Map<String, Object> attributes; // 用户在 sink 之间传值
    // ... getters
}

/**
 * ChannelContext 的薄封装，避免用户 Sink 直接依赖 t-io。
 * 仅暴露 Sink 常用的最小信息，未来切换网络层不影响用户代码。
 */
public class RuleChannelInfo {
    private final String remoteIp;
    private final int remotePort;
    private final String nodeId;   // 集群节点 id（host:port），单机为本地 server 名
    private final Map<String, Object> ext; // broker 内部可塞入扩展字段
    // ... getters
}
```

`attributes` 是 `ConcurrentHashMap`，允许 sink 之间传递中间结果，例如解码后的 JSON 对象。

> **不暴露 `ChannelContext`**：原设计直接把 t-io 的 `ChannelContext` 暴露到 `Sink` API，用户 Sink 会直接依赖 t-io 类型。改用 `RuleChannelInfo` 薄封装，未来若更换网络层不影响用户代码。如果 Sink 确需底层 `ChannelContext`（极少见），可在 broker 内部 SPI 提供向下转型通道，但不进公开 API。

### 4.4 `RuleMatcher`

```java
public interface RuleMatcher {
    boolean matches(RuleContext ctx);
}

public interface RuleMatcherFactory {
    String getType();
    RuleMatcher create(String name, Map<String, String> props);
}
```

`RuleEngine` 匹配流程：

```java
List<IMqttFunctionMessageListener> fnListeners = functionManager.get(ctx.topic);
for (IMqttFunctionMessageListener fn : fnListeners) {
    if (!(fn instanceof RuleFunctionListener)) {
        fn.onMessage(...);  // 非规则监听器透传
        continue;
    }
    Rule rule = ((RuleFunctionListener) fn).rule;
    if (!rule.isEnabled()) continue;
    RuleMatcher matcher = matcherRegistry.get(rule.getMatcherType());
    if (matcher != null && !matcher.matches(ctx)) continue;
    // 串行执行 sinks（同一 SinkRef 共享同一 Sink 实例，见 4.1 缓存语义）
    for (SinkRef ref : rule.getSinks()) {
        Sink sink = sinkRegistry.materialize(ref); // 按 SinkRef 全量缓存
        try {
            sink.send(ctx);
            ruleMetrics.recordSuccess(rule.getId(), sink.getName());
        } catch (Exception e) {  // 不 catch Throwable，避免吞 OOM/SOE
            ruleMetrics.recordFailure(rule.getId(), sink.getName());
            logger.error("rule {} sink {} failed", rule.getId(), sink.getName(), e);
            if (rule.isStopOnError()) break;
        }
    }
}
```

### 4.5 `PayloadCodec` / `PayloadCodecRegistry`

```java
public interface PayloadCodec {
    Object decode(byte[] payload);   // 业务可读对象（用于 matcher / 调试）
    byte[] encode(Object obj);
}
```

broker 内置：

- `raw` —— 透传（默认）。
- `string` —— UTF-8 字符串。
- `json` —— 由 broker 决定使用 fastjson / jackson（看 mica 依赖）。

用户提供 `PayloadCodecFactory` 即可扩展（如 protobuf / avro / 自研二进制协议）。

### 4.6 `RuleManager`

```java
public class RuleManager {
    // 增删改查
    public Rule addRule(Rule rule);
    public Rule updateRule(String id, UnaryOperator<Rule> updater);
    public boolean removeRule(String id);
    public Rule getRule(String id);
    public List<Rule> listRules();
    public List<Rule> match(String topic); // 调试用

    // 扩展点
    public void registerSinkFactory(SinkFactory factory);
    public void registerMatcherFactory(RuleMatcherFactory factory);
    public void registerCodecFactory(PayloadCodecFactory factory);

    public void setRuleStore(RuleStore store);          // 持久化
    public void addLoader(RuleLoader loader);           // 启动期 + 热加载

    /** 监听规则变更事件，RuleEngine 内部用于同步挂载/摘除 RuleFunctionListener */
    public void addListener(RuleEventListener listener);
    public void removeListener(RuleEventListener listener);

    public void start();                                // 加载 + 监听
    public void stop();
}

@FunctionalInterface
public interface RuleEventListener {
    void onRuleEvent(RuleEvent evt);
}
```

持久化：

```java
public interface RuleStore {
    void save(Rule rule);
    void delete(String id);
    Rule find(String id);                 // 返回 null 表示不存在（不用 Optional，避免跨节点序列化坑）
    List<Rule> loadAll();
    /** 注册变更监听器，用于热加载场景；默认实现可空操作 */
    default void setListener(RuleStoreListener listener) {}
}

public interface RuleStoreListener {
    void onSaved(Rule rule);
    void onDeleted(String ruleId);
}
```

启动：

```java
for (RuleLoader l : loaders) {
    l.loadAll().forEach(this::addRule);
    l.setListener(this::applyEvent);   // 热加载（监听器模式，非 Flux）
}
```

### 4.7 `RuleEngine`

```java
public class RuleEngine {
    public RuleEngine(MqttServerCreator creator, RuleManager ruleManager) {
        this.creator = creator;
        this.ruleManager = ruleManager;

        // 接管 / 包装 MqttFunctionManager
        IMqttMessageListener prev = creator.getMessageListener();
        MqttFunctionManager fnMgr = new MqttFunctionManager();
        if (prev != null) {
            fnMgr.register(new String[]{"#"}, wrapToFunction(prev));
        }
        this.functionManager = fnMgr;
        creator.messageListener(new MqttFunctionMessageListener(fnMgr));

        this.listenerMap = new ConcurrentHashMap<>();  // ruleId -> RuleFunctionListener

        // 监听规则变更
        ruleManager.addListener(this::onRuleEvent);
    }

    private void onRuleEvent(RuleEvent evt) {
        switch (evt.getType()) {
            case ADDED:
            case UPDATED: {
                Rule rule = evt.getRule();
                unregister(evt.getOldRuleId());         // 老 listener 摘掉
                RuleFunctionListener fn = new RuleFunctionListener(rule);
                listenerMap.put(rule.getId(), fn);
                functionManager.register(rule.getTopicFilter(), fn);
                break;
            }
            case REMOVED:
                unregister(evt.getRuleId());
                break;
        }
    }
}
```

---

## 5. mica-mqtt-server 侧增强（最小改动）

需要给 `MqttFunctionManager` 增加：

```java
public void unregister(String topicFilter, IMqttFunctionMessageListener listener) {
    Node node = root;
    String[] parts = TopicUtil.getTopicParts(topicFilter);
    for (int i = 0; i < parts.length; i++) {
        node = node.findNodeByPart(parts[i]);
        if (node == null) return;
        if (i == parts.length - 1) {
            node.listeners.remove(listener);
            // 可选：清理空 listener 的 trie 节点（避免内存泄漏）
        }
    }
}
```

实现要点：

- 使用 `==` 引用比较移除 listener（注册时由 `RuleEngine` 持有强引用，无需 equals/hashCode）。
- **需回收空 listener 的 trie 节点**：原设计"暂不主动压缩"在规则频繁增删时会内存泄漏。这里折中——递归向上清理 `listeners.isEmpty() && children.isEmpty()` 的末端节点；中间节点只要还有子节点就保留，避免反复重建路径。删除极少的场景下成本可忽略，热加载场景下避免泄漏。
- 清理在 `unregister` 内同步完成，不需要后台线程。

> 若不希望修改 server 模块，也可在 broker 内部复制一份 trie 结构，但维护成本高。**推荐在 server 内补 `unregister`**。

---

## 6. 内置 Sink

### 6.1 `LogSink`

```
props: { "level": "info" }
```

打印 `clientId / topic / qos / payload` 到 SLF4J，默认开启，便于排错。

### 6.2 `MqttSink`（复用 mica-mqtt-client）

```
props:
  clientId:   rule-forwarder-1
  host:       cloud-mqtt.example.com
  port:       1883
  username:   fwd
  password:   xxx
  ssl:        false
  topicTemplate: device/{clientId}/{topic}    // 占位符替换
  qos:        1
  retain:     false
```

实现：

- **连接缓存键**：`host:port + clientId + username`（原方案仅 `host:port` 会让相同 host 不同账号的 sink 串号）。用户也可显式传 `connectionName` 覆盖缓存键。
- 启动时 `MqttClientCreator.connect().start()`，broker 关闭时 `stop()`。
- 支持 `topicTemplate`，例如 `device/{clientId}/{topic}` 把原始 topic 拼到目标 topic。
- QoS / retain 直接透传。

### 6.3 `HttpSink`（JDK HttpURLConnection，零依赖）

```
props:
  url:         https://example.com/ingest
  method:      POST
  headers:
    X-Token:   xxx
  contentType: application/json
  timeoutMs:   3000
  async:       true      # 默认 true
```

实现：

- **默认异步**：HTTP 调用提交到 `RuleEngine` 提供的 sink 线程池（与 t-io IO 线程隔离），不阻塞 IO 线程。原方案"默认保持同步以避免在 t-io IO 线程堆积"逻辑反了——同步才会堆积，高频 topic + 慢后端会卡死 broker。
- sink 线程池由 `RuleEngine` 统一管理（`namedPool`：`rule-sink-pool`，core/max/queue 由 `MqttClusterConfig` 或 `MqttServerCreator` 暴露配置项，默认 `2*cpu, 32*cpu, 1024` 队列 + CallerRuns 拒绝策略）。
- `async=false` 时退化为同步调用（仅用于调试或快路径），并在日志里 WARN 提醒不要在生产高频路径用。
- 调用异常按 `Rule.stopOnError` 处理；超时记入 `RuleMetrics` 的 failure。

### 6.4 其它 Sink

`KafkaSink` / `RocketMqSink` / `DBSink` 等不内置在 broker 模块，**由 starter 模块或用户 jar 通过 SPI 提供**，避免 broker 模块引入重依赖。

### 6.5 内置 Loader 的依赖来源

broker 模块坚持"零三方依赖"，因此两个内置 loader 的依赖来源需明确：

| Loader | 依赖来源 | 说明 |
|--------|----------|------|
| `JsonRuleLoader` | 复用 `MqttServerCreator.jsonAdapter()` | server 模块已抽象 `JsonAdapter`（支持 Jackson/Fastjson/Fastjson2/Gson/Hutool-json/Snack3 六选一），broker 不新增任何 JSON 依赖。 |
| `YamlRuleLoader` | **不内置到 broker 主 jar**，放在 `starter/*-spring-boot-starter` 下 | YAML 解析需要 SnakeYAML，Spring Boot 已传递性引入；Solon/JFinal starter 可按需引入。broker 模块本身不依赖 SnakeYAML，只暴露 `RuleLoader` SPI，用户 jar 也可自行提供。 |

> 即 broker 核心 jar 仍保持零三方依赖；YAML 能力是 starter 层的便利实现。

---

## 7. 与 `MqttClusterBrokerCreator` 集成

### 7.1 装配时机（关键修正）

原方案"在 `build()` 末尾追加"是错的：`MqttClusterBrokerCreator.build()` 内部会调用 `serverCreator.build()`，而 `MqttServerCreator.build()` 在末尾把当时持有的 `messageListener` 包进 `MessageListenerHandler` 加进 publish pipeline。也就是说**末尾追加时 pipeline 已经捕获了旧 listener 引用，`creator.messageListener(...)` 的替换无效**。

正确做法是：**在 `serverCreator.build()` 之前完成 `messageListener` 替换**，且这段装配对集群/非集群两种模式都要生效（`build()` 开头的 `if (!clusterConfig.isEnabled()) return serverCreator.build();` 分支也要走规则引擎）。

`MqttClusterBrokerCreator.build()` 改为：

```java
public MqttServer build() {
    // 1. 先装配规则引擎（覆盖集群/非集群两种分支）
    RuleManager ruleManager = new RuleManager(new InMemoryRuleStore());
    loadRuleSpi(ruleManager);  // 静态缓存 SPI 工厂实例，避免多次 build 重复加载
    RuleEngine ruleEngine = new RuleEngine(serverCreator, ruleManager);
    ruleEngine.attachToServerCreator();  // 替换 messageListener + 预挂 RuleFunctionListener
    this.ruleManager = ruleManager;
    this.ruleEngine = ruleEngine;

    // 2. 原有集群分支
    if (clusterConfig == null || !clusterConfig.isEnabled()) {
        MqttServer server = serverCreator.build();  // 此时 listener 已是规则引擎的 fn 包装
        ruleEngine.start(server);
        return server;
    }
    // ... 原有集群装配 ...
    MqttServer mqttServer = serverCreator.build();
    ruleEngine.start(mqttServer);
    return mqttServer;
}
```

`RuleEngine.attachToServerCreator()` 做的事：

```java
void attachToServerCreator() {
    IMqttMessageListener prev = creator.getMessageListener();
    MqttFunctionManager fnMgr = new MqttFunctionManager();
    if (prev != null) {
        fnMgr.register(new String[]{"#"}, wrapToFunction(prev));  // 老的 listener 透传
    }
    this.functionManager = fnMgr;
    creator.messageListener(new MqttFunctionMessageListener(fnMgr));  // 此时替换生效
    this.listenerMap = new ConcurrentHashMap<>();
    ruleManager.addListener(this::onRuleEvent);
}
```

> `wrapToFunction(prev)` 把原来的 `IMqttMessageListener` 适配成一个 `IMqttFunctionMessageListener`，注册到 `#` 上，保持原有行为不变。

### 7.2 SPI 加载

```java
// 静态字段缓存，避免多次 build() 重复 ServiceLoader 扫描
private static volatile List<SinkFactory> CACHED_SINK_FACTORIES;
private static volatile List<RuleMatcherFactory> CACHED_MATCHER_FACTORIES;
private static volatile List<PayloadCodecFactory> CACHED_CODEC_FACTORIES;

private static void loadRuleSpi(RuleManager rm) {
    if (CACHED_SINK_FACTORIES == null) {
        synchronized (MqttClusterBrokerCreator.class) {
            if (CACHED_SINK_FACTORIES == null) {
                CACHED_SINK_FACTORIES = stream(ServiceLoader.load(SinkFactory.class));
                CACHED_MATCHER_FACTORIES = stream(ServiceLoader.load(RuleMatcherFactory.class));
                CACHED_CODEC_FACTORIES = stream(ServiceLoader.load(PayloadCodecFactory.class));
            }
        }
    }
    CACHED_SINK_FACTORIES.forEach(rm::registerSinkFactory);
    CACHED_MATCHER_FACTORIES.forEach(rm::registerMatcherFactory);
    CACHED_CODEC_FACTORIES.forEach(rm::registerCodecFactory);
}
```

### 7.3 暴露 API

```java
public RuleManager getRuleManager() { return ruleManager; }
public RuleEngine getRuleEngine() { return ruleEngine; }
```

### 7.4 集群规则同步（v2，需改 enum）

`ClusterMessage` 是**接口**而非基类，`getType()` 返回 `ClusterMessageType` 枚举。新增 `RuleEvent` 集群消息意味着：

1. `ClusterMessageType` 枚举新增 `RULE_EVENT` 常量；
2. `ClusterMessageSerializer` 注册 `RULE_EVENT` 的序列化/反序列化分支；
3. 新增 `RuleEventClusterMessage implements ClusterMessage`。

这三处改动**不是纯增量**，落地时要在 broker 模块一并改。设计如下：

- `RuleManager` 增加 `applyRemoteEvent(RuleEvent)`：收到集群广播后只更新本地 store + 重新挂载 listener，**不再回广播**（避免环路）。
- leader 节点变更后通过 `RuleEventClusterMessage` 广播，其它节点调用 `applyRemoteEvent`。
- 简易策略：最后写胜出；后续可基于 `revision` 解决冲突。

---

## 8. HTTP 管理 API（可选，预留接口）

复用 `mica-mqtt-server` 已有的 HTTP 通道（18083，由 `HttpApiMessageHandler` 处理 `MessageType.HTTP_API`），新增路由：

| Method | Path             | 说明 |
|--------|------------------|------|
| POST   | `/rule/save`     | 新增或更新（body = Rule JSON） |
| DELETE | `/rule/{id}`     | 删除规则 |
| GET    | `/rule/{id}`     | 查询单条 |
| GET    | `/rule/list`     | 列表 |
| POST   | `/rule/test`     | 用一条样例消息试跑，返回每条 sink 成功/失败 |

实现思路：仓库中**没有独立的 `HttpRouter` 类**，HTTP API 实际走 [HttpApiMessageHandler](file:///e:/codes/gitee/mica-mqtt/mica-mqtt-server/src/main/java/org/dromara/mica/mqtt/core/server/pipeline/message/HttpApiMessageHandler.java)（注册为 `MessageType.HTTP_API` 的 message handler）。新增 `RuleAdminHandler` 时有两种方式：

1. **沿用现有 handler 风格**：把 `RuleAdminHandler` 也注册成 message handler，在 `HttpApiMessageHandler` 内部分流路径前缀 `/rule/` 到它；
2. **直接在 `HttpApiMessageHandler` 内加分支**：原 handler 已经处理 `/publish`、`/clients` 等路径，加 `/rule/*` 分支最省事。

推荐方式 2，改动最小。**v1 先打通内存内的 `RuleManager`，HTTP 接口作为后续迭代。**

---

## 9. 错误处理与可观测性

- 单条 sink 失败：默认 `log + 继续`；`Rule.stopOnError=true` 时中断后续。
- 整体执行超时：`Rule.timeoutMs`（默认 5000ms，<=0 表示不限）。超时由 `RuleEngine` 在 sink 线程池上用 `Future.get(timeout)` 实现，超时后取消当前 sink 但不影响后续 rule。
- 指标：暴露 `RuleMetrics`（不依赖 Micrometer），统计每条 rule、每个 sink 的 success / failure / latency。结构：

```java
public interface RuleMetrics {
    void recordSuccess(String ruleId, String sinkName);
    void recordFailure(String ruleId, String sinkName);
    void recordLatency(String ruleId, String sinkName, long costMs);
    /** 快照，供 HTTP API /log 拉取 */
    Map<String, SinkStat> snapshot();
}

public class SinkStat {
    private final String ruleId;
    private final String sinkName;
    private final long successCount;
    private final long failureCount;
    private final long totalLatencyMs;
    private final long maxLatencyMs;
    // ... getters
}
```

默认实现 `RuleMetricsRecorder` 用 `LongAdder` 计数 + `HdrHistogram`-like 简易分桶（不引入三方库），仅内存。

- 日志：所有 sink 异常统一格式：

```
logger.error("rule {} sink {} failed", rule.getId(), sink.getName(), e);
```

---

## 10. SPI 注册清单

`META-INF/services/` 下，所有可扩展点都基于 JDK `ServiceLoader`，**无第三方依赖**：

| 接口 | 文件名 |
|------|--------|
| `SinkFactory` | `org.dromara.mica.mqtt.broker.rule.sink.SinkFactory` |
| `RuleMatcherFactory` | `org.dromara.mica.mqtt.broker.rule.matcher.RuleMatcherFactory` |
| `PayloadCodecFactory` | `org.dromara.mica.mqtt.broker.rule.codec.PayloadCodecFactory` |
| `RuleLoaderFactory` | `org.dromara.mica.mqtt.broker.rule.loader.RuleLoaderFactory` |
| `RuleStoreFactory` | `org.dromara.mica.mqtt.broker.rule.store.RuleStoreFactory` |

> 用 `ServiceLoader` 而非 Spring/Guice，是为了保持 broker 模块与具体 IoC 容器解耦。starter（Spring Boot / Solon）再封装一层 `@Component` 自动扫描。

---

## 11. 用户视角示例

### 11.1 纯 Java API

```java
MqttBroker broker = MqttBroker.create()
    .clusterConfig(MqttClusterConfig.enabled(true).clusterPort(9001))
    .build();

RuleManager rules = broker.getRuleManager();

rules.addRule(Rule.builder()
    .name("temp-to-cloud")
    .topicFilter("sensor/+/temperature")
    .codecType("json")
    .addSink(SinkRef.of("mqtt")
        .prop("host", "cloud-mqtt.example.com")
        .prop("port", 1883)
        .prop("username", "fwd").prop("password", "xxx")
        .prop("topicTemplate", "cloud/{clientId}/{topic}")
        .prop("qos", "1"))
    .addSink(SinkRef.of("http")
        .prop("url", "https://ingest.example.com/temp")
        .prop("contentType", "application/json"))
    .build());
```

### 11.2 YAML 加载

```yaml
rules:
  - id: rule-001
    name: device-event-to-kafka
    topicFilter: device/+/event
    enabled: true
    codec: json
    matcher: payload-meta-env-is-prod        # 可选：注册名 -> RuleMatcherFactory
    sinks:
      - type: kafka
        bootstrap: 10.0.0.1:9092
        topic: device-events
        acks: all
      - type: http
        url: https://audit.example.com/device
        method: POST
        contentType: application/json
```

加载（YAML loader 由 starter 提供，见 6.5）：

```java
// Spring Boot starter 自动注入 YamlRuleLoader；纯 Java API 需自行 new（依赖 SnakeYAML）
rules.addLoader(new YamlRuleLoader(Paths.get("rules.yml")));
rules.start();   // 加载 + 监听变更
```

### 11.3 自定义 Sink（Kafka）示例

用户 jar 中：

```java
public class KafkaSinkFactory implements SinkFactory {
    public String getType() { return "kafka"; }
    public Sink create(SinkRef ref) {
        return new KafkaSink(
            (String) ref.getProps().get("bootstrap"),
            (String) ref.getProps().get("topic"),
            ref.getProps()
        );
    }
}
```

`META-INF/services/org.dromara.mica.mqtt.broker.rule.sink.SinkFactory`：

```
com.example.KafkaSinkFactory
```

broker 启动时 `ServiceLoader` 自动加载，业务代码无需改动。

---

## 12. 设计权衡

| 议题 | 选择 | 原因 |
|------|------|------|
| 是否引入规则引擎（Drools / Aviator） | 否 | 业务是"topic → sinks"，trie + 顺序动作已足够；自研 200 行内。 |
| sink 同步 vs 异步 | 默认异步 | t-io IO 线程不能长时占用，同步阻塞慢后端会卡死 broker；HTTP/MQTT 转发默认走 sink 线程池，用户 Sink 实现内部也可再用自己的线程池。同步模式仅作调试用。 |
| Kafka 等重依赖是否内置 | 否，放 starter / 用户 jar | broker 模块定位精简；kafka-client 体积大且版本敏感。 |
| 默认持久化 | InMemory | 让 broker 模块零依赖启动；用户按需替换。 |
| IoC 容器 | 不依赖 Spring/Guice | 通过 JDK ServiceLoader 解耦，starter 层再适配。 |
| 配置格式 | YAML/JSON/Properties | JSON 复用 server `JsonAdapter`；YAML 由 starter 提供（依赖 SnakeYAML），其余由用户扩展。 |
| 反应式 API | 不用 Flux/Mono | 原 `RuleStore.watch()` 返回 `Flux` 与"零三方依赖"冲突，改用 `RuleStoreListener` 监听器接口。 |

---

## 13. 落地步骤

| Step | 内容 | 涉及模块 |
|------|------|----------|
| 1 | 在 `mica-mqtt-server` 给 `MqttFunctionManager` 加 `unregister`（含空 listener trie 节点回收） | server |
| 2 | broker 内新增抽象接口：`Sink` / `SinkRef` / `SinkFactory` / `SinkRegistry` / `RuleMatcher` / `PayloadCodec` / `RuleLoader` / `RuleStore` / `RuleStoreListener` / `RuleManager` / `RuleEventListener` / `RuleEngine` / `Rule` / `RuleContext` / `RuleChannelInfo` | broker |
| 3 | 内置 `LogSink` / `MqttSink` / `HttpSink`（默认异步 + sink 线程池）及对应 `SinkFactory` | broker |
| 4 | 内置 `TopicRuleMatcher` / `RawPayloadCodec` / `StringPayloadCodec` / `JsonPayloadCodec` 及 `*Factory` | broker |
| 5 | 内置 `InMemoryRuleStore` / `JsonRuleLoader`；`YamlRuleLoader` 放 starter 层 | broker + starter |
| 6 | 内置 `RuleMetrics` / `RuleMetricsRecorder` | broker |
| 7 | **修改 `MqttClusterBrokerCreator.build()`：在 `serverCreator.build()` 之前装配 `RuleEngine.attachToServerCreator()`，覆盖集群 + 非集群两分支；SPI 静态缓存加载；暴露 `getRuleManager()` / `getRuleEngine()`** | broker |
| 8 | 单测：规则增删、topic 匹配、sink 顺序执行、失败继续、SPI 加载、运行时增删、sink 缓存共享、timeout、HttpSink 异步 | broker |
| 9 | （可选）Kafka sink 单独放 starter 模块 | starter（新） |
| 10 | （可选）HTTP 管理 API：在 `HttpApiMessageHandler` 内加 `/rule/*` 分支 | server + broker |
| 11 | （可选）集群规则同步：改 `ClusterMessageType` enum + `ClusterMessageSerializer`，新增 `RuleEventClusterMessage` | broker |

---

## 14. 后续演进

- **集群规则同步**：通过 `ClusterMessage` 广播 `RuleEvent`，多节点保持一致。
- **HTTP/UI 管理**：18083 端口开放 CRUD + 试跑接口，可与 mica-mqtt http-api 文档同步。
- **指标与告警**：与现有 `ClusterMetrics` 对齐，输出 Micrometer 风格指标。
- **规则版本**：每条 rule 增加 `revision`，冲突时按最后写胜出升级为 revision 比较。
- **可视化编排**：通过 HTTP API + YAML 实现低代码规则配置。