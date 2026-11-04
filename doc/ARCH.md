# 架构说明

单体仓库：`backend`（Spring Boot）+ `campus-trade/miniprogram`（原生微信小程序）。

接口以 [base-API.md](./base-API.md) v1.6 为准，需求以 [D3-第一组-软件需求规格说明书-V1.6.docx](./D3-第一组-软件需求规格说明书-V1.6.docx) 为准。当前 Java 代码和 [STATUE.md](./STATUE.md) 里的线上包还是旧实现：学号邮箱注册、学号密码登录、收藏、浏览记录、评价、按商品聊天。改这些模块时直接改成 v1.6，不要在旧字段上继续加功能。

v1.6 要做的是：微信登录，校园认证提交姓名、学号、学院、楼栋和校园卡，手填发布和 AI 草稿，建议价，下单与沙箱支付，订单内文字聊天，双方确认面交，待面交申诉，商品详情举报。不做收藏、浏览记录、评价、议价话术、模拟发票和封号。

表结构见 [../sql/README.md](../sql/README.md)。现有 Flyway 停在 `V5`，和 v1.6 的表还不一致，改表时新增迁移，不要改已经执行过的 `V1`–`V5`。

## 目录

```
backend/src/main/java/com/campus/ershou/
  CampusErshouApplication.java
  HealthController.java          GET /api/health
  auth/     目标：微信登录、校园认证、管理员审核
  user/     资料、统计、公开主页
  common/file/  上传
  goods/    商品列表、发布、同义叫法。收藏和浏览记录要卸掉
  order/    下单、快照、面交确认、申诉、订单聊天
  pay/      模拟支付。状态改为待支付、已支付、已取消、模拟退回
  im/       通知和 WebSocket。会话改挂在订单上
  ai/       草稿和建议价。失败时建议价返回「暂无建议价」，不做议价话术
  common/   鉴权、统一响应、元数据、分类、楼栋
  admin/    管理员登录和待办；审核接口在 auth 包
sql/
  README.md                     表说明
  schema.sql                    当前库的建表（旧结构）
  seed.sql                      区域 / 楼栋 / 分类初始数据
  start-local-mysql.sh          本机 3307 MySQL
campus-trade/miniprogram/      课程使用的原生微信小程序
  pages/                        页面（app.json 登记）
  utils/config.js               后端地址（默认 https://simpleyi.site）
```

当前库里已经有：`areas`、`buildings`、`categories`、`users`、`stored_files`、`campus_verifications`、`goods`、`goods_images`、`favorites`、`admins`、`notifications`、`audit_logs`，以及 `V5` 的订单、支付、会话、申诉、评价、举报。`favorites` 和评价表是旧功能留下的。验证码和浏览记录在 Redis，v1.6 不再使用。

实现时在对应包改 `controller` / `service` / `mapper`。改表先改 `sql/`，再补新的 Flyway 文件。前端在 `campus-trade/miniprogram/pages/` 改页面，并写入 `app.json`。

## 写代码时的约定

1. 下单只收 `goodsId` 和 `quantity`（数量只能是 1）。成交价按库里的商品标价写入，并保存商品快照。请求体里多出来的字段直接拒绝，订单、支付和商品状态都不变。
2. 同一商品同时只有一笔未结束订单。用短锁，加上 `UPDATE ... WHERE status = 'ON_SALE'`，再用唯一约束兜底。未支付取消或超时释放后商品回到在售。
3. 订单状态：`PENDING_PAY`（已预约）→ `PAID`（待面交）→ `COMPLETED`，或未支付时 `PENDING_PAY` → `CANCELLED`。待面交可以进入 `DISPUTED`。已支付后买卖双方都不能取消。进入待面交只能靠核验通过的支付通知。
4. 商品交易状态要能区分在售、已预约、待面交、申诉中、已售出。页面对已预约、待面交、申诉中统一显示「交易进行中」。
5. 聊天按订单。下单前和待支付不能发。待面交和申诉中只有买家和卖家能发文字。结束后只能看。管理员只在处理该申诉时能读，不能代发。
6. 查订单、商品、聊天时核对当前登录用户。非当事人的响应里不带订单正文和聊天内容。
7. MyBatis 只用 `#{}`。小程序不用 `rich-text` 渲染用户输入。
8. 不建 `user_balance`。支付走模拟网关通知，要验签、防重放、回查金额。取消或超时后的成功通知记模拟退回，不能把订单改回待面交。
9. 密钥和微信 AppID 只放本机或服务器配置，不要提交。`campus-trade/miniprogram/project.config.json` 的 `appid` 在没有正式 AppID 时保持 `touristappid`。
10. 学号在校园认证时采集，不存明文，只存 HMAC 和脱敏串。学生登录不设密码。管理员密码用 BCrypt。
