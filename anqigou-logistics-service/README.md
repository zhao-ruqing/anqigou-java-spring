# anqigou-logistics-service

物流服务，端口 `8085`。管"送货"的事：发货、查物流轨迹、确认收货、评价。

## 技术栈

Spring Boot + MyBatis-Plus + MySQL + Redis + Nacos + OpenFeign + 快递100 API

## 文件夹都干了啥

```
com/anqigou/logistics/
│
├── controller/            # 控制器 —— 接请求的
│   ├── LogisticsController.java      #   发货、查物流、确认收货、评价
│   ├── SubscribeController.java      #   微信订阅消息
│   └── SubscribeTestController.java  #   订阅消息测试接口
│
├── service/               # 业务层 —— 真正干活的地方
│   └── impl/
│
├── entity/                # 实体类 —— 物流表、物流轨迹表、物流评价表
│
├── dto/                   # 数据传递用的临时对象
│                          #   物流详情、物流轨迹、订单信息、评价请求
│
├── mapper/                # 数据库操作 —— 增删改查
│
├── client/                # 远程调用 —— 找其他服务
│   ├── OrderServiceClient.java  #   调订单服务拿订单信息
│   └── UserServiceClient.java   #   调用户服务拿 openId（发微信通知用）
│
├── config/                # 配置
│   ├── Kuaidi100Config.java       #   快递100 API 配置
│   ├── RestTemplateConfig.java    #   HTTP 请求工具配置
│   └── WechatSubscribeConfig.java #   微信订阅消息配置
│
└── util/                  # 工具类
    ├── Kuaidi100Client.java          #   快递100 API 调用封装
    └── LogisticsTrackGenerator.java  #   物流轨迹模拟生成
```

## API 端点

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/logistics/ship` | 发货（订单服务调用） |
| GET | `/logistics/order/{orderId}` | 查物流详情 |
| GET | `/logistics/{id}/tracks` | 查物流轨迹 |
| POST | `/logistics/confirm-receipt/{orderId}` | 确认收货 |
| POST | `/logistics/{id}/evaluate` | 评价物流 |
| POST | `/subscribe/save` | 保存微信订阅状态 |

## 它做了什么

简单说就是"管快递的"。商家发货 → 创建物流记录 → 对接快递100查实时轨迹 → 用户确认收货 → 用户评价配送。还集成了微信订阅消息，发货时给用户发通知。
