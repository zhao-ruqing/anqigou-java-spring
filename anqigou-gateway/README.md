# anqigou-gateway

网关服务，端口 `8080`。整个系统的唯一入口，所有前端请求先到这里，再由它转发给后面的各个服务。

## 技术栈

Spring Cloud Gateway + Nacos + JWT + Redis + Lombok

## 文件夹都干了啥

```
com/anqigou/gateway/
│
├── GatewayApplication.java    # 启动类
│
├── config/                    # 配置
│   ├── CorsConfig.java        #   跨域配置，允许前端 localhost:5173/5174 访问
│   └── GatewayRouteConfig.java#   限流配置，按 IP 限流（每秒10次，突发20次）
│
└── filter/                    # 过滤器 —— 请求进来先过这些"关卡"
    ├── JwtAuthenticationFilter.java  #   JWT 校验，从请求头拿 token，解析出 userId 传给下游
    ├── JwtAuthFilter.java            #   另一个 JWT 过滤器（功能重叠，待合并）
    ├── RequestLoggingFilter.java     #   请求日志，记录每个请求的方法、路径、耗时
    └── ErrorHandlingFilter.java      #   异常兜底，出错了统一返回 JSON 错误信息
```

## 路由规则

前端请求 `/api/xxx` → 网关根据路径前缀转发到对应服务：

| 路径前缀 | 转发到 | 端口 |
|---------|--------|------|
| `/api/auth/**`, `/api/user/**`, `/api/feedback/**` | 用户服务 | 8081 |
| `/api/product/**` | 商品服务 | 8082 |
| `/api/order/**`, `/api/cart/**` | 订单服务 | 8083 |
| `/api/payment/**` | 支付服务 | 8084 |
| `/api/logistics/**`, `/api/subscribe/**` | 物流服务 | 8085 |
| `/api/seller/**` | 商家服务 | 8086 |
| `/api/admin/**` | 管理后台 | 8087 |

## 它做了什么

简单说就是"门卫+快递员"：先检查你有没有登录（JWT 校验），登录了就放行并把你的 userId 带给后面的服务，然后根据你访问的路径转发到正确的服务去处理。
