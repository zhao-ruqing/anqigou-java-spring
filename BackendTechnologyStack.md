# 安琦购电商平台后端技术栈

## 1. 基础框架
| 技术 | 版本 | 用途 | 详细配置 |
|------|------|------|----------|
| Spring Boot | 2.7.14 | 微服务基础框架 | 基于Java 11构建，使用Spring Boot DevTools支持热部署 |
| Spring Cloud | 2021.0.5 | 微服务治理框架 | 包含Gateway、Feign、Config等组件 |
| Spring Cloud Alibaba | 2021.0.4.0 | 阿里巴巴微服务生态组件 | 集成Nacos作为服务注册发现和配置中心 |

## 2. 开发语言
| 语言 | 版本 | 编译配置 |
|------|------|----------|
| Java | 11 | Maven 3.x编译，UTF-8编码 |

## 3. 数据库与缓存
| 技术 | 版本 | 用途 | 详细配置 |
|------|------|------|----------|
| MySQL | 8.0.33 | 关系型数据库 | 使用mysql-connector-java 8.0.33驱动，连接URL：`jdbc:mysql://localhost:3306/anqigou?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&allowMultiQueries=true` |
| Redis | 2.7.14 | 缓存、会话存储、限流 | 连接配置：host=localhost, port=6379, database=0, 连接池max-active=20, max-idle=10, min-idle=5 |

## 4. 数据访问层
| 技术 | 版本 | 用途 | 详细配置 |
|------|------|------|----------|
| MyBatis-Plus | 3.5.3 | ORM框架 | 映射文件路径：`classpath:mapper/*.xml`，实体类包：`com.anqigou.*.entity`，开启下划线转驼峰，日志实现：`StdOutImpl` |
| Druid | 1.2.18 | 数据库连接池 | 初始化连接数：5，最小空闲连接：5，最大活跃连接：20，最大等待时间：60000ms，开启预编译语句缓存 |

## 5. 微服务组件
| 组件 | 用途 | 详细配置 |
|------|------|----------|
| Spring Cloud Gateway | API网关 | 端口：8080，支持基于路径的路由、IP限流（replenish-rate=10, burst-capacity=20），默认过滤器包含请求速率限制 |
| Nacos | 服务注册与发现、配置中心 | 地址：localhost:8848，命名空间：public，配置文件格式：yml |
| Feign | 服务间通信 | 基于Spring Cloud OpenFeign实现，版本3.1.5 |

## 6. 安全认证
| 技术 | 版本 | 用途 | 详细配置 |
|------|------|------|----------|
| JWT | 0.11.5 | 身份认证与授权 | 使用jjwt-api、jjwt-impl、jjwt-jackson组件，实现无状态认证 |
| 微信小程序 | - | 第三方登录 | 配置AppID：wx8dbab4f617ac7415，密钥：397b54148106ca36fa1217e6afd9d108 |

## 7. 搜索引擎
| 技术 | 版本 | 用途 | 详细配置 |
|------|------|------|----------|
| Elasticsearch | 7.17.9 | 商品搜索、日志分析 | 地址：localhost:9200，支持自动初始化索引和数据同步，可通过配置启用/禁用 |

## 8. 工具库
| 技术 | 版本 | 用途 | 详细配置 |
|------|------|------|----------|
| Lombok | 1.18.30 | 代码简化 | 提供@Data、@AllArgsConstructor、@NoArgsConstructor等注解 |
| Jackson | 2.15.2 | JSON处理 | 用于对象序列化和反序列化，版本：2.15.2 |

## 9. 项目架构

### 9.1 模块结构
| 模块名称 | 端口 | 功能描述 | 主要依赖 |
|----------|------|----------|----------|
| **anqigou-common** | - | 公共模块，包含工具类、常量、实体类等 | Lombok、Jackson、JWT |
| **anqigou-gateway** | 8080 | API网关，负责请求路由、限流、熔断等 | Spring Cloud Gateway、Redis、Nacos |
| **anqigou-user-service** | 8081 | 用户服务，处理用户注册、登录、信息管理等 | MySQL、Redis、MyBatis-Plus、微信小程序SDK |
| **anqigou-product-service** | 8082 | 产品服务，处理商品CRUD、分类、搜索等 | MySQL、Redis、MyBatis-Plus、Elasticsearch |
| **anqigou-order-service** | 8083 | 订单服务，处理订单创建、支付、查询等 | MySQL、Redis、MyBatis-Plus |
| **anqigou-payment-service** | 8084 | 支付服务，处理支付回调、退款等 | MySQL、Redis、MyBatis-Plus |
| **anqigou-logistics-service** | 8085 | 物流服务，处理物流信息查询、更新等 | MySQL、Redis、MyBatis-Plus |
| **anqigou-seller-service** | 8086 | 卖家服务，处理店铺管理、商品发布等 | MySQL、Redis、MyBatis-Plus |
| **anqigou-admin-service** | 8087 | 管理员服务，处理后台管理、统计报表等 | MySQL、Redis、MyBatis-Plus |

