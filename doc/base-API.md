# 校园二手交易平台 · Base API

v0.4 设计稿（2026-09-30）。这是后续分叉前的公共底座。技术栈：Spring Boot 3.4 / Java 17、MySQL 8、Redis、原生微信小程序（`campus-trade/miniprogram`）。

已实现范围与 [STATUE.md](./STATUE.md) 一致：健康检查、注册登录、资料、统计角标、公开主页、校园认证提交和管理员审核、上传、商品、通知、订单、模拟支付、聊天、举报、管理端动作。微信登录仍等 AppID。AI 模块和书籍视觉分档只加接口和可选字段，不改这里的字段含义、状态机和错误码。改底座先改本文件。

## 1. 范围

**包含：** 学号 + 邮箱验证码注册、学号 + 密码登录；校园身份人工审核；资料、公开主页、评价；商品发布/改价/上下架/搜索/就近/收藏/浏览记录；下单不超卖、订单状态机、面交、申诉入口；模拟支付（验签、防重放、回查金额、幂等、模拟退款）；站内聊天与 WebSocket；通知、举报；管理端（审核、封禁、下架、申诉简版、举报、审计）。

**不包含：**

| 内容 | 去向 |
| --- | --- |
| 大模型填表、建议价、议价话术、报价令牌 | AI 模块，路径 `/api/ai/**` |
| 校园实体书的视觉分档和预价 | 书籍视觉分档。视觉语言模型按分档规则定档，服务端按档计算预价。预价不写入订单，也不生成议价话术。路径、字段和分档细则待整理 |
| 自然语言搜索、求购、候补、组合发布、校园专题、订阅消息、学校统一身份接口 | 不做 |
| 价格为 0 的赠送 | 不做。价格最低 1 分，订单都走支付 |
| 真实支付、站内钱包、`user_balance` | 不做 |

已付款后取消，订单仍是 `CANCELLED`，退款记在支付单上。

## 2. 环境

| 环境 | 谁用 | 地址 | 数据 |
| --- | --- | --- | --- |
| server | 小程序、Apifox、展示 | `https://simpleyi.site`，WebSocket `wss://simpleyi.site/ws/im` | 库 `ershou`；Redis 前缀 `campus:` |
| local | 只给后端在本机写接口 | `http://127.0.0.1:8080` | 本机库 `campus_ershou`。推荐 `sql/start-local-mysql.sh`（端口 3307）。前端不要连 |

`campus-trade/miniprogram/utils/config.js` 默认是服务器地址。后端临时改成 `127.0.0.1` 时不要提交。开发者工具勾选「不校验合法域名」。体验版再把 `simpleyi.site` 配进 request、socket、uploadFile、downloadFile。

服务器：Spring Boot 只听 `127.0.0.1:8080`，Nginx 转发 `/api/`、`/ws/`、`/files/public/`。部署是替换 `/opt/campus-ershou/campus-ershou.jar` 后 `sudo systemctl restart campus-ershou`。库账号只允许本机连接。原来的 `simpleyi` 库不使用。只信 Nginx 写入的 `X-Real-IP`，不信客户端自己带的 `X-Forwarded-For`。

密钥放服务器或本机的 `application-local.yml`，不进仓库：数据库、Redis、`app.jwt.secret`（≥32 字节）、`app.student-no.hmac-key`、以及后续的 `wx.app-id` / `wx.app-secret`、`pay.mock.secret`、`file.sign-key`、`admin.init-password`。

第一批表（`areas`、`buildings`、`categories`、`users`）见 [../sql/README.md](../sql/README.md)。验证码和 JWT 黑名单在 Redis，前缀 `campus:`。

| 开关 | local | server |
| --- | --- | --- |
| `POST /api/auth/dev-login` | 开 | 404 |
| 注册响应里的 `debugCode` | 仅 mail 未启用时 | 禁止 |
| OpenAPI 页面 | 开 | 关 |
| 模拟支付 | 开 | 开 |
| 错误响应带异常栈 | 开 | 关，只返回错误码和 traceId |

## 3. 通用约定

- JSON，`camelCase`。上传用 `multipart/form-data`。小程序不用 `PATCH`。查询里多个值用逗号：`status=PENDING_PAY,PAID`。多出来的查询参数忽略。
- 登录头：`Authorization: Bearer <token>`。JWT 固定 HS256，拒绝 `alg: none`。载荷：`sub`、`typ`（`user` / `admin`）、`ver`、`jti`、`iat`、`exp`。用户 7 天，管理员 12 小时。两种 token 不能互调。登出或封禁（`ver` 加一）后旧 token 失效。401 时走学号密码重新登录，只重试一次。
- 除健康检查、私有图片本体、支付回调外，响应都是：

```json
{ "code": "OK", "message": "ok", "data": {}, "traceId": "8c1f0a2b9d3e4f57" }
```

校验失败时 `data.fieldErrors` 为 `[{ "field", "message" }]`。`uni.request` 要同时看 `statusCode` 和 `code`。

| HTTP | 含义 |
| --- | --- |
| 200 | 成功 |
| 400 | 格式错、校验失败、未知字段 |
| 401 | 未登录或 token 无效 |
| 403 | 已登录但无权、未认证、被封禁 |
| 404 | 不存在，或存在但你看不到 |
| 409 | 状态冲突 |
| 413 | 文件过大 |
| 429 | 限流，响应头 `Retry-After` 为秒 |
| 500 / 503 | 服务器错误 / 微信等上游不可用 |

