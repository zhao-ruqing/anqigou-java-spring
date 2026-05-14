# anqigou-admin-service

管理后台服务，端口 `8087`。管"平台"的事：审核商家、审核商品、看数据、管用户。

## 技术栈

Spring Boot + MyBatis-Plus + MySQL + Redis + Nacos

## 文件夹都干了啥

```
com/anqigou/admin/
│
├── controller/            # 控制器 —— 接请求的
│
├── service/               # 业务层 —— 真正干活的地方（目前大部分是 TODO 占位）
│   └── impl/
│
├── entity/                # 实体类 —— 管理员表 admin
│                          #   字段：用户名、密码、角色（超管/商家审核/商品审核/数据分析）
│
├── mapper/                # 数据库操作 —— 增删改查
│
└── dto/                   # 数据传递用的临时对象
```

## API 端点（全部前缀 `/admin`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/admin/sellers/pending` | 待审核商家列表 |
| POST | `/admin/sellers/{id}/approve` | 审核商家（通过/拒绝） |
| GET | `/admin/products/pending` | 待审核商品列表 |
| POST | `/admin/products/{id}/approve` | 审核商品（通过/拒绝） |
| GET | `/admin/statistics` | 平台数据统计（用户数、商家数、订单数、销售额等） |
| GET | `/admin/users` | 用户列表 |
| POST | `/admin/feedback/{id}/reply` | 回复用户反馈 |

## 它做了什么

简单说就是"管平台的"。平台管理员登录后：审核新入驻的商家、审核商家新上架的商品、看整个平台的运营数据、管理用户、回复用户反馈。目前大部分业务逻辑还是 TODO 占位，骨架已搭好。