### 9.2 网关路由配置
| 路由ID | 服务名称 | 路径规则 | 过滤器 |
|--------|----------|----------|--------|
| user-service | anqigou-user-service | /api/auth/**, /api/user/**, /api/feedback/** | StripPrefix=1 |
| product-service | anqigou-product-service | /api/product/** | StripPrefix=1 |
| order-service | anqigou-order-service | /api/order/**, /api/cart/** | StripPrefix=1 |
| payment-service | anqigou-payment-service | /api/payment/** | StripPrefix=1 |
| logistics-service | anqigou-logistics-service | /api/logistics/**, /api/subscribe/** | StripPrefix=1 |
| seller-service | anqigou-seller-service | /api/seller/** | StripPrefix=1 |
| admin-service | anqigou-admin-service | /api/admin/** | StripPrefix=1 |

### 9.3 架构特点
- 基于Spring Cloud + Spring Cloud Alibaba的微服务架构
- 服务间通过Feign进行通信
- 统一的API网关进行请求管理和限流
- 服务注册与发现使用Nacos
- 配置中心使用Nacos，支持动态配置更新
- 支持分布式事务处理
- 完善的服务监控与日志系统
- 使用Redis实现缓存和限流
- 集成Elasticsearch实现高效商品搜索
- 支持微信小程序第三方登录

## 10. 开发与调试
| 配置项 | 详细说明 |
|--------|----------|
| Spring Boot DevTools | 启用热重启，排除目录：WEB-INF/**, static/**, public/**, templates/** |
| Livereload | 启用Livereload服务，端口：35729 |
| 模板缓存 | 禁用Thymeleaf模板缓存 |
| 静态资源缓存 | 禁用静态资源缓存，缓存周期：0 |
| 日志级别 | root: info, com.anqigou: debug，网关模块开启trace级别日志 |

## 11. 构建与部署
| 工具 | 版本 | 配置 |
|------|------|------|
| Maven | 3.x | 使用spring-boot-maven-plugin 2.7.14进行打包，支持repackage目标 |
| Docker | - | 支持容器化部署 |

## 12. 技术亮点
1. **微服务架构**：基于Spring Cloud Alibaba生态，实现服务的高可用和弹性扩展
2. **分布式缓存**：使用Redis实现缓存、会话管理和限流
3. **搜索引擎集成**：集成Elasticsearch实现高效的商品搜索
4. **API网关**：实现统一的请求路由、限流、熔断等功能
5. **微信小程序支持**：集成微信小程序SDK，支持第三方登录
6. **热部署开发**：配置Spring Boot DevTools，提高开发效率
7. **完善的日志系统**：分层日志级别配置，便于问题排查
8. **连接池优化**：使用Druid连接池，优化数据库连接管理

## 13. 技术选型说明
- **为什么选择Spring Cloud Alibaba？**
  - 提供了完整的微服务解决方案
  - Nacos作为注册中心和配置中心，功能强大且易于部署
  - 与Spring Boot和Spring Cloud深度集成
  - 社区活跃，文档完善

- **为什么选择MyBatis-Plus？**
  - 简化CRUD操作，减少重复代码
  - 支持Lambda表达式，类型安全
  - 提供丰富的插件机制，如分页、性能分析等
  - 与MyBatis兼容，易于迁移

- **为什么选择Redis？**
  - 高性能的内存数据库，适合作为缓存
  - 支持多种数据结构，如字符串、哈希、列表、集合等
  - 支持发布订阅模式，适合实现消息通知
  - 支持持久化，数据可靠性高

- **为什么选择Elasticsearch？**
  - 分布式搜索引擎，支持高并发、高可用
  - 全文检索功能强大，适合商品搜索场景
  - 支持实时数据索引和搜索
  - 提供丰富的API，易于集成

## 14. 未来技术规划
1. 引入Sentinel实现服务熔断和降级
2. 集成Seata实现分布式事务
3. 引入SkyWalking实现分布式链路追踪
4. 支持Kubernetes部署
5. 引入Prometheus + Grafana实现监控告警
6. 支持灰度发布
7. 引入消息队列，如RocketMQ或Kafka