- 分页：`page` 从 1，`size` 默认 20、最大 50。响应 `list`、`page`、`size`、`total`、`hasMore`。聊天记录用消息 ID 做游标。
- 时间：`2026-09-29T10:15:30+08:00`。不要用带空格的写法，iOS 上 `new Date()` 解析不了。
- 金额：整数，单位分。`1500` = 15.00 元。前端不计算、不回传金额。
- ID：用户/商品/订单等是自增整数。`orderNo` 为 `O` + 时间 + 4 位随机数，`paymentNo` 为 `P` 开头，`fileId` 为 `f_` + 随机串。自增 ID 能被遍历，按 ID 取数据必须先查归属。
- 枚举用大写英文，中文名由 `GET /api/meta` 下发。前端遇到不认识的值显示「未知」。
- **请求体出现文档没定义的字段：400 `UNKNOWN_FIELD`。** 字段名是 `price`、`amount`、`paid`、`status`、`stock`、`sellerId`、`buyerId`、`userId`、`role` 时再记审计 `TAMPER_FIELD`。当前用户只从 token 取。
- 幂等：下单和创建支付单可带 `Idempotency-Key`（8–64 位）。同一用户、同一键 10 分钟内：成功过就返回原结果；还在处理返回 409 `CONFLICT`。发消息用 `clientMsgId`。
- 文本按字符计，先 trim。原样保存。小程序用 `<text>`，不用 `rich-text`；管理网页不用 `v-html`。
- 联系方式（先做 NFKC，全角转半角）：商品标题/描述/交易说明、昵称、简介、面交地点、评价、申诉说明里出现则 400 `CONTACT_INFO_NOT_ALLOWED`。聊天消息不拒绝，命中片段换成 `[联系方式已隐藏]`，`masked: true`。举报说明不检测。

```text
手机号：(?<!\d)1[3-9]\d(?:[\s-]?\d){8}(?!\d)
微信号：(?:微信|威信|薇信|v信|vx|wx|weixin|wechat|加v|\+v)[\s:：]*[a-z][-_a-z0-9]{5,19}
QQ：   (?:qq|扣扣|企鹅)[\s:：]*[1-9]\d{4,10}
邮箱：  [a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}
```

| 限流 | 上限 |
| --- | --- |
| 注册发验证码 | 同一邮箱 60 秒 1 次；每 IP 每分钟 10 次 |
| 登录失败 | 15 分钟 5 次后锁 15 分钟 |
| 微信登录 | 每 IP 每分钟 10 次（后续） |
| 管理员登录失败 | 15 分钟 5 次后锁 15 分钟 |
| 提交校园认证 | 每人每天 3 次 |
| 上传 | 每人每小时 60 个 |
| 发布商品 | 每人每天 20 件 |
| 编辑商品 | 每人每小时 60 次 |
| 商品列表 | 每人每分钟 120 次 |
| 下单 | 每人每小时 10 单，另有待支付上限 |
| 创建支付单 / 模拟付款 | 各每人每小时 20 次 |
| 开会话 | 每人每小时 30 个 |
| 发消息 | 每人每分钟 30 条 |
| 举报 | 每人每天 10 次 |

| 谁 | 能做什么 |
| --- | --- |
| 公开 | 健康检查、元数据、楼栋、分类、注册、登录、管理员登录 |
| 登录（`status=ACTIVE`） | 浏览、资料、收藏、浏览记录、通知、举报、提交认证 |
| 已认证（`verifyStatus=VERIFIED`） | 发布、下单、支付、聊天、面交、申诉、评价 |
| 资源方 | 在上一级之外，还必须是卖家本人、订单买家或卖家、会话成员 |
| 管理员 | 只认管理员 token |

别人的订单、支付单、会话、私有文件：403，记 `ACCESS_DENIED`。别人的在售商品可看；编辑返回 403。别人已下架或已删除的商品：404。

