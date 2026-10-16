# 架构说明

单体仓库：`backend`（Spring Boot）+ `campus-trade/miniprogram`（原生微信小程序）。已实现：学号邮箱注册、学号密码登录、资料、校园认证提交和管理员审核、通知、上传、商品、订单、模拟支付、聊天、举报和管理端动作。表结构见 [../sql/README.md](../sql/README.md)。微信登录仍等 AppID。给前端的参数见 [STATUE.md](./STATUE.md) 和 [base-API.md](./base-API.md)。

当前仓库是底座。AI 模块（大模型填表、建议价和议价）和书籍视觉分档都不在底座接口里。`ai/` 包留给 AI 模块，现在是空的。书籍分档另起模块：视觉语言模型按分档规则定档，预价由服务端按档计算。包名、路径和分档细则等文档整理后再定。

## 目录

```
backend/src/main/java/com/campus/ershou/
  CampusErshouApplication.java
  HealthController.java          GET /api/health
  auth/     注册登录、校园认证提交、管理员审核
  user/     资料、统计、公开主页（已实现）
  common/file/  上传（已实现）
  goods/    商品列表、发布、收藏、浏览记录（已实现）
  order/    下单与库存
  pay/      模拟支付
  im/       即时通讯：会话、消息；通知列表和已读（已实现）
  ai/       AI 模块：定价 / 议价（底座不实现）
  common/   鉴权、统一响应、元数据、分类、楼栋、上传
  admin/    管理员登录和待办计数；审核接口在 auth 包
sql/
  README.md                     表说明
  schema.sql                    建表
  seed.sql                      区域 / 楼栋 / 分类初始数据
  start-local-mysql.sh          本机 3307 MySQL
campus-trade/miniprogram/      原生微信小程序
  pages/                        页面（app.json 登记）
  utils/config.js               后端地址（默认 https://simpleyi.site）
```

已用到的表：`areas`、`buildings`、`categories`、`users`、`stored_files`、`campus_verifications`、`goods`、`goods_images`、`favorites`、`admins`、`notifications`、`audit_logs`。验证码、JWT 黑名单和浏览记录在 Redis，不建表。

实现某个模块时，在对应包新建 `controller` / `service` / `mapper`。改表先改 `sql/`，再补 Flyway。前端在 `campus-trade/miniprogram/pages/` 加页面，并写入 `app.json`。字段、错误码和状态以 `base-API.md` 为准，这里只留必须一起遵守的几条。

## 写代码时的约定

1. 下单只收 `goodsId` 和 `quantity`，金额按库里的商品价格计算。请求体里多出来的字段直接拒绝。
2. 扣库存用一条 SQL：`UPDATE goods SET stock = stock - n WHERE id = ? AND stock >= n`，影响行数为 0 就失败。进行中的订单再加唯一约束兜底。
3. 订单状态：`PENDING_PAY → PAID → COMPLETED`，或 `PENDING_PAY → CANCELLED`。已付款后可以进入 `DISPUTED`（申诉中）。禁止客户端传 `paid=true` 或直接改状态。
4. 查/改订单、商品、聊天时核对当前登录用户，不能只靠隐藏按钮。
5. MyBatis 只用 `#{}`；不要用 `rich-text` 渲染用户输入。
6. 不建 `user_balance`。支付走模拟网关回调，要验签、防重放、回查金额。
7. 密钥和微信 AppID 只放本机，不要提交。`campus-trade/miniprogram/project.config.json` 的 `appid` 在没有正式 AppID 时保持 `touristappid`。
8. 学号不存明文，只存 HMAC 和脱敏串。密码用 BCrypt。改表先改 `sql/`。
