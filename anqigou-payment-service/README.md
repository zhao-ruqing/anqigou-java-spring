# anqigou-payment-service

支付服务，端口 `8084`。管"付钱"的事：微信支付、支付宝、退款。

## 技术栈

Spring Boot + MyBatis-Plus + MySQL + Redis + RabbitMQ + Nacos + OpenFeign

## 文件夹都干了啥

```
com/anqigou/payment/
│
├── controller/            # 控制器 —— 接请求的
│
├── service/               # 业务层 —— 真正干活的地方
│   └── impl/
│       └── PaymentServiceImpl.java  #   支付、退款、模拟支付
│
├── entity/                # 实体类 —— 支付表 payment
│
├── dto/                   # 数据传递用的临时对象
│                          #   支付请求、模拟支付请求/响应
│
├── mapper/                # 数据库操作 —— 增删改查
│
├── client/                # 远程调用 —— 找订单服务
│   ├── OrderServiceClient.java       #   调订单服务（查订单、更新支付状态、发货）
│   └── LogisticsServiceClient.java   #   调物流服务（已定义，暂未使用）
│
└── config/                # 配置
    ├── WechatPayConfig.java  #   微信支付配置
    └── AlipayConfig.java     #   支付宝配置
```

## API 端点（全部前缀 `/payment`）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/payment/wechat/prepare` | 微信预支付（待接入真实API） |
| POST | `/payment/wechat/notify` | 微信支付回调 |
| POST | `/payment/alipay/prepare` | 支付宝支付（待接入真实API） |
| POST | `/payment/alipay/notify` | 支付宝回调 |
| GET | `/payment/{orderId}/status` | 查支付状态 |
| POST | `/payment/{orderId}/refund` | 退款 |
| POST | `/payment/mock/pay` | 模拟支付（测试用） |

## 它做了什么

简单说就是"管付钱的"。用户下单后调这里发起支付，目前微信/支付宝的真实对接还是 TODO 状态。**模拟支付**是唯一完整实现的功能：调它 → 创建支付记录 → 通知订单服务"已付款" → 隔 30 秒自动模拟发货（随机快递公司+单号），方便开发测试。