底线：MyBatis 只用 `#{}`。`ORDER BY` 用白名单。`LIKE` 转义 `%`、`_`、`\`。上传按文件头判断类型，转码，去掉 EXIF。日志不记 token、密码、密钥、完整学号、聊天原文。

## 4. 接口清单

| 模块 | 方法 | 路径 | 权限 |
| --- | --- | --- | --- |
| 系统 | GET | `/api/health` | 公开。已实现。不走统一包：`{"service":"campus-ershou","status":"UP"}` |
| 系统 | GET | `/api/meta` | 公开。枚举中文名 + 下面的 limits |
| 基础 | GET | `/api/campus/buildings?type=&meetupOnly=` | 公开 |
| 基础 | GET | `/api/categories` | 公开。只有一级 |
| 认证 | POST | `/api/auth/register` | 公开。`{ "studentNo", "username", "email" }`，发邮箱验证码 |
| 认证 | POST | `/api/auth/register/verify-code` | 公开。`{ "email", "code" }` |
| 认证 | POST | `/api/auth/register/set-password` | 公开。`{ "setupToken", "password" }`，完成后发 JWT |
| 认证 | POST | `/api/auth/login` | 公开。`{ "studentNo", "password" }` |
| 认证 | POST | `/api/auth/logout` | 登录 |
| 认证 | POST | `/api/auth/wx-login` | 后续（AppID 未就绪） |
| 认证 | POST | `/api/auth/dev-login` | 仅 local。`{ "devOpenid" }` 匹配 `^dev-[a-z0-9-]{3,32}$` |
| 认证 | POST | `/api/auth/campus-verification` | 登录，且未认证或已驳回。本批未实现 |
| 认证 | GET | `/api/auth/campus-verification` | 登录。本批未实现 |
| 用户 | GET | `/api/me` | 登录 |
| 用户 | PUT | `/api/me/profile` | 登录。只改传入字段 |
| 用户 | GET | `/api/me/stats` | 登录 |
| 用户 | GET | `/api/me/badges` | 登录。未读角标，可 30 秒轮询 |
| 用户 | GET | `/api/users/{userId}` | 登录。公开主页 |
| 用户 | GET | `/api/users/{userId}/goods?status=` | 登录。`status` 只能 `ON_SALE` 或 `SOLD` |
| 用户 | GET | `/api/users/{userId}/reviews` | 登录 |
| 文件 | POST | `/api/files` | 见 §6。`file` + `purpose` |
| 文件 | GET | `/api/files/{fileId}/url` | 资源方。私有图 10 分钟临时地址 |
| 文件 | GET | `/api/files/private/{fileId}?size=&exp=&sig=` | 地址签名。给 `<image>` 用 |
| 商品 | GET | `/api/goods` | 登录。只返回在售 |
| 商品 | GET | `/api/goods/{goodsId}` | 登录 |
| 商品 | POST | `/api/goods` | 已认证 |
| 商品 | PUT | `/api/goods/{goodsId}` | 已认证且是卖家。整体替换 |
| 商品 | POST | `/api/goods/{goodsId}/off-shelf` | 卖家 |
| 商品 | POST | `/api/goods/{goodsId}/on-shelf` | 已认证且是卖家 |
| 商品 | DELETE | `/api/goods/{goodsId}` | 卖家。软删除 |
| 商品 | GET | `/api/me/goods?status=` | 登录 |
| 商品 | PUT / DELETE | `/api/goods/{goodsId}/favorite` | 登录 |
| 商品 | GET | `/api/me/favorites` | 登录 |
| 商品 | GET / DELETE | `/api/me/history` | 登录。DELETE 为清空 |
| 订单 | POST | `/api/orders` | 已认证 |
| 订单 | GET | `/api/orders/{orderId}` | 买家或卖家 |
| 订单 | GET | `/api/me/orders?role=&status=` | 登录。`role` 为 `BUYER`（默认）或 `SELLER` |
| 订单 | POST | `/api/orders/{orderId}/cancel` | 买家或卖家。`{ "reason"? }` |
| 订单 | PUT | `/api/orders/{orderId}/meetup` | 买家或卖家，仅 `PAID` |
| 订单 | POST | `/api/orders/{orderId}/handover-confirm` | 买家或卖家，仅 `PAID` |
| 订单 | POST | `/api/orders/{orderId}/dispute` | 买家或卖家，仅 `PAID` |
| 订单 | POST | `/api/orders/{orderId}/dispute/withdraw` | 申诉发起人 |
| 订单 | POST | `/api/orders/{orderId}/review` | 买家或卖家，仅 `COMPLETED` |
| 支付 | POST | `/api/orders/{orderId}/payments` | 买家。`{ "channel": "MOCK" }`，不能带金额 |
| 支付 | GET | `/api/payments/{paymentNo}` | 买家 |
| 支付 | POST | `/api/pay/mock/{paymentNo}/confirm` | 买家。`{ "result": "SUCCESS" 或 "FAIL" }`，不能带金额 |
| 支付 | POST | `/api/pay/notify/mock` | 签名，不用 token |
| 聊天 | POST | `/api/im/conversations` | 已认证。`{ "goodsId" }` |
| 聊天 | GET | `/api/im/conversations` | 登录 |
| 聊天 | GET | `/api/im/conversations/{id}` | 会话成员 |
| 聊天 | GET | `/api/im/conversations/{id}/messages` | 会话成员 |
| 聊天 | POST | `/api/im/conversations/{id}/messages` | 已认证且是成员 |
| 聊天 | POST | `/api/im/conversations/{id}/read` | 会话成员。`{ "lastReadMessageId" }` |
| 聊天 | WS | `/ws/im` | 登录 |
| 通知 | GET | `/api/notifications?unreadOnly=` | 登录 |
| 通知 | POST | `/api/notifications/read` | 登录。`{ "ids": [] }` 或 `{ "all": true }` |
| 举报 | POST | `/api/reports` | 登录 |
| 举报 | GET | `/api/me/reports` | 登录 |
| 管理 | POST | `/api/admin/auth/login` | 公开 |
| 管理 | POST | `/api/admin/auth/logout` | 管理员 |
| 管理 | POST | `/api/admin/auth/password` | 管理员。`oldPassword`、`newPassword`（≥12 位） |
| 管理 | GET | `/api/admin/me` | 管理员 |
| 管理 | GET | `/api/admin/dashboard` | 管理员。待办计数 |
| 管理 | GET | `/api/admin/verifications?status=` | 管理员 |
| 管理 | GET | `/api/admin/verifications/{id}` | 管理员。含证件临时地址和历史申请 |
| 管理 | POST | `/api/admin/verifications/{id}/approve` | 管理员 |
| 管理 | POST | `/api/admin/verifications/{id}/reject` | 管理员。`{ "reason" }` 2–100 字 |
| 管理 | GET | `/api/admin/users?keyword=&status=&verifyStatus=` | 管理员 |
| 管理 | GET | `/api/admin/users/{userId}` | 管理员 |
| 管理 | POST | `/api/admin/users/{userId}/ban` | 管理员。`{ "reason", "days" }`，`days: null` 为永久 |
| 管理 | POST | `/api/admin/users/{userId}/unban` | 管理员 |
| 管理 | GET | `/api/admin/goods?status=&keyword=&sellerId=` | 管理员。含 `DELETED` |
| 管理 | POST | `/api/admin/goods/{goodsId}/remove` | 管理员。`{ "reason" }` |
| 管理 | POST | `/api/admin/goods/{goodsId}/restore` | 管理员。`REMOVED → OFF_SHELF` |
| 管理 | GET | `/api/admin/orders?status=&orderNo=&buyerId=&sellerId=` | 管理员 |
| 管理 | GET | `/api/admin/orders/{orderId}` | 管理员。含状态流水和支付单，不能直接改状态 |
| 管理 | GET | `/api/admin/disputes?status=` | 管理员 |
| 管理 | POST | `/api/admin/disputes/{id}/resolve` | 管理员。`{ "result": "COMPLETE" 或 "REFUND", "remark" }` |
| 管理 | GET | `/api/admin/reports?status=&targetType=` | 管理员 |
| 管理 | GET | `/api/admin/reports/{id}` | 管理员。含消息原文快照 |
| 管理 | POST | `/api/admin/reports/{id}/handle` | 管理员 |
| 管理 | GET | `/api/admin/audit-logs?category=&event=&userId=&ip=&from=&to=` | 管理员 |
| 管理 | GET、POST | `/api/admin/categories` | 管理员 |
| 管理 | PUT | `/api/admin/categories/{id}` | 管理员。不提供删除，停用即可 |
| 管理 | GET、POST | `/api/admin/buildings` | 管理员 |
| 管理 | PUT | `/api/admin/buildings/{id}` | 管理员 |

主流程：注册（学号 + 用户名 + 邮箱验证码 + 设密码）→ 学号密码登录 → 填资料 → 校园认证 → 管理员通过 → 卖家上传图片并发布 → 买家浏览 → 开会话 → 下单 → 创建支付单 → 模拟付款 → 约面交 → 双方确认 → 评价。出问题走申诉，管理员 `resolve`。

## 5. 枚举与上限

`GET /api/meta` 的 `enums.*.value/label` 与 `limits`：

| 枚举 | 值 |
| --- | --- |
| goodsCondition | `NEW` 全新，`LIKE_NEW` 几乎全新，`GOOD` 轻微使用痕迹，`FAIR` 明显使用痕迹，`POOR` 有瑕疵但可用 |
| goodsStatus | `ON_SALE` 在售，`RESERVED` 已预约，`SOLD` 已售出，`OFF_SHELF` 已下架，`REMOVED` 管理员下架，`DELETED` 已删除（仅管理端） |
| orderStatus | `PENDING_PAY` 待支付，`PAID` 待面交，`COMPLETED` 已完成，`CANCELLED` 已取消，`DISPUTED` 申诉中 |
| paymentStatus | `CREATED` 待付款，`SUCCESS` 已付款，`FAILED` 付款失败，`CLOSED` 已关闭，`REFUNDED` 已退款 |
| userStatus | `ACTIVE`，`BANNED` |
| verifyStatus | `UNVERIFIED`，`PENDING`，`VERIFIED`，`REJECTED` |
| buildingType | `DORM`，`TEACHING`，`CANTEEN`，`LIBRARY`，`GATE`，`OTHER` |
| disputeReason | `NOT_AS_DESCRIBED`，`PEER_NO_SHOW`，`PAYMENT_ISSUE`，`OTHER` |
| disputeStatus | `OPEN`，`WITHDRAWN`，`RESOLVED` |
| reportTargetType | `GOODS`，`USER`，`MESSAGE`，`ORDER` |
| reportReason | `FRAUD`，`PROHIBITED`，`FALSE_INFO`，`OFF_PLATFORM`，`HARASSMENT`，`OTHER` |
| reportStatus | `PENDING`，`DISMISSED`，`ACTIONED` |
| messageType | `TEXT`，`IMAGE`，`SYSTEM` |
| filePurpose | `GOODS` 公开，`AVATAR` 公开，`IM` 会话双方，`VERIFICATION` 本人和管理员，`EVIDENCE` 本人和管理员；用于申诉时订单双方也能看 |

limits：`payTimeoutMinutes` 15，`maxPendingPayOrders` 3，`priceMin` 1，`priceMax` 9999900，标题 2–30，描述 500，交易说明 100，商品图最多 9 张、单张 5242880 字节，昵称 16，简介 60，消息 500，面交地点 40、最多提前 14 天，评价窗口 30 天、内容 200，申诉说明 10–500、证据最多 6 张，举报说明 300。

楼栋：`id`、`name`、`areaId`、`areaName`、`type`、`meetupPoint`、`sort`。停用的不返回。「同区」比 `areaId`。分类：`id`、`name`、`icon`、`sort`。`icon` 对应小程序本地图片。初始分类：教材书籍、数码电子、宿舍电器、生活日用、运动户外、服饰鞋包、考研考证、其他。

通知类型：`VERIFICATION_APPROVED`、`VERIFICATION_REJECTED`、`ORDER_CREATED`（卖家）、`ORDER_PAID`（卖家）、`ORDER_CANCELLED`、`ORDER_REFUNDED`（买家）、`MEETUP_UPDATED`、`HANDOVER_CONFIRMED`、`ORDER_COMPLETED`、`DISPUTE_OPENED`、`DISPUTE_WITHDRAWN`、`DISPUTE_RESOLVED`、`REVIEW_RECEIVED`、`GOODS_REMOVED`、`GOODS_RESTORED`、`REPORT_HANDLED`、`ACCOUNT_WARNED`。列表项含 `title`、`content`、`ref: { type: ORDER|GOODS|REPORT, id }`、`read`、`createdAt`。

## 6. 认证、用户、文件

当前底座登录方式是 **学号 + 密码**。注册用邮箱验证码，不依赖微信 AppID。`wx-login` 等 AppID。`dev-login` 仅本机，且 `app.dev-login.enabled=true`；服务器保持关闭，返回 404。

**注册分三步。**

1. `POST /api/auth/register` `{ "studentNo", "username", "email" }` → `{ emailMasked, expiresInSeconds }`。学号 `^\d{8,12}$`；用户名 1–16 字，仅中文/字母/数字/下划线，且不能含联系方式；邮箱唯一、存小写。学号已绑定：409 `STUDENT_NO_TAKEN`。用户名占用：409 `USERNAME_TAKEN`。邮箱已注册：409 `EMAIL_TAKEN`。同一邮箱 60 秒只能发 1 次；同一 IP 每分钟 10 次。本机未启用 SMTP 时，`data.debugCode` 返回 6 位验证码，方便联调；服务器禁止该字段。
2. `POST /api/auth/register/verify-code` `{ "email", "code" }` → `{ setupToken, expiresInSeconds }`。验证码 6 位数字，10 分钟有效，最多试 5 次。错误：400 `INVALID_CODE`。过期或不存在：400 `CODE_EXPIRED`。
3. `POST /api/auth/register/set-password` `{ "setupToken", "password" }` → `{ token, expiresAt, isNewUser: true, user }`。密码 8–64 位。`setupToken` 15 分钟有效，失效：400 `SETUP_TOKEN_INVALID`。成功后发用户 JWT。新用户 `status=ACTIVE`，`verifyStatus=UNVERIFIED`（邮箱只证明邮箱，校园认证仍要走证件审核）。

**登录** `POST /api/auth/login` `{ "studentNo", "password" }` → `{ token, expiresAt, isNewUser: false, user }`。学号或密码错一律 401 `BAD_CREDENTIALS`，不区分哪一项。封禁：403 `USER_BANNED`，`data.reason`、`data.until`（`null` 为永久）。15 分钟内失败 5 次后锁 15 分钟：429 `LOGIN_LOCKED`，响应头 `Retry-After`。

**登出** `POST /api/auth/logout`。令牌版本加一，当前 `jti` 进 Redis 黑名单，旧 token 失效。

学号只存 `HMAC-SHA256(密钥, 学号)` 和脱敏串（前 4 后 2）。接口不返回明文学号、密码、完整邮箱。`user` / `GET /api/me` 含 `id`、`username`、`nickname`、`emailMasked`、`studentNoMasked`、`avatarUrl`、`bio`、`status`、`verifyStatus`、`dormBuilding`、`createdAt`。

**校园认证** `{ "dormBuildingId", "cardImageFileId" }`（学号在注册时已绑定，不再改）。证件照审核结束 30 天后删除。已认证：`ALREADY_VERIFIED`；审核中：`VERIFICATION_PENDING`。进度接口在未提交时 `status=UNVERIFIED`。提交、进度、管理员通过/驳回已实现。通过后 `verifyStatus=VERIFIED`，驳回后为 `REJECTED` 并可重新提交。已处理再审：409 `VERIFICATION_NOT_PENDING`。

**资料** 本人可见学号脱敏和宿舍楼。别人的主页有 `nickname`、`avatarUrl`、`bio`、`verified`、`relation.sameBuilding/sameArea`、`rating`（暂为 null）、成交数、在售数。不返回学号和楼名。`PUT /api/me/profile` 可改 `nickname`（1–16）、`avatarFileId`、`bio`（≤60）、`dormBuildingId`（必须是 `DORM`）。不能用外部图片 URL。`GET /api/me`、`PUT /api/me/profile`、`GET /api/me/stats`、`GET /api/me/badges`、`GET /api/users/{userId}` 已实现。角标和统计里的订单数目前为 0。

**上传** 只收 JPEG、PNG、WebP，≤5 MB。按文件头判断，转成 JPEG（最长边 1600，缩略图 400），去掉 EXIF，文件名随机。公开用途直接给 URL；私有用途的 URL 是 10 分钟临时地址，业务里只存 `fileId`。引用 `fileId` 必须同时满足：存在、本人上传、用途匹配，否则 400 `FILE_NOT_USABLE`。私有图签名：`HMAC-SHA256(file.sign-key, fileId + "|" + size + "|" + exp)`，`size` 为空或 `thumb`。

| purpose | 谁能传 |
| --- | --- |
| `GOODS` | 已认证 |
| `AVATAR` | 登录 |
| `IM` | 已认证 |
| `VERIFICATION` | 未认证或已驳回 |
| `EVIDENCE` | 已认证 |

## 7. 商品

库存固定为 1，发布时服务端写入。状态只能用下表里的接口改，并且 `UPDATE ... WHERE status = '原状态'`，影响 0 行就 409。

| 状态 | 谁能看 | 能否改价/编辑 | 能否买 |
| --- | --- | --- | --- |
| `ON_SALE` | 登录用户 | 卖家 | 能 |
| `RESERVED` | 登录用户，显示已预约 | 否 | 否 |
| `SOLD` | 登录用户 | 否 | 否 |
| `OFF_SHELF` | 只有卖家 | 卖家 | 否 |
| `REMOVED` | 卖家（带原因）和管理员 | 否 | 否 |
| `DELETED` | 只有管理员 | 否 | 否 |

转移：发布 → `ON_SALE`；下单 → `RESERVED`；未付款取消 → `ON_SALE`；订单完成 → `SOLD`；已付款取消或退款 → `OFF_SHELF`；卖家下架/上架在 `ON_SALE` 与 `OFF_SHELF` 之间；管理员下架 → `REMOVED`（已预约要先取消订单；申诉中则 409 `GOODS_IN_DISPUTE`）；恢复 → `OFF_SHELF`；卖家删除 → `DELETED`。

列表项：`id`、`title`、`coverUrl`、`price`、`originalPrice`、`condition`、`status`、`category`、`meetupBuilding`、`seller`（含 `relation`）、`favoriteCount`、`createdAt`。详情另有 `description`、`tradeNote`、`images[]`、`viewCount`、`seller.verified/rating`、`viewer`（`isMine`、`favorited`、`canBuy`、`canChat`、`activeOrderId`）、`removedReason`（仅卖家）、`updatedAt`。`price` 只用于展示。

列表参数：`keyword`（1–30，匹配标题和描述）、`categoryId`、`condition`、`minPrice`、`maxPrice`、`meetupBuildingId`、`areaId`、`sort`。`sort` 只允许 `latest`（默认）、`price_asc`、`price_desc`、`nearby`。`nearby`：面交点与我的宿舍同楼优先，同区其次；我没填宿舍则按 `latest`。非法 `sort`：400。

发布和编辑的请求体：

| 字段 | 规则 |
| --- | --- |
| title | 必填，2–30，无联系方式 |
| description | 0–500，无联系方式。编辑时 `null` 表示清空 |
| categoryId | 必填，启用中的分类 |
| condition | 必填，卖家自报成色 |
| price | 必填，1–9999900 分。这是挂牌价，不是下单金额 |
| originalPrice | 可选，1–99999900。`null` 清空 |
| imageFileIds | 1–9，不重复，用途 `GOODS`。第一张是封面 |
| meetupBuildingId | 必填，且 `meetupPoint=true` |
| tradeNote | 0–100，无联系方式 |

服务端写入 `sellerId`、`status=ON_SALE`、`stock=1`。带这三个字段会被拒绝。只有 `ON_SALE`、`OFF_SHELF` 能编辑；改价记 `GOODS_PRICE_CHANGED`，不影响已下单的金额快照。下架、上架、删除的响应只给 `id` 和 `status`，删除后所有人看不到，订单快照保留。

收藏是幂等的。浏览记录在 Redis，每人最近 100 条、30 天；同一人同一天只给浏览数加 1。

## 8. 订单

| 从 | 到 | 谁触发 |
| --- | --- | --- |
| — | `PENDING_PAY` | 买家下单 |
| `PENDING_PAY` | `PAID` | 验签通过的支付回调 |
| `PENDING_PAY` | `CANCELLED` | 买家或卖家取消、超时、封禁、管理员下架 |
| `PAID` | `COMPLETED` | 双方都确认面交，或管理员判 `COMPLETE` |
| `PAID` | `CANCELLED` 并退款 | 只有卖家可取消；或管理员下架；或管理员判 `REFUND` |
| `PAID` | `DISPUTED` | 买家或卖家申诉 |
| `DISPUTED` | `PAID` | 发起人撤回 |
| `DISPUTED` | `COMPLETED` / `CANCELLED` | 管理员处理 |

表里没有的转移不存在。每次都是 `UPDATE ... WHERE id=? AND status='原状态'`，0 行则 409 `ORDER_STATUS_INVALID`。每次写状态流水。

`PENDING_PAY` 时商品 `RESERVED`；`PAID`、`DISPUTED` 仍是 `RESERVED`；`COMPLETED` 时商品 `SOLD`；未付款取消商品回 `ON_SALE`；已付款退款商品变 `OFF_SHELF`。

订单详情字段：`orderNo`、`status`、`myRole`（`BUYER`/`SELLER`）、`amount`（下单时按库内标价写入，之后不变）、`goods` 快照、`buyer`、`seller`、`conversationId`、`meetup`、`handover`、`payment`、`cancel`、`refund`、`dispute`、`review`、`allowedActions`、`expireAt`、`paidAt`、`completedAt`、`cancelledAt`。不是买家或卖家：403。读到已过期的待支付单时，当场按超时取消再返回。

`allowedActions`：`PAY`、`CANCEL`、`UPDATE_MEETUP`、`CONFIRM_HANDOVER`、`OPEN_DISPUTE`、`WITHDRAW_DISPUTE`、`REVIEW`。前端按它显示按钮。

**下单**只收 `{ "goodsId", "quantity" }`，`quantity` 只能是 1。处理顺序：

1. 已认证；不是自己的商品（`CANNOT_BUY_OWN_GOODS`）。
2. 待支付已有 3 单：`PENDING_ORDER_LIMIT`。24 小时内第 3 次超时未付后，24 小时内不能下单：403 `ORDER_RESTRICTED`。
3. Redis：`SET lock:goods:{goodsId} {requestId} NX PX 5000`。拿不到锁：409 `GOODS_NOT_AVAILABLE`。
4. 同一事务：`UPDATE goods SET status='RESERVED', stock=stock-1, version=version+1 WHERE id=? AND status='ON_SALE' AND stock>=1`。0 行则 409。然后按读到的价格插入订单，`expire_at` 为现在加 15 分钟。
5. 唯一约束兜底：进行中（`PENDING_PAY`、`PAID`、`DISPUTED`）的同一商品只能有一单。表名用 `orders`。生成列 `active_goods_id`，唯一索引 `uk_orders_active_goods`。
6. 用 Lua 比较后删除自己的锁。没有会话就建一个。通知卖家。

三层挡住都返回同一个 `GOODS_NOT_AVAILABLE`，并记 `ORDER_STOCK_CONFLICT`。若进行中的订单就是我的，`data.myOrderId` 让前端跳过去。

**取消：** 待支付双方都能取消，商品回在售，支付单 `CLOSED`。已付款只有卖家能取消，并模拟退款，商品下架。取消和支付回调并发时，只有一个条件更新能成功；取消先成功、付款后到的，自动退款。

**面交：** `{ "buildingId", "place", "time" }`。`place` 1–40 字。时间在未来 14 天内。双方都能改，后写覆盖。确认面交无请求体，按 token 区分角色，重复调用不报错。双方都确认后订单完成、商品售出。

**申诉：** `{ "reason", "description", "evidenceFileIds" }`。说明 10–500 字。订单变 `DISPUTED`。撤回后回到 `PAID`。管理员 `COMPLETE`：订单完成、商品售出。`REFUND`：订单取消、模拟退款、商品下架。`remark` 2–300 字，双方可见。

**评价：** `{ "score": 1-5, "content" }`。每方一次，完成后 30 天内。过期 `REVIEW_WINDOW_CLOSED`，重复 `ALREADY_REVIEWED`。

定时任务：每 30 秒取消超时未支付；支付回调按 5 秒、30 秒、2 分钟、10 分钟、30 分钟重发，最多 5 次；每天删满 30 天的证件照。卖家已确认、买家 72 小时无操作则自动完成：默认关闭。

## 9. 支付

课程不接真实资金。后端内置模拟网关，和订单之间只通过签名回调通信。

支付单：`CREATED` → `SUCCESS` / `FAILED` / `CLOSED` / `REFUNDED`。一单可有多张支付单，最多一张 `SUCCESS`。已有未过期的 `CREATED` 则直接返回，不新建。

创建支付单返回 `paymentNo`、`amount`（取订单金额）、`expireAt`、`cashier.confirmPath`。渠道只允许 `MOCK`，否则 `PAY_CHANNEL_UNSUPPORTED`。

模拟付款由网关按支付单金额记账，签名后 POST 到 `/api/pay/notify/mock`。这个地址必须能被调用，安全靠签名，不靠把地址藏起来。

回调字段：`paymentNo`、`orderNo`、`amount`、`status`（`SUCCESS`/`FAIL`）、`gatewayTradeNo`、`timestamp`（Unix 秒）、`nonce`（16–32 位，重发也要换）、`sign`。

签名：除 `sign` 外的非空字段按名字 ASCII 排序，拼成 `字段=值`，用 `&` 连接，值不做 URL 编码。`sign = HMAC-SHA256(pay.mock.secret, 拼接串)` 的小写十六进制。常量时间比较。

示例密钥 `example-secret-do-not-use-anywhere-0001` 只用来核对算法，不能用于任何环境。拼接串：

```text
amount=1500&gatewayTradeNo=MOCK20260929101602483911&nonce=9f2c7a1e4b6d8f03&orderNo=O202609291015004821&paymentNo=P202609291015304107&status=SUCCESS&timestamp=1790648162
```

签名：`6f9ae15df7364522ef981a8fc3ce4ed861db81dffe5c7dbba44dde67292316a2`。

校验顺序，失败不改订单：

| 步 | 失败 |
| --- | --- |
| 字段齐全 | 400 `BAD_REQUEST` |
| 时间戳相差 ≤300 秒 | `EXPIRED_TIMESTAMP`，审计 `PAY_NOTIFY_EXPIRED` |
| 签名正确 | `INVALID_SIGN`，审计 `PAY_NOTIFY_BAD_SIGN` |
| nonce 10 分钟内未用过：`SET pay:nonce:{nonce} 1 NX EX 600` | `REPLAYED`，审计 `PAY_NOTIFY_REPLAY` |
| 支付单存在且订单号一致 | `PAYMENT_NOT_FOUND` |
| 金额等于支付单金额，也等于订单金额 | `AMOUNT_MISMATCH`，审计 `PAY_NOTIFY_AMOUNT_MISMATCH` |
| 支付单已是 `SUCCESS` | 直接返回成功 |
| `status=FAIL` | 支付单 `CREATED → FAILED`，订单不变，返回成功 |
| `status=SUCCESS` | 同事务：支付单成功，订单 `PENDING_PAY → PAID`。订单已不是待支付则不改订单，立刻退款，审计 `PAYMENT_LATE_REFUND` |

成功响应不走统一包：HTTP 200 `{ "code": "SUCCESS", "message": "OK" }`。失败 HTTP 400 `{ "code": "FAIL", "message": "<原因>" }`。没收到 200 和 `SUCCESS` 就重发。

退款没有对外接口。触发：卖家取消已付款订单、管理员判退款、管理员下架有已付款订单的商品、迟到的付款。模拟退款立即把支付单改为 `REFUNDED`。

以后换渠道只换验签和收银台。防重放、回查金额、幂等、状态机必须复用。真实渠道还要加主动查单。

## 10. 聊天

一件商品、一个买家、一个卖家一个会话。发消息走 HTTP，收消息靠 WebSocket。HTTP 是数据来源。

发起会话：当前用户是买家，商品须 `ON_SALE`。已有则直接返回。不能和自己聊：`CANNOT_CHAT_WITH_SELF`。卖家不能主动开会话。下单时如果还没有会话，服务端补建。

消息：`type` 为 `TEXT`（1–500 字）、`IMAGE`（先上传，用途 `IM`）或 `SYSTEM`。客户端不能发 `SYSTEM`。`clientMsgId` 必填，8–40 位；同一发送人重复则返回第一条。系统消息在下单、付款、面交、完成、取消、申诉时自动写入。

拉记录：`beforeId` 往上翻，`afterId` 断线补拉，只能带一个。`size` 默认 30、最大 50。`list` 按 id 升序。

WebSocket：`/ws/im`，请求头带 token，不要把 token 放在 URL 里。无效则关闭码 4401。客户端每 30 秒发 `{"type":"PING"}`，服务端回 `PONG`。90 秒无帧则断开。服务端推送：`MESSAGE`、`READ`、`NOTIFICATION`、`ORDER_STATUS`（`orderId`、`status`）、`BADGE`。客户端只发 `PING`。关闭码 4401 重新登录后再连；4403 是封禁，不再重连。重连间隔 1、2、4、8 秒，之后 15 秒。重连后拉角标，当前会话用 `afterId` 补拉。单用户最多 3 个连接。

## 11. 举报与管理端动作

举报：`targetType`、`targetId`、`reason`、`description`（0–300，不拦联系方式）、`evidenceFileIds`（0–6）。目标必须是我看得到的，不能举报自己。24 小时内同一目标重复：`DUPLICATE_REPORT`。举报消息时保存隐藏前的原文，只给管理员看。

管理员登录失败记 `LOGIN_FAIL`，不说明是账号错还是密码错。`mustChangePassword=true` 时，除改密码外都返回 403 `PASSWORD_CHANGE_REQUIRED`。改密后旧 token 失效。审核通过记 `ADMIN_VERIFY_APPROVE`，驳回记 `ADMIN_VERIFY_REJECT`，`detail` 只含申请 ID 和用户 ID。

封禁：令牌版本加一；在售商品改为下架；待支付订单取消（`by=ADMIN`），商品回在售。已付款订单不自动处理。解封后商品不自动上架。

强制下架：在售或已下架直接 `REMOVED`。已预约且待支付则取消订单；已付款则取消并退款。申诉中返回 `GOODS_IN_DISPUTE`。

处理举报的 `action`：`DISMISS`（不成立）、`WARN`（通知被举报人）、`REMOVE_GOODS`（仅商品类）、`BAN_USER`（`banDays` 为天数或 `null`）。已处理：`REPORT_ALREADY_HANDLED`。

审计 `detail` 只记字段名、资源 ID、金额、命中的联系方式类别，不记原文。

| 类别 | 事件 |
| --- | --- |
| SECURITY | `TAMPER_FIELD`、`ACCESS_DENIED`、`TOKEN_INVALID`（过期不记）、`LOGIN_FAIL`、`RATE_LIMITED`、`PAY_NOTIFY_BAD_SIGN`、`PAY_NOTIFY_REPLAY`、`PAY_NOTIFY_EXPIRED`、`PAY_NOTIFY_AMOUNT_MISMATCH`、`FILE_REJECTED`、`DEV_LOGIN_BLOCKED` |
| BUSINESS | `ORDER_STOCK_CONFLICT`、`ORDER_STATUS_CHANGED`、`PAYMENT_SUCCESS`、`PAYMENT_LATE_REFUND`、`GOODS_PRICE_CHANGED`、`IM_CONTACT_MASKED` |
| ADMIN | `ADMIN_VERIFY_APPROVE`、`ADMIN_VERIFY_REJECT`、`ADMIN_USER_BAN`、`ADMIN_USER_UNBAN`、`ADMIN_GOODS_REMOVE`、`ADMIN_GOODS_RESTORE`、`ADMIN_DISPUTE_RESOLVE`、`ADMIN_REPORT_HANDLE` |

Redis 键都加 `campus:`：`lock:goods:{id}` 5 秒；`idem:{userId}:{key}` 10 分钟；`pay:nonce:{nonce}` 10 分钟；`jwt:deny:{jti}` 到 token 过期；`user:tokenver:{userId}`；`order:timeouts:{userId}` 24 小时；`rl:{规则}:{用户或IP}:{时间窗}`；`history:{userId}`；`goods:view:{id}:{yyyyMMdd}`；商品列表和详情短缓存。下单和支付只读 MySQL。释放锁用 Lua：值等于自己的 requestId 才删除。

模块对应包：`/api/auth` → `auth`；`/api/me` 与 `/api/users` → `user`；元数据、楼栋、分类、文件 → `common`；商品 → `goods`；订单 → `order`；支付 → `pay`；聊天和通知 → `im`；举报 → `report`；管理端账号和审计 → `admin`，其余管理接口放在对应业务包。`ai` 包留给 AI 模块，底座不用。书籍视觉分档另起模块，包等文档再定。下单改库存只能调商品模块的方法，不在订单包里直接 `UPDATE goods`。

## 12. 错误码

| code | HTTP | 含义 |
| --- | --- | --- |
| `OK` | 200 | 成功 |
| `BAD_REQUEST` | 400 | JSON 或类型错误 |
| `VALIDATION_FAILED` | 400 | 字段不合法，看 `fieldErrors` |
| `UNKNOWN_FIELD` | 400 | 请求体有文档外字段 |
| `CONTACT_INFO_NOT_ALLOWED` | 400 | 文本含联系方式 |
| `INVALID_CODE` | 400 | 邮箱验证码错误 |
| `CODE_EXPIRED` | 400 | 验证码已过期，请重新获取 |
| `SETUP_TOKEN_INVALID` | 400 | 设置密码凭证无效或已过期 |
| `WX_LOGIN_FAILED` | 400 | 微信 code 无效或已用过 |
| `FILE_TYPE_NOT_ALLOWED` | 400 | 不是 JPEG、PNG、WebP |
| `FILE_NOT_USABLE` | 400 | fileId 不能用 |
| `PAY_CHANNEL_UNSUPPORTED` | 400 | 渠道不是 `MOCK` |
| `UNAUTHORIZED` | 401 | 未登录或 token 失效 |
| `BAD_CREDENTIALS` | 401 | 学号/密码错误，或管理员账号/密码错 |
| `FORBIDDEN` | 403 | 不是资源所属人，或没有管理权限 |
| `NOT_VERIFIED` | 403 | 需要校园认证 |
| `USER_BANNED` | 403 | 已封禁，`data.reason`、`data.until` |
| `ORDER_RESTRICTED` | 403 | 超时未付次数过多，`data.until` |
| `PASSWORD_CHANGE_REQUIRED` | 403 | 管理员须先改初始密码 |
| `NOT_FOUND` | 404 | 不存在或不可见 |
| `CONFLICT` | 409 | 请求处理中或并发冲突 |
| `ALREADY_VERIFIED` | 409 | 已经认证 |
| `VERIFICATION_PENDING` | 409 | 认证审核中 |
| `STUDENT_NO_TAKEN` | 409 | 学号已被绑定 |
| `USERNAME_TAKEN` | 409 | 用户名已被占用 |
| `EMAIL_TAKEN` | 409 | 邮箱已被注册 |
| `GOODS_NOT_EDITABLE` | 409 | 当前商品状态不能改 |
| `GOODS_NOT_AVAILABLE` | 409 | 已被预约、售出或下架。可带 `myOrderId` |
| `CANNOT_BUY_OWN_GOODS` | 409 | 不能买自己的商品 |
| `PENDING_ORDER_LIMIT` | 409 | 待支付单已达 3 笔 |
| `ORDER_STATUS_INVALID` | 409 | 订单状态不允许该操作 |
| `ORDER_EXPIRED` | 409 | 订单已超时 |
| `PAYMENT_NOT_PAYABLE` | 409 | 支付单已付款、关闭或过期 |
| `DISPUTE_NOT_OPEN` | 409 | 没有进行中的申诉 |
| `ALREADY_REVIEWED` | 409 | 已评价 |
| `REVIEW_WINDOW_CLOSED` | 409 | 超过评价期 |
| `CANNOT_CHAT_WITH_SELF` | 409 | 不能和自己聊天 |
| `DUPLICATE_REPORT` | 409 | 24 小时内已举报该目标 |
| `VERIFICATION_NOT_PENDING` | 409 | 认证申请已被处理 |
| `GOODS_IN_DISPUTE` | 409 | 商品订单在申诉中 |
| `REPORT_ALREADY_HANDLED` | 409 | 举报已处理 |
| `FILE_TOO_LARGE` | 413 | 超过 5 MB |
| `RATE_LIMITED` | 429 | 看 `Retry-After` |
| `LOGIN_LOCKED` | 429 | 登录失败次数过多已锁定 |
| `INTERNAL_ERROR` | 500 | 服务器错误 |
| `UPSTREAM_UNAVAILABLE` | 503 | 微信等上游不可用 |

支付回调的 `EXPIRED_TIMESTAMP`、`INVALID_SIGN`、`REPLAYED`、`PAYMENT_NOT_FOUND`、`AMOUNT_MISMATCH` 只出现在给网关的响应里。

攻防时至少要看到这些结果：下单带 `price` 被拒绝且无订单；并发抢一件只有一单成功；伪造或改金额的回调不改变订单；A 的 token 不能读 B 的订单和会话；`sort` 不能注入；标题里的 `<script>` 原样保存但不执行；服务器上 `dev-login` 为 404；登出和封禁后旧 token 失效。

## 13. 待确认

已定：模拟网关、域名 `simpleyi.site`、库名 `ershou`、小程序默认连服务器。当前登录走学号 + 密码；微信登录等 AppID。

未定：管理端是网页还是小程序页；学号格式是否收紧到本校规则；图片放磁盘还是对象存储。

## 变更记录

| 版本 | 日期 | 说明 |
| --- | --- | --- |
| v0.1 | 2026-09-29 | 初稿 |
| v0.2 | 2026-09-29 | 小程序默认连 `https://simpleyi.site`；库名 `ershou` |
| v0.3 | 2026-09-29 | 压缩篇幅。接口、字段、状态机、签名和错误码不变 |
| v0.4 | 2026-09-30 | 注册改为学号+用户名+邮箱验证码，登录改为学号+密码；本批实现 auth / me / meta / 分类 / 楼栋；表结构见 `sql/` |
| v0.5 | 2026-10-05 | 资料、认证提交、上传、商品浏览/发布/收藏/浏览记录已实现并部署到 `simpleyi.site`。参数见 `STATUE.md`。订单、支付、聊天、管理端未做 |
| v0.6 | 2026-10-08 | 管理员独立成 `admins` 表。已实现管理员登录、登出、当前管理员、改密。审核接口仍未做 |
| v0.7 | 2026-10-10 | 管理员可看待办、审核校园认证（通过/驳回）。用户可拉通知并标已读。角标里的未读通知改为真实数量。已部署到 `simpleyi.site` |
| v0.8 | 2026-10-10 | 订单、模拟支付、面交、申诉、评价、聊天 HTTP/WebSocket、举报，以及用户封禁、商品下架/恢复、分类楼栋维护、审计查询。角标和评分改为真实数据。`wx-login` 仍未做。已部署到 `simpleyi.site` |
