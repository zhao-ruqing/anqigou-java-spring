# anqigou-seller-service

商家服务，端口 `8086`。管"卖家"的事：入驻、资料管理、看订单、发货、看数据。

## 技术栈

Spring Boot + MyBatis-Plus + MySQL + Redis + Nacos

## 文件夹都干了啥

```
com/anqigou/seller/
│
├── controller/            # 控制器 —— 接请求的
│
├── service/               # 业务层 —— 真正干活的地方
│   └── impl/
│       └── SellerServiceImpl.java   #   入驻申请、资料管理、订单查看、发货、统计
│
├── entity/                # 实体类 —— 商家表 seller
│                          #   字段：店铺名、执照号、执照图片、审核状态、信用分等
│
├── mapper/                # 数据库操作 —— 增删改查
│
└── dto/                   # 数据传递用的临时对象
```

## API 端点（全部前缀 `/seller`）

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/seller/register` | 商家入驻申请 |
| GET | `/seller/{id}/info` | 查商家信息（审核通过才能看到） |
| PUT | `/seller/{id}/info` | 修改商家信息 |
| GET | `/seller/{id}/orders` | 商家看自己的订单 |
| POST | `/seller/orders/{orderId}/ship` | 商家发货 |
| GET | `/seller/{id}/statistics` | 商家数据统计（销售额、订单数等） |

## 它做了什么

简单说就是"管卖家的"。卖家注册入驻 → 等待管理员审核 → 审核通过后可以上架商品、看订单、发货、看销售数据。审核不通过的话，资料对外不可见。
