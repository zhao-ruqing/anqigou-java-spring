# 安琦购电商平台后端

## 项目简介
安琦购是一个基于微信小程序的全链路电商平台，采用微服务架构设计，提供完整的电商解决方案，包括用户管理、商品管理、订单管理、支付管理、物流管理等核心功能。

## 技术栈

### 基础框架
- Spring Boot 2.7.14
- Spring Cloud 2021.0.5
- Spring Cloud Alibaba 2021.0.4.0
- Java 11

### 数据库与缓存
- MySQL 8.0.33
- Redis 2.7.14
- Elasticsearch 7.17.9

### 数据访问
- MyBatis-Plus 3.5.3
- Druid 1.2.18

### 微服务组件
- Nacos 2.1.0 (服务注册与配置中心)
- Spring Cloud Gateway (API网关)
- Feign (服务间通信)

### 安全认证
- JWT 0.11.5
- 微信小程序登录

## 模块结构

| 模块名称 | 端口 | 功能描述 |
|----------|------|----------|
| **anqigou-common** | - | 公共模块，包含工具类、常量、配置等 |
| **anqigou-gateway** | 8080 | API网关，负责请求路由、限流、认证等 |
| **anqigou-user-service** | 8081 | 用户服务，处理用户注册、登录、信息管理等 |
| **anqigou-product-service** | 8082 | 产品服务，处理商品CRUD、分类、搜索等 |
| **anqigou-order-service** | 8083 | 订单服务，处理订单创建、支付、查询等 |
| **anqigou-payment-service** | 8084 | 支付服务，处理支付回调、退款等 |
| **anqigou-logistics-service** | 8085 | 物流服务，处理物流信息查询、更新等 |
| **anqigou-seller-service** | 8086 | 卖家服务，处理店铺管理、商品发布等 |
| **anqigou-admin-service** | 8087 | 管理员服务，处理后台管理、统计报表等 |

## 快速开始

### 环境要求
- JDK 11
- Maven 3.x
- MySQL 8.0+
- Redis 6.0+
- Elasticsearch 7.17.9
- Nacos 2.1.0

### 数据库配置
1. 执行 `database.sql` 创建数据库和表
2. 执行 `test_data.sql` 导入测试数据（可选）

### 启动步骤
1. 启动依赖服务：Nacos、MySQL、Redis、Elasticsearch
2. 编译项目：
   ```bash
   mvn clean install
   ```
3. 启动所有服务：
   ```bash
   start-all-services.bat
   ```
   或逐个启动服务：
   ```bash
   # 启动网关
   cd anqigou-gateway
   mvn spring-boot:run
   
   # 启动用户服务
   cd ../anqigou-user-service
   mvn spring-boot:run
   
   # 其他服务以此类推
   ```

## 项目架构

### 架构特点
- 基于Spring Cloud + Spring Cloud Alibaba的微服务架构
- 服务间通过Feign进行通信
- 统一的API网关进行请求管理和限流
- 服务注册与发现使用Nacos
- 配置中心使用Nacos，支持动态配置更新
- 使用Redis实现缓存和限流
- 集成Elasticsearch实现高效商品搜索
- 支持微信小程序第三方登录

### 网关路由
| 路由ID | 服务名称 | 路径规则 |
|--------|----------|----------|
| user-service | anqigou-user-service | /api/auth/**, /api/user/**, /api/feedback/** |
| product-service | anqigou-product-service | /api/product/** |
| order-service | anqigou-order-service | /api/order/**, /api/cart/** |
| payment-service | anqigou-payment-service | /api/payment/** |
| logistics-service | anqigou-logistics-service | /api/logistics/**, /api/subscribe/** |
| seller-service | anqigou-seller-service | /api/seller/** |
| admin-service | anqigou-admin-service | /api/admin/** |

## 核心功能

### 用户管理
- 用户注册、登录、注销
- 微信小程序登录
- 用户信息管理
- 地址管理
- 收藏夹管理

### 商品管理
- 商品分类管理
- 商品CRUD操作
- 商品搜索（基于Elasticsearch）
- 商品SKU管理

### 订单管理
- 购物车管理
- 订单创建、支付、取消
- 订单查询、分页
- 售后申请管理

### 支付管理
- 支付回调处理
- 退款管理
- 支付记录查询

### 物流管理
- 物流信息查询
- 物流轨迹更新
- 物流评价

### 管理员管理
- 后台用户管理
- 统计报表
- 系统配置

## API文档

项目集成了Swagger，启动服务后可以访问以下地址查看API文档：
- 网关服务：http://localhost:8080/swagger-ui.html
- 各微服务：http://localhost:{port}/swagger-ui.html

详细API信息请参考 `API_INFO.md` 文件。

## 开发指南

### 代码规范
- 遵循Java编码规范
- 使用Lombok简化代码
- 统一的异常处理机制
- 统一的API响应格式

### 日志配置
- 日志级别：root: info, com.anqigou: debug
- 日志文件：各服务目录下的 `logs.txt`
- 查看日志命令：
  ```bash
  view-logs.bat
  ```

### 开发流程
1. 从develop分支创建功能分支
2. 开发完成后提交代码
3. 编写单元测试
4. 提交MR到develop分支

## 许可证

[MIT License](LICENSE)

## 联系方式

如有问题或建议，欢迎提交Issue或联系项目维护者。