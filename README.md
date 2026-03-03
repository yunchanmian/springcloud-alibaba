# 全链路金丝雀发布（Gray-Level-Dome）

基于 **Spring Cloud 2021.0.5** 与 **Spring Cloud Alibaba 2021.0.6.0** 实现的全链路金丝雀发布示例工程，包含完整版本信息、模块分布、代码与注释说明。

---

## 一、版本信息

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 1.8 | JDK 8 |
| Spring Boot | 2.6.13 | 与 Spring Cloud 2021.0.x 对应 |
| Spring Cloud | 2021.0.5 | 含 LoadBalancer、Gateway、OpenFeign 等 |
| Spring Cloud Alibaba | 2021.0.6.0 | Nacos 服务发现与配置 |
| Nacos Client | 2.0.3 | 由 Alibaba BOM 管理 |
| Spring Cloud Gateway | 3.1.1 | 网关 |
| Spring Cloud Bootstrap | 3.1.2 | Nacos Config 需配合使用 |
| Lombok | 1.18.24 | 可选 |
| Hutool | 5.8.25 | 工具（gray-common） |
| Commons IO | 2.11.0 | 可选 |

版本对应关系（摘录）：

- Spring Cloud 2021.0.x 对应 Spring Boot 2.6.x
- Spring Cloud Alibaba 2021.0.x 与 Spring Cloud 2021.0.x 配套

---

## 二、模块分布

```
gray-level-dome/
├── pom.xml                    # 父 POM，依赖与版本管理
├── README.md                  # 本说明
├── gray-common/               # 公共库：常量、上下文、JSON 工具
├── gray-gateway/              # 网关：灰度过滤、灰度负载均衡、规则管理
├── gray-consumer/             # 消费者：Feign 透传灰度、调用 gray-provider
└── gray-provider/             # 提供者：stable/gray 双版本示例
```

| 模块 | 职责 | 关键类/配置 |
|------|------|-------------|
| **gray-common** | 灰度常量、GrayContext、GrayContextHolder、JsonUtils | `GrayConstant`、`GrayContext`、`GrayContextHolder` |
| **gray-gateway** | 入口流量打标、按版本路由到下游、灰度规则 | `GrayLoadBalancerFilter`、`GrayLoadBalancer`、`GrayLoadBalancerConfiguration`、`GrayRuleManager` |
| **gray-consumer** | 接收网关请求、透传灰度头、Feign 调用 provider 时按版本路由 | `GrayContextFilter`、`GrayFeignRequestInterceptor`、`ConsumerGrayLoadBalancer` |
| **gray-provider** | 以 stable/gray 两种元数据注册，提供示例接口 | `ProductController`、`application-stable.yaml` / `application-gray.yaml` |

---

## 三、全链路金丝雀流程简述

1. **网关（gray-gateway）**
   - `GrayLoadBalancerFilter`：根据请求头/用户 ID/百分比判断是否灰度请求，构造 `GrayContext`，写入请求头 `x-gray-tag`、`x-gray-context`，并写入 Reactor 上下文。
   - 路由为 `lb://gray-consumer` 或 `lb://gray-provider` 时，使用 `GrayLoadBalancer` 从 Reactor 上下文（或 Request 上下文）取 `GrayContext`，将请求路由到 Nacos 元数据 `version=stable` 或 `version=gray` 的实例。

2. **消费者（gray-consumer）**
   - `GrayContextFilter`：从请求头 `x-gray-context` 解析 `GrayContext` 并放入 `GrayContextHolder`。
   - `GrayFeignRequestInterceptor`：Feign 调用时把灰度相关请求头透传到下游。
   - `ConsumerGrayLoadBalancer`：Feign 调用 `gray-provider` 时，从 `GrayContextHolder` 取灰度标记，选择 stable/gray 实例。

3. **提供者（gray-provider）**
   - 以不同 profile（stable / gray）启动，在 Nacos 注册时设置 `metadata.version=stable` 或 `gray`，供网关与消费者负载均衡筛选。

---

## 四、配置说明

### 4.1 灰度规则（网关）

在 `gray-gateway` 的 `application.yaml` 或 Nacos 中配置，例如：

```yaml
gray:
  enabled: true
  rules:
    user-id:
      percentage: 10          # 约 10% 流量走灰度
      whitelist:
        - "1001"
        - "1002"
```

- 请求头带 `x-gray-tag: gray` 则视为灰度。
- 请求头带 `x-user-id` 且命中白名单则灰度。
- 否则按 `percentage` 随机灰度。

### 4.2 提供者版本（Nacos 元数据）

- **stable**：`application-stable.yaml` 或 `spring.cloud.nacos.discovery.metadata.version=stable`，端口如 48081。
- **gray**：`application-gray.yaml` 或 `spring.cloud.nacos.discovery.metadata.version=gray`，端口如 48091（避免与 stable 同机冲突）。

---

## 五、运行方式

1. 启动 Nacos（如 127.0.0.1:8848）。
2. 编译：`mvn clean install -DskipTests`。
3. 启动提供者（二选一或同时开两个）：
   - 稳定版：`java -jar gray-provider/target/gray-provider.jar --spring.profiles.active=stable`
   - 金丝雀：`java -jar gray-provider/target/gray-provider.jar --spring.profiles.active=gray`
4. 启动消费者：`java -jar gray-consumer/target/gray-consumer.jar`。
5. 启动网关：`java -jar gray-gateway/target/gray-gateway.jar`。

验证示例：

- 网关直接访问提供者：`curl "http://localhost:48080/api/product/hello?from=gateway"`，可通过请求头 `x-user-id: 1001` 或 `x-gray-tag: gray` 观察命中 gray 或 stable。
- 网关经消费者再调提供者：`curl "http://localhost:48080/api/consumer/hello?from=gateway"`，同样通过上述请求头控制全链路灰度。

---

## 六、代码与注释

- **gray-common**：常量、上下文、工具类均带 JavaDoc，说明用途与全链路含义。
- **gray-gateway**：Filter、LoadBalancer、Configuration、RuleManager 中注释说明了与 Spring Cloud 2021.0.5、Reactor 上下文、Nacos 的配合方式。
- **gray-consumer**：Filter、Feign 拦截器、LoadBalancer 注释说明了从请求头到 `GrayContextHolder`、再到 Feign 透传与负载均衡的流程。
- **gray-provider**：Controller 与 profile 配置注释说明了 stable/gray 双版本注册与验证方式。

以上即为本工程的全链路金丝雀发布实现说明、版本信息与模块分布。
