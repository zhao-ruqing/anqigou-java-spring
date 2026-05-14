# anqigou-common

公共模块，其他所有服务都依赖它。把大家都要用的东西抽到这里，避免每个服务重复写。

## 技术栈

Spring Boot + RabbitMQ + Redis + Elasticsearch + JWT + Swagger

## 文件夹都干了啥

```
com/anqigou/common/
│
├── config/                # 通用配置 —— 各服务公用的基础设施配置
│   ├── RabbitMQConfig.java       #   RabbitMQ 消息队列配置
│   ├── RedisConfig.java          #   Redis 缓存配置
│   ├── ElasticsearchConfig.java  #   ES 搜索配置
│   ├── SwaggerConfig.java        #   接口文档配置
│   ├── WebCorsConfig.java        #   跨域配置
│   └── WebMvcConfig.java         #   Spring MVC 通用配置
│
├── constant/
│   └── AppConstants.java  # 全局常量，比如订单状态、支付状态这些枚举值
│
├── event/                 # 事件对象 —— MQ 消息的载体
│   ├── OrderEvent.java    #   订单事件（下单、支付、发货、完成等）
│   └── PaymentEvent.java  #   支付事件
│
├── exception/
│   └── BizException.java  # 自定义业务异常，业务出错时抛出，统一格式
│
├── interceptor/
│   └── UserIdInterceptor.java  # 用户ID拦截器，从请求头提取 userId
│
├── response/
│   └── ApiResponse.java   # 统一返回格式，所有接口都用这个包装返回结果
│
└── util/                  # 工具类
    ├── JwtUtil.java           #   JWT 生成和校验
    ├── RedisUtil.java         #   Redis 操作封装
    ├── ElasticsearchUtil.java #   ES 操作封装
    └── StringUtil.java        #   字符串工具
```

## 它做了什么

别的服务是"各管一摊"，这个模块是"公共仓库"。统一的返回格式、统一的异常处理、统一的 JWT 校验、MQ/Redis/ES 的配置，全放这里，哪个服务要用直接引就行，不用自己再写一遍。
