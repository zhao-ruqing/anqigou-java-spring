# anqigou-product-service

商品服务，端口 `8082`。管"货"的事：商品列表、详情、搜索、分类、库存。

## 技术栈

Spring Boot + MyBatis-Plus + MySQL + Redis + Elasticsearch + Nacos

## 文件夹都干了啥

```
com/anqigou/product/
│
├── controller/            # 控制器 —— 接请求的
│
├── service/               # 业务层 —— 真正干活的地方
│   └── impl/
│       ├── ProductServiceImpl.java   #   商品查询、搜索、分类、库存扣减/归还
│       └── ProductSyncService.java   #   启动时把商品数据同步到 ES
│
├── entity/                # 实体类 —— 数据库表和 Java 类的对应关系
│                          #   商品表、SKU表、分类表、评价表
│
├── dto/                   # 数据传递用的临时对象
│                          #   商品详情、商品列表项、SKU信息、库存信息
│
├── mapper/                # 数据库操作 —— 增删改查
│
└── document/              # ES 文档对象
    └── ProductDocument.java  #   ES 里存的商品数据结构（用于全文搜索）
```

## API 端点（全部前缀 `/product`）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/product/list` | 商品列表，支持分类、关键词、排序（价格/热度） |
| GET | `/product/search` | 全文搜索（优先走 ES，ES 挂了走 MySQL 模糊匹配） |
| GET | `/product/{id}` | 商品详情（含 SKU、图片） |
| GET | `/product/hot` | 热销商品 |
| GET | `/product/recommended` | 推荐商品 |
| GET | `/product/categories` | 全部分类 |
| GET | `/product/categories/first-level` | 一级分类 |
| GET | `/product/categories/sub?parentId=` | 子分类 |
| GET | `/product/{id}/reviews` | 商品评价列表 |
| GET | `/product/{id}/review-stats` | 评价统计（1-5星各多少条） |
| GET | `/product/sku/batch-stock?skuIds=` | 批量查 SKU 库存（订单服务用） |
| GET | `/product/sku/{skuId}/deduct-stock?quantity=` | 扣库存 |
| GET | `/product/sku/{skuId}/return-stock?quantity=` | 归还库存（取消订单时） |

## 它做了什么

简单说就是"管商品的"：用户逛商城看到的所有东西（列表、详情、搜索、分类）都从这出，下单时扣库存、取消时还库存也是它。搜索默认走 Elasticsearch，ES 不可用时自动降级到 MySQL 模糊搜索。

## 注意

金额用**分**存（Long 类型），避免小数精度问题。
