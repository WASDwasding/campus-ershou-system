# 架构说明

单体仓库：`backend`（Spring Boot）+ `frontend`（Vue3）。当前只保留目录骨架和健康检查，业务在对应包里从零写。

## 目录

```
backend/src/main/java/com/campus/ershou/
  CampusErshouApplication.java
  HealthController.java          GET /api/health
  auth/     注册登录
  user/     用户资料
  goods/    商品
  order/    下单与库存
  pay/      模拟支付
  im/       即时通讯
  ai/       定价 / 议价
  common/   鉴权、OSS、统一响应
sql/init.sql
frontend/                       Vue3 单页
```

实现某个模块时，在对应包新建 `controller` / `service` / `mapper`，并在 `backend/pom.xml` 按需加入 MyBatis、Security、Redis、OSS 等依赖。

## 写代码时的约定

1. 下单只收 `goodsId` 和 `quantity`，金额按库里的商品价格计算。
2. 扣库存用一条 SQL：`UPDATE goods SET stock = stock - n WHERE id = ? AND stock >= n`，影响行数为 0 就失败。
3. 订单状态：`PENDING_PAY → PAID → COMPLETED`，或 `PENDING_PAY → CANCELLED`。禁止客户端传 `paid=true`。
4. 查/改订单、商品、聊天时核对当前登录用户，不能只靠隐藏按钮。
5. MyBatis 只用 `#{}`；不要用 `v-html` 渲染用户输入。
6. 密钥只写 `application-local.yml`，不要提交。
