# anqigou-user-service

用户服务，端口 `8081`。管"人"的事：注册、登录、地址、收藏、反馈。

## 技术栈

Spring Boot + MyBatis-Plus + MySQL + Redis + Nacos + Spring Security + JWT

## 文件夹都干了啥

```
com/anqigou/user/
│
├── controller/            # 控制器 —— 接请求的
│
├── service/               # 业务层 —— 真正干活的地方
│   └── impl/
│
├── entity/                # 实体类 —— 数据库表和 Java 类的对应关系
│                          #   用户表、地址表、收藏表、反馈表、设备表
│
├── dto/                   # 数据传递用的临时对象
│                          #   注册/登录请求、地址请求、用户信息响应等
│
├── mapper/                # 数据库操作 —— 增删改查
│
├── config/                # 配置
│   ├── SecurityConfig.java  #   Spring Security 安全配置
│   └── WechatConfig.java    #   微信小程序配置（appid、secret）
│
├── client/                # 远程调用
│   └── ProductServiceClient.java  #  调商品服务拿商品详情（收藏列表用）
│
└── exception/             # 全局异常处理
```

## API 端点

### 登录注册 `/auth`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/auth/send-code` | 发短信验证码 |
| POST | `/auth/register` | 密码注册 |
| POST | `/auth/login` | 密码登录 |
| POST | `/auth/login-with-code` | 验证码登录 |
| POST | `/auth/wechat-login` | 微信小程序登录 |
| GET | `/auth/user-info` | 查个人信息 |
| PUT | `/auth/user-info` | 改个人信息 |

### 收货地址 `/user/address`
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/address/list` | 地址列表 |
| POST | `/user/address` | 新增地址 |
| PUT | `/user/address/{id}` | 修改地址 |
| DELETE | `/user/address/{id}` | 删除地址 |
| POST | `/user/address/{id}/set-default` | 设为默认地址 |

### 收藏 `/user/favorite`
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/user/favorite/add/{productId}` | 收藏商品 |
| DELETE | `/user/favorite/cancel/{productId}` | 取消收藏 |
| GET | `/user/favorite/list` | 收藏列表 |
| GET | `/user/favorite/check/{productId}` | 查是否已收藏 |

### 设备管理 `/device`
管理用户登录设备，支持远程踢下线。

### 意见反馈 `/feedback`
用户提交反馈、查看反馈列表。
