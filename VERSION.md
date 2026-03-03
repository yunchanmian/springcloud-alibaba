# 版本与依赖清单（Gray-Level-Dome）

## 核心版本矩阵

| 依赖 | 版本 | 说明 |
|------|------|------|
| spring-boot-dependencies | 2.6.13 | 父 POM 中 springboot.version |
| spring-cloud-dependencies | 2021.0.5 | 父 POM 中 spring-cloud.version |
| spring-cloud-alibaba-dependencies | 2021.0.6.0 | 父 POM 中 spring-cloud-alibaba.version |
| spring-cloud-starter-gateway | 3.1.1 | 与 2021.0.5 兼容的 Gateway 版本 |
| spring-cloud-starter-bootstrap | 3.1.2 | Nacos Config 需配合 Bootstrap |
| nacos-client | 2.0.3 | 由 spring-cloud-alibaba-dependencies 管理 |
| lombok | 1.18.24 | 可选 |
| hutool-all | 5.8.25 | gray-common |
| commons-io | 2.11.0 | 可选 |

## 模块 artifact 与版本

| 模块 | groupId | artifactId | 版本 |
|------|---------|------------|------|
| 父工程 | org.example | gray-level-dome | 1.0-SNAPSHOT |
| 公共库 | org.gray.common | gray-common | 1.0-SNAPSHOT |
| 网关 | com.gray.level | gray-gateway | 1.0-SNAPSHOT |
| 消费者 | com.gray.level | gray-consumer | 1.0-SNAPSHOT |
| 提供者 | com.gray.level | gray-provider | 1.0-SNAPSHOT |

## Spring Cloud 2021.0.5 与 Alibaba 2021.0.6.0 说明

- **Spring Cloud 2021.0.5** 对应 **Spring Boot 2.6.x**，内置 LoadBalancer、Gateway、OpenFeign、Bootstrap 等。
- **Spring Cloud Alibaba 2021.0.6.0** 在该组合下提供 Nacos 服务发现与配置中心能力，用于注册 stable/gray 实例与可选灰度规则热更新。

本工程全链路金丝雀基于上述版本实现，保证兼容性。
