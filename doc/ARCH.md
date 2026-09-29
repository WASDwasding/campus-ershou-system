# 架构说明

单体仓库：`backend`（Spring Boot）+ `frontend`（uni-app，编译为微信小程序）。当前只保留目录骨架和健康检查，业务按 [base-API.md](./base-API.md) 从零实现。

项目 A 的大模型填表和议价、项目 B 的成色与仲裁都不在底座接口里。`ai/` 包留给项目 A，现在是空的。

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
  ai/       项目 A：定价 / 议价（底座先不实现）
  common/   鉴权、文件、统一响应
sql/                            数据库（表结构、脚本、说明）
frontend/                       uni-app（微信小程序）
  src/pages/                    小程序页面（pages.json 登记）
  src/config.js                 后端地址（默认 https://simpleyi.site）
```

实现某个模块时，在对应包新建 `controller` / `service` / `mapper`，并在 `backend/pom.xml` 按需加入 MyBatis、Security、Redis 等依赖。前端在 `frontend/src/pages/` 加页面，并写入 `pages.json`。字段、错误码和状态以 `base-API.md` 为准，这里只留必须一起遵守的几条。

## 写代码时的约定

1. 下单只收 `goodsId` 和 `quantity`，金额按库里的商品价格计算。请求体里多出来的字段直接拒绝。
2. 扣库存用一条 SQL：`UPDATE goods SET stock = stock - n WHERE id = ? AND stock >= n`，影响行数为 0 就失败。进行中的订单再加唯一约束兜底。
3. 订单状态：`PENDING_PAY → PAID → COMPLETED`，或 `PENDING_PAY → CANCELLED`。已付款后可以进入 `DISPUTED`（申诉中）。禁止客户端传 `paid=true` 或直接改状态。
4. 查/改订单、商品、聊天时核对当前登录用户，不能只靠隐藏按钮。
5. MyBatis 只用 `#{}`；不要用 `rich-text` 渲染用户输入。
6. 不建 `user_balance`。支付走模拟网关回调，要验签、防重放、回查金额。
7. 密钥和微信 AppID 只放本机，不要提交。`manifest.json` 里的 `mp-weixin.appid` 保持空字符串，或只在本机填写且不要推进仓库。
