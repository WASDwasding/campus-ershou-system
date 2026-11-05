# 校园二手交易平台 · Base API

v1.6.1（2026-11-04）。商品、订单、支付、聊天、申诉、举报和 AI 仍按《软件需求规格说明书 V1.6》书写。登录按组内决定：保留学号注册和学号密码登录，并增加微信登录。技术栈：Spring Boot 3.4 / Java 17、MySQL 8、Redis、微信小程序。

线上 `https://simpleyi.site` 仍是改正前的进程，已部署行为见 [STATUE.md](./STATUE.md)。本文是下一版约定。改代码前以本文为准；与本文冲突的旧字段、旧状态机和旧路径停止使用。

## 1. 范围

**本期要做：**

- 学号注册登录，以及微信登录。学号注册走邮箱验证码和密码；微信按 openid 找回或建立用户。已有学号账号可以再绑定微信，之后两种方式进入同一个用户。
- 校园身份人工审核。学生提交姓名、学号、学院、楼栋和校园卡照片。未通过只能浏览。学号注册的账号，认证时填写的学号必须与注册学号一致。
- 手填发布，或先要 AI 草稿再确认。按关键词（含卖家确认的同义叫法）、楼栋、价格区间查找。
- 议价建议。建议价不进订单。失败时提示「暂无建议价」。
- 下单只提交商品编号和数量 1。成交价由服务端按商品编号读取。同一商品同时只有一笔未结束订单。满 15 分钟未支付则释放。
- 支付沙箱。只有核验通过的成功通知才能进入待面交。取消或超时后的成功通知记模拟退回。
- 待面交和申诉中，本单买卖双方在订单里发文字，约定面交时间和校内地点。双方都确认后订单完成。
- 待面交可申诉。管理员结案只有取消订单、继续完成、驳回。
- 通知反映订单、支付、聊天、申诉和本人举报。通知里不能回复。
- 已认证学生在商品详情举报该商品。管理员下架或驳回，并写理由。

**本期不做，旧约定里的这些路径撤下：**

| 撤下 | 原因 |
| --- | --- |
| `PUT/DELETE /api/goods/{id}/favorite`、`GET /api/me/favorites` | 本期不做收藏 |
| `GET/DELETE /api/me/history` | 本期不做浏览记录 |
| `POST /api/orders/{id}/review`、`GET /api/users/{id}/reviews`、主页 `rating` | 本期不做评价、信用分、差评 |
| `POST /api/im/conversations` 以及按 `goodsId` 开会话 | 下单前不能聊天。聊天只挂在订单上 |
| 聊天图片、`purpose=IM` | 订单聊天只发文字 |
| `PUT /api/orders/{id}/meetup` | 时间和校内地点写在订单聊天里，不另存一套面交字段 |
| `POST /api/ai/bargain` | 本期只有参考价，不做议价话术 |
| 举报用户、举报消息、警告、封号 | 举报只做到商品详情。封号会使账号不能登录，和说明书的处罚不是一回事 |
| 待支付最多 3 笔、超时 3 次禁止下单 | 说明书没有这两条限制 |
| 模拟发票 `/api/orders/{id}/invoice` | V1.6 不验收 |
| 真实支付、站内钱包、真实退款、微信订阅消息、账号注销、自然语言搜索、求购、候补、学校统一身份接口 | 本期不做 |

已支付之后，买家和卖家都不能取消。已支付订单只有管理员在申诉里选择「取消订单」时才结束，支付状态改为模拟退回，不发生真实退款。

## 2. 环境

| 环境 | 谁用 | 地址 | 数据 |
| --- | --- | --- | --- |
| server | 小程序、Apifox、展示 | `https://simpleyi.site`，WebSocket `wss://simpleyi.site/ws/im` | 库 `ershou`；Redis 前缀 `campus:` |
| local | 只给后端在本机写接口 | `http://127.0.0.1:8080` | 本机库 `campus_ershou`。推荐 `sql/start-local-mysql.sh`（端口 3307）。前端不要连 |

`campus-trade/miniprogram/utils/config.js` 默认是服务器地址。后端临时改成 `127.0.0.1` 时不要提交。开发者工具勾选「不校验合法域名」。

服务器：Spring Boot 只听 `127.0.0.1:8080`，Nginx 转发 `/api/`、`/ws/`、`/files/public/`。部署是替换 `/opt/campus-ershou/campus-ershou.jar` 后 `sudo systemctl restart campus-ershou`。库账号只允许本机连接。只信 Nginx 写入的 `X-Real-IP`。

密钥放服务器或本机的 `application-local.yml`，不进仓库：数据库、Redis、`app.jwt.secret`（≥32 字节）、`app.student-no.hmac-key`、`wx.app-id`、`wx.app-secret`、`pay.mock.secret`、`file.sign-key`、`admin.init-password`。

MySQL 保存用户、商品、订单、商品快照、聊天消息、支付记录、支付通知、申诉、举报和通知。Redis 用于会话、注册验证码、商品列表热点缓存，以及下单短锁。业务状态以 MySQL 为准，重启后仍可查询。

| 开关 | local | server |
| --- | --- | --- |
| `POST /api/auth/dev-login` | 开。只给本机造用户，不进入验收 | 404 |
| OpenAPI 页面 | 开 | 关 |
| 模拟支付 | 开。页面标明「模拟」 | 同左 |
| AI 上游 | 可关。关闭、超过 10 秒或失败：草稿 `filled=false`；建议价 `available=false`，提示「暂无建议价」 | 同左。模型密钥只放服务器配置 |
| 错误响应带异常栈 | 开 | 关，只返回错误码和 traceId |

## 3. 通用约定

- JSON，`camelCase`。上传用 `multipart/form-data`。小程序不用 `PATCH`。查询里多个值用逗号。多出来的查询参数忽略。
- 登录头：`Authorization: Bearer <token>`。JWT 固定 HS256，拒绝 `alg: none`。载荷：`sub`、`typ`（`user` / `admin`）、`ver`、`jti`、`iat`、`exp`。用户 7 天，管理员 12 小时。两种 token 不能互调。会话是否仍有效以 Redis 为准。登出后旧 token 失效。401 时重新登录（学号密码或微信），只重试一次。
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
| 403 | 已登录但无权、未认证、禁止再发布 |
| 404 | 不存在，或存在但你看不到 |
| 409 | 状态冲突 |
| 413 | 文件过大 |
| 429 | 限流或当日 AI 次数用尽，响应头 `Retry-After` 为秒 |
| 500 / 503 | 服务器错误 / 微信或模型上游不可用 |

- 分页：`page` 从 1，`size` 默认 20、最大 50。响应 `list`、`page`、`size`、`total`、`hasMore`。聊天记录用消息 ID 做游标。
- 时间：`2026-09-29T10:15:30+08:00`。金额：整数，单位分。`1500` = 15.00 元。前端不计算、不回传成交价。
- ID：用户/商品/订单等是自增整数。`orderNo` 为 `O` + 时间 + 4 位随机数，`paymentNo` 为 `P` 开头，`notifyNo` 为 `N` 开头，`fileId` 为 `f_` + 随机串。按 ID 取数据必须先查归属。
- 枚举用大写英文，中文名由 `GET /api/meta` 下发。前端遇到不认识的值显示「未知」。商品 `RESERVED`、`MEETUP`、`DISPUTED` 在页面上统一显示「交易进行中」，接口仍返回这三个值。
- **请求体出现文档没定义的字段：400 `UNKNOWN_FIELD`。** 字段名是 `price`、`amount`、`paid`、`status`、`orderStatus`、`paymentStatus`、`stock`、`sellerId`、`buyerId`、`userId`、`role`、`suggestedPrice`、`result` 时再记审计 `TAMPER_FIELD`，订单、支付和商品状态都不变。当前用户只从 token 取。
- 幂等：下单和创建支付单可带 `Idempotency-Key`（8–64 位）。同一用户、同一键 10 分钟内：成功过就返回原结果；还在处理返回 409 `CONFLICT`。发消息用 `clientMsgId`。
- 文本按字符计，先 trim。原样保存。小程序用 `<text>`，不用 `rich-text`。
- 联系方式（先做 NFKC，全角转半角）出现在商品标题、描述、同义叫法、交易说明、昵称、简介、申诉说明里：400 `CONTACT_INFO_NOT_ALLOWED`。订单聊天同样拒绝，不入库，不保留原文。

```text
手机号：(?<!\d)1[3-9]\d(?:[\s-]?\d){8}(?!\d)
微信号：(?:微信|威信|薇信|v信|vx|wx|weixin|wechat|加v|\+v)[\s:：]*[a-z][-_a-z0-9]{5,19}
QQ：   (?:qq|扣扣|企鹅)[\s:：]*[1-9]\d{4,10}
邮箱：  [a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}
```

| 限流 | 上限 |
| --- | --- |
| 发送注册验证码 | 每 IP 每分钟 10 次，同一邮箱 60 秒一次 |
| 学号密码登录失败 | 15 分钟 5 次后锁 15 分钟 |
| 微信登录 | 每 IP 每分钟 10 次 |
| 管理员登录失败 | 15 分钟 5 次后锁 15 分钟 |
| 提交校园认证 | 每人每天 3 次 |
| 上传 | 每人每小时 60 个 |
| 发布商品 | 每人每天 20 件 |
| 编辑商品 | 每人每小时 60 次 |
| 商品列表 | 每人每分钟 120 次 |
| 下单 | 每人每小时 10 单 |
| 创建支付单 / 模拟付款 | 各每人每小时 20 次 |
| AI 草稿 | 北京时间每个自然日 20 次，成功和失败都计 |
| AI 建议价 | 北京时间每个自然日 20 次，成功和失败都计 |
| 发消息 | 每人每分钟 30 条 |
| 举报 | 每人每天 10 次 |

| 谁 | 能做什么 |
| --- | --- |
| 公开 | 健康检查、元数据、楼栋、分类、学号注册三步、学号密码登录、微信登录、管理员登录 |
| 登录且未认证 | 浏览商品、改自己的资料、看通知、提交校园认证、看建议价 |
| 已认证（`verifyStatus=VERIFIED`） | 在上一行之外：发布、下单、支付、确认面交、申诉、举报、发订单聊天、要商品草稿 |
| `publishBanned=true` | 仍可登录和买东西。`POST /api/goods` 与重新上架返回 403 `PUBLISH_BANNED` |
| 资源方 | 在上一级之外，还必须是卖家本人、该订单的买家或卖家 |
| 管理员 | 只认管理员 token。申诉进行中才能读该订单聊天，不能代发 |

别人的订单、支付单、聊天、私有文件：403，响应里没有业务正文，记 `ACCESS_DENIED`。别人的在售、已预约、待面交、申诉中、已售出商品可看。别人已下架、管理员下架或已删除的商品：404。

底线：MyBatis 只用 `#{}`。`ORDER BY` 用白名单。`LIKE` 转义 `%`、`_`、`\`。上传按文件头判断类型，转码，去掉 EXIF。日志不记 token、密码、密钥、完整学号、openid、聊天原文、模型原文和填表说明。

## 4. 接口清单

| 模块 | 方法 | 路径 | 权限 |
| --- | --- | --- | --- |
| 系统 | GET | `/api/health` | 公开。不走统一包：`{"service":"campus-ershou","status":"UP"}` |
| 系统 | GET | `/api/meta` | 公开 |
| 基础 | GET | `/api/campus/buildings?type=&meetupOnly=` | 公开 |
| 基础 | GET | `/api/categories` | 公开。只有一级 |
| 认证 | POST | `/api/auth/register` | 公开。`{ studentNo, username, email }` |
| 认证 | POST | `/api/auth/register/verify-code` | 公开。`{ email, code }` |
| 认证 | POST | `/api/auth/register/set-password` | 公开。`{ setupToken, password }`，成功即登录 |
| 认证 | POST | `/api/auth/login` | 公开。`{ studentNo, password }` |
| 认证 | POST | `/api/auth/wx-login` | 公开。`{ code }` |
| 认证 | POST | `/api/auth/wx-bind` | 登录。`{ code }`，把微信绑到当前账号 |
| 认证 | POST | `/api/auth/logout` | 登录 |
| 认证 | POST | `/api/auth/dev-login` | 仅 local。`{ "devOpenid" }` 匹配 `^dev-[a-z0-9-]{3,32}$`。不进入验收 |
| 认证 | GET | `/api/auth/campus-verification` | 登录 |
| 认证 | POST | `/api/auth/campus-verification` | 登录，且未认证或已驳回 |
| 用户 | GET | `/api/me` | 登录 |
| 用户 | PUT | `/api/me/profile` | 登录。只改传入字段 |
| 用户 | GET | `/api/me/stats` | 登录 |
| 用户 | GET | `/api/me/badges` | 登录。未读角标，可 30 秒轮询 |
| 用户 | GET | `/api/users/{userId}` | 登录。公开主页 |
| 用户 | GET | `/api/users/{userId}/goods?status=` | 登录。`status` 只能 `ON_SALE` 或 `SOLD` |
| 文件 | POST | `/api/files` | 见 §6。`file` + `purpose` |
| 文件 | GET | `/api/files/{fileId}/url` | 资源方。私有图 10 分钟临时地址 |
| 文件 | GET | `/api/files/private/{fileId}?size=&exp=&sig=` | 地址签名。给 `<image>` 用 |
| 商品 | GET | `/api/goods` | 登录。默认只返回在售 |
| 商品 | GET | `/api/goods/{goodsId}` | 登录 |
| 商品 | POST | `/api/goods` | 已认证，且未被禁止再发布 |
| 商品 | PUT | `/api/goods/{goodsId}` | 已认证且是卖家。仅在售或已下架。整体替换 |
| 商品 | POST | `/api/goods/{goodsId}/off-shelf` | 卖家。仅在售 |
| 商品 | POST | `/api/goods/{goodsId}/on-shelf` | 已认证、是卖家、未被禁止再发布。仅已下架 |
| 商品 | DELETE | `/api/goods/{goodsId}` | 卖家。没有未结束订单时软删除 |
| 商品 | GET | `/api/me/goods?status=` | 登录 |
| 订单 | POST | `/api/orders` | 已认证。`{ "goodsId", "quantity": 1 }` |
| 订单 | GET | `/api/orders/{orderId}` | 买家或卖家 |
| 订单 | GET | `/api/me/orders?role=&status=` | 登录。`role` 为 `BUYER`（默认）或 `SELLER` |
| 订单 | POST | `/api/orders/{orderId}/cancel` | 买家或卖家。仅已预约且待支付。请求体 `{}` |
| 订单 | POST | `/api/orders/{orderId}/handover-confirm` | 买家或卖家。仅待面交 |
| 订单 | POST | `/api/orders/{orderId}/dispute` | 买家或卖家。仅待面交 |
| 订单 | POST | `/api/orders/{orderId}/dispute/withdraw` | 申诉发起人。进行中的申诉 |
| 订单 | GET | `/api/orders/{orderId}/messages` | 买家或卖家。阶段见 §10 |
| 订单 | POST | `/api/orders/{orderId}/messages` | 买家或卖家。仅待面交和申诉中 |
| 支付 | POST | `/api/orders/{orderId}/payments` | 买家。`{ "channel": "MOCK" }` |
| 支付 | GET | `/api/payments/{paymentNo}` | 买家 |
| 支付 | POST | `/api/pay/mock/{paymentNo}/confirm` | 买家。请求体 `{}`。由沙箱发起通知，调用方不提交支付结果 |
| 支付 | POST | `/api/pay/notify/mock` | 签名，不用 token |
| AI | POST | `/api/ai/goods-draft` | 已认证。草稿不写商品 |
| AI | POST | `/api/ai/quotes` | 登录。参考价，不写订单 |
| 聊天 | WS | `/ws/im` | 登录 |
| 通知 | GET | `/api/notifications?unreadOnly=` | 登录 |
| 通知 | POST | `/api/notifications/read` | 登录。`{ "ids": [] }` 或 `{ "all": true }` |
| 举报 | POST | `/api/reports` | 已认证。只举报商品 |
| 举报 | GET | `/api/me/reports` | 登录。只看自己的 |
| 管理 | POST | `/api/admin/auth/login` | 公开 |
| 管理 | POST | `/api/admin/auth/logout` | 管理员 |
| 管理 | POST | `/api/admin/auth/password` | 管理员。`oldPassword`、`newPassword`（≥12 位） |
| 管理 | GET | `/api/admin/me` | 管理员 |
| 管理 | GET | `/api/admin/dashboard` | 管理员。待办计数 |
| 管理 | GET | `/api/admin/verifications?status=` | 管理员 |
| 管理 | GET | `/api/admin/verifications/{id}` | 管理员。含姓名、学号、学院、楼栋、证件 |
| 管理 | POST | `/api/admin/verifications/{id}/approve` | 管理员 |
| 管理 | POST | `/api/admin/verifications/{id}/reject` | 管理员。`{ "reason" }` 2–100 字 |
| 管理 | GET | `/api/admin/users?keyword=&status=&verifyStatus=` | 管理员 |
| 管理 | GET | `/api/admin/users/{userId}` | 管理员 |
| 管理 | GET | `/api/admin/goods?status=&keyword=&sellerId=` | 管理员。含 `DELETED` |
| 管理 | POST | `/api/admin/goods/{goodsId}/remove` | 管理员。`{ "reason" }` 2–100 字。不取消未结束订单 |
| 管理 | POST | `/api/admin/goods/{goodsId}/restore` | 管理员。`REMOVED → OFF_SHELF` |
| 管理 | GET | `/api/admin/orders?status=&orderNo=&buyerId=&sellerId=` | 管理员。不含聊天正文 |
| 管理 | GET | `/api/admin/orders/{orderId}` | 管理员。含状态流水、快照、支付通知。不含聊天正文，不能改状态 |
| 管理 | GET | `/api/admin/disputes?status=` | 管理员 |
| 管理 | GET | `/api/admin/disputes/{id}/messages` | 管理员。仅该申诉 `OPEN` 时可看，只读 |
| 管理 | POST | `/api/admin/disputes/{id}/resolve` | 管理员。`CANCEL`、`COMPLETE` 或 `REJECT` |
| 管理 | GET | `/api/admin/reports?status=` | 管理员 |
| 管理 | GET | `/api/admin/reports/{id}` | 管理员 |
| 管理 | POST | `/api/admin/reports/{id}/handle` | 管理员。`REMOVE_GOODS` 或 `DISMISS`，都必须写理由 |
| 管理 | GET | `/api/admin/audit-logs?category=&event=&userId=&ip=&from=&to=` | 管理员 |
| 管理 | GET、POST | `/api/admin/categories` | 管理员 |
| 管理 | PUT | `/api/admin/categories/{id}` | 管理员。不提供删除，停用即可 |
| 管理 | GET、POST | `/api/admin/buildings` | 管理员 |
| 管理 | PUT | `/api/admin/buildings/{id}` | 管理员 |

主流程：学号注册或微信登录 → 提交姓名、学号、学院、楼栋、校园卡 → 管理员通过 → 卖家手填或确认草稿后发布 → 买家浏览（详情没有聊天）→ 下单 → 沙箱付款被采纳 → 待面交后在本单聊天里约定时间和地点 → 双方确认面交。待面交可申诉，管理员在取消、继续完成、驳回里选一个。已用学号注册的用户，可以在登录后再绑定微信。

## 5. 枚举与上限

`GET /api/meta` 的 `enums.*.value/label` 与 `limits`：

| 枚举 | 值 |
| --- | --- |
| goodsCondition | `NEW` 全新，`LIKE_NEW` 几乎全新，`GOOD` 轻微使用痕迹，`FAIR` 明显使用痕迹，`POOR` 有瑕疵但可用 |
| goodsStatus | `ON_SALE` 在售，`RESERVED` 已预约，`MEETUP` 待面交，`DISPUTED` 申诉中，`SOLD` 已售出，`OFF_SHELF` 已下架，`REMOVED` 管理员下架，`DELETED` 已删除（仅管理端） |
| orderStatus | `PENDING_PAY` 已预约，`PAID` 待面交，`COMPLETED` 已完成，`CANCELLED` 已取消，`DISPUTED` 申诉中 |
| paymentStatus | `PENDING` 待支付，`PAID` 已支付，`CANCELLED` 已取消，`MOCK_REFUNDED` 模拟退回 |
| userStatus | `ACTIVE` 正常 |
| verifyStatus | `UNVERIFIED` 未认证，`PENDING` 审核中，`VERIFIED` 已认证，`REJECTED` 已驳回 |
| buildingType | `DORM`，`TEACHING`，`CANTEEN`，`LIBRARY`，`GATE`，`OTHER` |
| disputeReason | `GOODS_MISMATCH` 货不对版，`FALSE_DESCRIPTION` 虚假描述，`SUSPECTED_FRAUD` 疑似欺诈，`MISSED_MEETUP` 未按约定面交 |
| disputeStatus | `OPEN` 处理中，`WITHDRAWN` 已撤回，`REJECTED` 已驳回，`RESOLVED` 已结案 |
| disputeResult | `CANCEL` 取消订单，`COMPLETE` 继续完成，`REJECT` 驳回。撤回没有 result，状态是 `WITHDRAWN` |
| reportStatus | `PENDING` 待处理，`DISMISSED` 已驳回，`REMOVED` 已下架 |
| messageType | `TEXT` 文字 |

limits：`payTimeoutMinutes` 15，`priceMin` 1，`priceMax` 9999900，标题 2–30，描述 1–500，同义叫法每条 1–20、最多 5 条，交易说明 100，商品图 1–9 张、单张 5242880 字节，昵称 16，简介 60，消息 1–500，申诉说明 10–500、证据最多 3 张，举报说明 1–300、证据最多 3 张，姓名 1–20，学院 1–30，`aiDailyDraft` 20，`aiDailyQuote` 20，`aiTimeoutSeconds` 10。

楼栋：`id`、`name`、`areaId`、`areaName`、`type`、`meetupPoint`、`sort`。停用的不返回。「同区」比 `areaId`。分类：`id`、`name`、`icon`、`sort`。`icon` 对应小程序本地图片。初始分类：教材书籍、数码电子、宿舍电器、生活日用、运动户外、服饰鞋包、考研考证、其他。

通知类型：`VERIFICATION_APPROVED`、`VERIFICATION_REJECTED`、`ORDER_CREATED`（卖家）、`ORDER_PAID`（卖家，表示已进入待面交）、`ORDER_CANCELLED`、`PAYMENT_MOCK_REFUNDED`、`ORDER_COMPLETED`、`DISPUTE_OPENED`、`DISPUTE_WITHDRAWN`、`DISPUTE_RESOLVED`、`CHAT_MESSAGE`、`REPORT_HANDLED`（只发给提交人）、`GOODS_REMOVED`。列表项含 `title`、`content`、`ref: { type: ORDER|GOODS|REPORT, id }`、`read`、`createdAt`。`CHAT_MESSAGE` 的 `ref` 是该订单，点进去打开这单的聊天。通知列表本身不能回复。

`PAYMENT_MOCK_REFUNDED` 的 `content` 写明原因。迟到支付的原因固定为「取消或释放后的迟到支付」。

## 6. 认证、用户、文件

学生有两种登录方式，进的是同一个用户。

**学号注册。** `POST /api/auth/register` 请求 `{ "studentNo", "username", "email" }`。学号 `^\d{8,12}$`。用户名 1–16 个字符，只含中文、字母、数字和下划线，且不能含联系方式。邮箱转成小写，最长 128。学号、用户名、邮箱任一已被占用：409，分别是 `STUDENT_NO_TAKEN`、`USERNAME_TAKEN`、`EMAIL_TAKEN`。成功时发 6 位验证码，响应 `{ "emailMasked", "expiresInSeconds" }`，验证码 10 分钟有效。同一邮箱 60 秒内不能重发。本机在邮件未开启且 `expose-code` 为 true 时，响应多一个 `debugCode`；服务器不返回这个字段。

**校验验证码。** `POST /api/auth/register/verify-code` 请求 `{ "email", "code" }`。成功返回 `{ "setupToken", "expiresInSeconds" }`，凭证 15 分钟有效。验证码错误：400 `INVALID_CODE`，同一封最多错 5 次，超过后该码作废。没有待验证记录、已作废或已过期：400 `CODE_EXPIRED`。

**设置密码并登录。** `POST /api/auth/register/set-password` 请求 `{ "setupToken", "password" }`。密码 8–64 位。凭证无效或过期：400 `SETUP_TOKEN_INVALID`。成功写入用户，`verifyStatus=UNVERIFIED`，`publishBanned=false`，并直接登录。响应 `{ "token", "expiresAt", "isNewUser": true, "user" }`。学号只存 HMAC 和脱敏串，密码只存 BCrypt。

**学号密码登录。** `POST /api/auth/login` 请求 `{ "studentNo", "password" }`。成功响应同上，`isNewUser` 为 false。学号不存在、密码不对、或该账号还没设过密码：一律 401 `BAD_CREDENTIALS`，响应不说明是哪一种。15 分钟内失败 5 次后锁定 15 分钟，429 `LOGIN_LOCKED`，响应头 `Retry-After` 为剩余秒数。

**微信登录。** `POST /api/auth/wx-login` 请求 `{ "code" }`。服务端用 code 向微信换 openid。这个 openid 已经绑在某个用户上：登录该用户，`isNewUser` 为 false。还没有绑定：新建未认证用户，不写学号、邮箱和密码，`isNewUser` 为 true，`verifyStatus=UNVERIFIED`，`publishBanned=false`。服务端生成用户名，昵称与用户名相同，随后可在资料里改昵称。code 无效或已用过：400 `WX_LOGIN_FAILED`。微信不可用：503 `UPSTREAM_UNAVAILABLE`。openid 只落库，任何接口都不返回 openid，也不返回微信号。

**绑定微信。** 已经用学号注册的用户，登录后调用 `POST /api/auth/wx-bind`，请求 `{ "code" }`。服务端把这次 code 对应的 openid 写到当前用户，之后该微信登录进入这个账号。成功 `{ "wxBound": true }`。openid 已经属于当前用户：同样返回成功，不新建用户。openid 属于别人，或当前用户已经绑了另一个微信：409 `WX_ALREADY_BOUND`，绑定保持原样。

只通过微信建立、还没有设密码的用户，用微信登录。学号密码登录不会进入这种账号。

`dev-login` 仅本机，且 `app.dev-login.enabled=true`。它按 `devOpenid` 造用户，不能代替学号登录或微信登录。

**登出** `POST /api/auth/logout`。令牌版本加一，当前会话从 Redis 失效。

**校园认证** `POST /api/auth/campus-verification`：

| 字段 | 规则 |
| --- | --- |
| realName | 必填，1–20 |
| studentNo | 必填，`^\d{8,12}$`。注册时已经有学号的，必须填同一个学号，填别的返回 400，`fieldErrors` 指向 `studentNo`。还没有学号的（仅微信建立），在这里写入并占用该学号 |
| college | 必填，1–30 |
| dormBuildingId | 必填，启用中的 `DORM` |
| cardImageFileId | 必填，用途 `VERIFICATION` |

学号已属于其他用户：409 `STUDENT_NO_TAKEN`。注册和认证都会占用学号，后提交的一方得到这个错误。已认证：409 `ALREADY_VERIFIED`。审核中：409 `VERIFICATION_PENDING`。驳回后可再交。进度在未提交时 `status=UNVERIFIED`。本人进度返回状态、驳回原因、提交时间、宿舍楼，不返回证件地址。

管理员详情返回 `realName`、完整 `studentNo`、`college`、宿舍楼、校园卡临时地址。通过后 `verifyStatus=VERIFIED`。驳回须写 2–100 字原因，`verifyStatus=REJECTED`。已处理再审：409 `VERIFICATION_NOT_PENDING`。证件照审核结束 30 天后删除。公开接口不返回宿舍号、完整学号、姓名和学院。

**资料** `GET /api/me`：`id`、`username`、`nickname`、`avatarUrl`、`bio`、`status`、`verifyStatus`、`realName`、`college`、`studentNoMasked`、`emailMasked`、`wxBound`、`dormBuilding`、`publishBanned`、`createdAt`。没有邮箱时 `emailMasked` 为 null。没有微信号字段，也不返回 openid。

`PUT /api/me/profile` 可改 `nickname`（1–16）、`avatarFileId`、`bio`（≤60，`null` 清空）、`dormBuildingId`（必须是 `DORM`，`null` 清空）。不能改姓名、学号、学院，也不能用外部图片 URL。

别人的主页：`id`、`nickname`、`avatarUrl`、`bio`、`verified`、`relation.sameBuilding`、`relation.sameArea`、`soldCount`、`onSaleCount`。没有评分、学号、楼名、姓名、学院。

`GET /api/me/stats`：`onSaleCount`、`offShelfCount`、`soldCount`、`pendingOrderCount`、`sellingOrderCount`。`GET /api/me/badges`：`unreadMessages`、`unreadNotifications`、`buyerTodos`。

**上传** 只收 JPEG、PNG、WebP，≤5 MB。按文件头判断，转成 JPEG（最长边 1600，缩略图 400），去掉 EXIF，文件名随机。公开用途直接给 URL；私有用途的 URL 是 10 分钟临时地址，业务里只存 `fileId`。引用 `fileId` 必须同时满足：存在、本人上传、用途匹配，否则 400 `FILE_NOT_USABLE`。私有图签名：`HMAC-SHA256(file.sign-key, fileId + "|" + size + "|" + exp)`，`size` 为空或 `thumb`。

| purpose | 谁能传 | 谁能看 |
| --- | --- | --- |
| `GOODS` | 已认证 | 公开 |
| `AVATAR` | 登录 | 公开 |
| `VERIFICATION` | 未认证或已驳回 | 本人和管理员 |
| `EVIDENCE` | 已认证 | 申诉材料：订单双方和管理员。举报材料：提交人和管理员 |

## 7. 商品

库存固定为 1，发布时服务端写入。状态只能走下表，并且 `UPDATE ... WHERE status = '原状态'`，影响 0 行就 409。

| 状态 | 谁能看 | 卖家能否编辑或下架 | 能否买 |
| --- | --- | --- | --- |
| `ON_SALE` | 登录用户 | 能 | 已认证且不是卖家 |
| `RESERVED` | 登录用户 | 否 | 否 |
| `MEETUP` | 登录用户 | 否 | 否 |
| `DISPUTED` | 登录用户 | 否 | 否 |
| `SOLD` | 登录用户 | 否 | 否。再卖要新建商品 |
| `OFF_SHELF` | 只有卖家 | 能编辑；上架另受 `publishBanned` 约束 | 否 |
| `REMOVED` | 卖家（带原因）和管理员 | 否 | 否 |
| `DELETED` | 只有管理员 | 否 | 否 |

转移：

| 事件 | 商品 |
| --- | --- |
| 发布或上架 | `ON_SALE` |
| 下单成功 | `RESERVED` |
| 未支付取消或超时释放 | `ON_SALE` |
| 支付成功通知被采纳 | `MEETUP`。商品仍由这一单占用 |
| 提交申诉 | `DISPUTED` |
| 撤回或管理员驳回 | `MEETUP` |
| 双方确认面交，或管理员在双方都已确认时继续完成 | `SOLD` |
| 管理员取消且不是疑似欺诈 | `ON_SALE` |
| 疑似欺诈且管理员取消 | `OFF_SHELF`，该卖家 `publishBanned=true` |
| 卖家自己下架 | `OFF_SHELF`。仅 `ON_SALE` |
| 管理员因举报或直接下架 | `REMOVED`。未结束订单保持原状态 |
| 管理员恢复 | `OFF_SHELF` |
| 卖家删除 | `DELETED`。存在未结束订单时 409 `GOODS_NOT_EDITABLE` |

列表项：`id`、`title`、`coverUrl`、`price`、`originalPrice`、`condition`、`status`、`category`、`meetupBuilding`、`seller`（含 `relation`）、`createdAt`。详情另有 `description`、`aliases`、`tradeNote`、`images[]`、`viewCount`、`seller.verified`、`viewer`（`isMine`、`canBuy`、`activeOrderId`）、`removedReason`（仅卖家）、`updatedAt`。

详情里的标题、描述、标价、图片都是卖家确认后的内容。响应里没有 `suggestedPrice`、没有模型草稿、没有 `canChat`。建议价只走 §12，页面和标价分开，并标明「仅供参考」。`activeOrderId` 仅当查看者是该商品未结束订单的买家时返回。

列表参数：`keyword`（1–30）、`categoryId`、`condition`、`minPrice`、`maxPrice`、`meetupBuildingId`、`areaId`、`sort`。`keyword` 匹配标题、描述和卖家确认过的 `aliases`。卖家给「高等数学」确认了同义叫法「高数」时，搜「高数」要能命中。`sort` 只允许 `latest`（默认）、`price_asc`、`price_desc`、`nearby`。`nearby`：面交点与我的宿舍同楼优先，同区其次；没填宿舍则按 `latest`。非法 `sort`：400。

发布和编辑的请求体：

| 字段 | 规则 |
| --- | --- |
| title | 必填，2–30，无联系方式 |
| description | 必填，1–500，无联系方式 |
| categoryId | 必填，启用中的分类 |
| condition | 必填 |
| price | 必填，1–9999900 分。这是标价 |
| originalPrice | 可选，1–99999900。编辑时 `null` 清空 |
| imageFileIds | 1–9，不重复，用途 `GOODS`。第一张是封面 |
| meetupBuildingId | 必填，且 `meetupPoint=true`。这是商品的面交楼栋，不是订单上的约定时间 |
| aliases | 可选，最多 5 条。每条 1–20，无联系方式。卖家确认后才写入。编辑时整组替换，`[]` 清空 |
| tradeNote | 可选，0–100，无联系方式。编辑时 `null` 清空 |

服务端写入 `sellerId`、`status=ON_SALE`、`stock=1`。请求里带这三个字段会被拒绝。`publishBanned=true` 时发布和上架返回 403 `PUBLISH_BANNED`，账号仍可登录。只有 `ON_SALE`、`OFF_SHELF` 能编辑。编辑不改已经写入订单的快照。

下架、上架、删除的响应只给 `id` 和 `status`。

## 8. 订单

订单状态和支付状态分开存。商品交易状态用 §7 的值。

| 当前订单 | 当前支付 | 当前商品 | 事件 | 结果 |
| --- | --- | --- | --- | --- |
| — | — | `ON_SALE` | 一名买家下单成功 | 订单 `PENDING_PAY`，支付 `PENDING`，商品 `RESERVED`。同时下单只有先成功的一笔 |
| `PENDING_PAY` | `PENDING` | `RESERVED` | 满 15 分钟没有可采纳的成功通知 | 订单 `CANCELLED`，支付 `CANCELLED`，商品 `ON_SALE`。原因：超时释放 |
| `PENDING_PAY` | `PENDING` | `RESERVED` | 买家或卖家取消 | 订单 `CANCELLED`，支付 `CANCELLED`，商品 `ON_SALE`。原因：买家取消，或卖家取消 |
| `PENDING_PAY` | `PENDING` | `RESERVED` | 金额一致且核验通过，商品仍被本单占用 | 订单 `PAID`，支付 `PAID`，商品 `MEETUP`。这是进入待面交的唯一支付路径 |
| `CANCELLED` | `CANCELLED` | `ON_SALE` | 原订单的迟到成功通知 | 订单保持 `CANCELLED`，支付改为 `MOCK_REFUNDED`，商品保持 `ON_SALE`。原因：取消或释放后的迟到支付 |
| `CANCELLED` | `CANCELLED` 或 `MOCK_REFUNDED` | 已被另一买家预约 | 原订单的迟到成功通知 | 旧单保持 `CANCELLED` 并记 `MOCK_REFUNDED`。新单和商品都不变 |
| `PAID` | `PAID` | `MEETUP` | 同一通知再次到达 | 三个状态都不变 |
| `CANCELLED` | `MOCK_REFUNDED` | 任意 | 同一迟到通知再次到达 | 不新增一条模拟退回，状态不变 |
| `PAID` | `PAID` | `MEETUP` | 双方都确认面交 | 订单 `COMPLETED`，支付保持 `PAID`，商品 `SOLD`。聊天改为只能看 |
| `PAID` | `PAID` | `MEETUP` | 提交申诉 | 订单 `DISPUTED`，支付保持 `PAID`，商品 `DISPUTED` |
| `DISPUTED` | `PAID` | `DISPUTED` | 发起人撤回，或管理员驳回 | 订单 `PAID`，支付保持 `PAID`，商品 `MEETUP` |
| `DISPUTED` | `PAID` | `DISPUTED` | 管理员继续完成，且双方都已确认面交 | 订单 `COMPLETED`，支付保持 `PAID`，商品 `SOLD` |
| `DISPUTED` | `PAID` | `DISPUTED` | 管理员取消，原因不是疑似欺诈 | 订单 `CANCELLED`，支付 `MOCK_REFUNDED`，商品 `ON_SALE` |
| `DISPUTED` | `PAID` | `DISPUTED` | 管理员取消，申诉原因是疑似欺诈 | 订单 `CANCELLED`，支付 `MOCK_REFUNDED`，商品 `OFF_SHELF`，该卖家 `publishBanned=true`，账号仍可登录 |
| 任意 | 任意 | 任意 | 学生直接要求标记已支付 | 全部拒绝，三个状态都不变 |

表里没有的转移不存在。`PAID` 时没有卖家取消，也没有买家取消。每次都是 `UPDATE ... WHERE id=? AND status='原状态'`，0 行则 409 `ORDER_STATUS_INVALID`。每次写状态流水。

未结束订单指 `PENDING_PAY`、`PAID`、`DISPUTED`。同一商品的未结束订单用唯一约束卡住，同时只允许一笔。

订单详情：`orderNo`、`status`、`myRole`（`BUYER`/`SELLER`）、`amount`、`payment`（`status`、`reason`、`amount`）、`paymentEvents`、`goods` 快照、`buyer`、`seller`、`handover`、`dispute`、`allowedActions`、`expireAt`、`paidAt`、`completedAt`、`cancelledAt`。没有面交楼栋/地点/时间字段，没有评价，没有微信号。不是买家或卖家：403。读到已过期的已预约订单时，当场按超时释放再返回。

`goods` 快照在下单时写入，之后改商品文案不改这张快照：`id`、`title`、`description`、`category`、`condition`、`price`、`images`、`meetupBuilding`、`tradeNote`、`aliases`。结案看这张快照、订单记录、聊天记录和申诉材料，不看模型草稿和建议价。

`paymentEvents`：`[{ notifyNo, arrivedAt, amount, adopted, reason }]`。每条沙箱通知一行。`adopted=true` 只出现在进入待面交的那一次。

`allowedActions` 只从下面选：`PAY`、`CANCEL`、`CONFIRM_HANDOVER`、`OPEN_DISPUTE`、`WITHDRAW_DISPUTE`。

| 订单状态 | 买家 | 卖家 |
| --- | --- | --- |
| `PENDING_PAY` | `PAY`、`CANCEL` | `CANCEL` |
| `PAID` | 未确认过则 `CONFIRM_HANDOVER`，以及 `OPEN_DISPUTE` | 同左 |
| `DISPUTED` | 自己是发起人则 `WITHDRAW_DISPUTE` | 同左 |
| `COMPLETED`、`CANCELLED` | 无 | 无 |

聊天是否能发由 §10 的 `canSend` 决定，不放进 `allowedActions`。

**下单**只收 `{ "goodsId", "quantity" }`，`quantity` 只能是 1。带成交价、订单状态或支付状态：400，不建订单。

1. 已认证；商品在售；不是自己的商品（`CANNOT_BUY_OWN_GOODS`）。
2. Redis：`SET lock:goods:{goodsId} {requestId} NX PX 5000`。拿不到锁：409 `GOODS_NOT_AVAILABLE`。
3. 同一事务：商品改为 `RESERVED`，按库内标价插入订单和快照，支付状态 `PENDING`，`expire_at` 为现在加 15 分钟。这一步不建立聊天。
4. 用 Lua 比较后删除自己的锁。通知卖家 `ORDER_CREATED`。

抢不到都返回 `GOODS_NOT_AVAILABLE`，并记 `ORDER_STOCK_CONFLICT`。进行中的订单就是我的时，`data.myOrderId` 让前端跳过去。

**取消：** 请求体 `{}`。仅 `PENDING_PAY`。服务端把原因写成「买家取消」或「卖家取消」，支付改为 `CANCELLED`，商品回到 `ON_SALE`。`PAID`、`DISPUTED`、`COMPLETED` 上调用：409 `ORDER_STATUS_INVALID`。

**超时：** 满 15 分钟仍没有可采纳的成功通知，原因写成「超时释放」，支付 `CANCELLED`，商品 `ON_SALE`。之后到达的成功通知走迟到支付，不能把订单改回 `PAID`。

**面交确认：** 无请求体，按 token 区分角色，重复确认保持原确认时间。待面交时只有一方确认，订单仍是 `PAID`，商品仍是 `MEETUP`。待面交时双方都确认后订单 `COMPLETED`，商品 `SOLD`，双方收到 `ORDER_COMPLETED`。申诉中也可以确认，只记下确认时间，不因此自动完成。时间和地点由双方在 §10 的文字里约定，本接口不接收楼栋、地点和时间。

**申诉：** `{ "reason", "description", "evidenceFileIds" }`。`reason` 只能是四种枚举。说明 10–500 字，无联系方式。`GOODS_MISMATCH` 和 `FALSE_DESCRIPTION` 的 `evidenceFileIds` 至少 1 张，否则 400，`fieldErrors.field` 为 `evidenceFileIds`。四种都最多 3 张，用途 `EVIDENCE`。订单变为 `DISPUTED`，商品变为 `DISPUTED`。双方收到 `DISPUTE_OPENED`。

撤回后申诉状态 `WITHDRAWN`，订单回到 `PAID`，商品回到 `MEETUP`。

管理员 `POST /api/admin/disputes/{id}/resolve`：`{ "result", "remark" }`。`remark` 2–300 字，双方可见，并写入通知 `DISPUTE_RESOLVED`。

| result | 条件 | 订单 | 支付 | 商品 |
| --- | --- | --- | --- | --- |
| `REJECT` | 申诉为 `OPEN` | `PAID` | 保持 `PAID` | `MEETUP` |
| `COMPLETE` | 买家和卖家都已有确认时间 | `COMPLETED` | 保持 `PAID` | `SOLD` |
| `COMPLETE` | 缺任意一方确认 | 三个状态都不变，409 `ORDER_STATUS_INVALID` | 不变 | 不变 |
| `CANCEL` | 申诉原因不是 `SUSPECTED_FRAUD` | `CANCELLED` | `MOCK_REFUNDED`，原因用 `remark` | `ON_SALE` |
| `CANCEL` | 申诉原因是 `SUSPECTED_FRAUD` | `CANCELLED` | `MOCK_REFUNDED`，原因用 `remark` | `OFF_SHELF`，卖家 `publishBanned=true` |

继续完成不能因为单方撤回而发生。撤回是学生接口，结案结果里没有「因撤回而完成」。

## 9. 支付

课程不接真实资金。模拟网关和订单之间只通过签名通知通信。页面上的付款按钮标明「模拟」。

订单上的支付状态只有 `PENDING`、`PAID`、`CANCELLED`、`MOCK_REFUNDED`。学生接口不能写入这四个值。

创建支付单：`{ "channel": "MOCK" }`。调用者是买家，订单为 `PENDING_PAY`，支付为 `PENDING`，商品仍是 `RESERVED` 且占用者是这一单。返回 `paymentNo`、`amount`（取订单金额）、`expireAt`、`cashier.confirmPath`。其他渠道：`PAY_CHANNEL_UNSUPPORTED`。已有未过期的待通知支付单则直接返回，不新建。

`POST /api/pay/mock/{paymentNo}/confirm` 的请求体是 `{}`。沙箱按该支付单的金额记账，自己生成 `notifyNo`、签名，再调用下面的通知处理。请求里出现 `result`、`status`、`amount`、`paid`：400 `UNKNOWN_FIELD`，订单和支付状态不变。

通知字段：`paymentNo`、`orderNo`、`amount`、`notifyNo`、`status`（只有 `SUCCESS`）、`timestamp`（Unix 秒）、`nonce`（16–32 位）、`sign`。

签名：除 `sign` 外的非空字段按名字 ASCII 排序，拼成 `字段=值`，用 `&` 连接，值不做 URL 编码。`sign = HMAC-SHA256(pay.mock.secret, 拼接串)` 的小写十六进制。常量时间比较。

示例密钥 `example-secret-do-not-use-anywhere-0001` 只用来核对算法。拼接串把旧字段 `gatewayTradeNo` 换成 `notifyNo` 后，算法不变：

```text
amount=1500&nonce=9f2c7a1e4b6d8f03&notifyNo=N20260929101602483911&orderNo=O202609291015004821&paymentNo=P202609291015304107&status=SUCCESS&timestamp=1790648162
```

签名：`c6e32bbe3495553dfaaa74bab7c515e1fc013584335b957e65f44f5bfb623f83`。

采纳成功通知必须四项同时成立：订单仍是 `PENDING_PAY`；支付仍是 `PENDING`；通知金额与成交金额一致且验签通过；商品仍是 `RESERVED` 且未结束订单就是这一笔。采纳后支付改为 `PAID`，订单改为 `PAID`，商品改为 `MEETUP`。然后才允许发聊天。卖家收到 `ORDER_PAID`。

| 步 | 处理 |
| --- | --- |
| 字段不齐、`status` 不是 `SUCCESS` | 400 `BAD_REQUEST`。能识别 `notifyNo` 时记一行 `adopted=false` |
| 时间戳相差超过 300 秒 | `EXPIRED_TIMESTAMP`，审计 `PAY_NOTIFY_EXPIRED`。状态不变 |
| 签名不对 | `INVALID_SIGN`，审计 `PAY_NOTIFY_BAD_SIGN`。状态不变 |
| nonce 10 分钟内已用过 | `REPLAYED`。状态不变 |
| 支付单不存在或订单号不一致 | `PAYMENT_NOT_FOUND`。状态不变 |
| 金额不等于订单成交金额 | `AMOUNT_MISMATCH`，审计 `PAY_NOTIFY_AMOUNT_MISMATCH`。金额和状态都不变，记 `adopted=false`，原因「金额不一致」 |
| 同一 `notifyNo` 已经有记录 | 直接成功返回。不新增模拟退回，不改状态 |
| 四项都成立 | 进入待面交。记 `adopted=true` |
| 订单已 `CANCELLED`，或商品已不再由这一单占用 | 订单保持 `CANCELLED`，支付改为 `MOCK_REFUNDED`，原因「取消或释放后的迟到支付」。商品和另一笔新订单不变。记 `adopted=false`。双方收到 `PAYMENT_MOCK_REFUNDED` |

成功响应不走统一包：HTTP 200 `{ "code": "SUCCESS", "message": "OK" }`。失败 HTTP 400 `{ "code": "FAIL", "message": "<原因>" }`。

模拟退回只改支付状态和原因，不调用退款接口，不把钱退回任何钱包。

## 10. 订单聊天

聊天按订单，不按商品。下单前、商品详情、首页和搜索都没有聊天入口。下单成功时不建可发送的会话。

`GET /api/orders/{orderId}/messages` 只有买家和卖家能调。其他人 403，响应没有 `list`。

| 订单状态 | `canSend` | 记录 | 发送 |
| --- | --- | --- | --- |
| `PENDING_PAY` | false | `list` 为空 | 409 `CHAT_NOT_OPEN`。`message` 为「支付完成并进入待面交后才能聊天」 |
| `PAID`、`DISPUTED` | true | 已有文字 | 可以发 |
| `COMPLETED`、`CANCELLED` | false | 已有文字 | 409 `CHAT_READONLY` |

查询参数：`beforeId` 往上翻，`afterId` 断线补拉，只能带一个。`size` 默认 30、最大 50。`list` 按 id 升序。每条消息：`id`、`orderId`、`senderId`、`type=TEXT`、`content`、`createdAt`。

`POST /api/orders/{orderId}/messages`：`{ "content", "clientMsgId" }`。`content` 去掉空白后 1–500 字。空消息 400。含联系方式 400，不入库。`clientMsgId` 8–40 位；同一发送人重复则返回第一条。只接受文字，出现 `type=IMAGE` 或其他类型按未知字段或校验失败拒绝。

发送成功后给对方写通知 `CHAT_MESSAGE`，`ref` 为 `{ "type": "ORDER", "id": orderId }`，并推 WebSocket。

管理员 `GET /api/admin/disputes/{id}/messages`：该申诉状态为 `OPEN` 时返回同一份文字记录，没有发送接口。申诉已撤回、驳回或结案：403 `CHAT_NOT_OPEN`，没有消息正文。其他订单接口也不带聊天正文。

WebSocket：`/ws/im`，请求头带 token。无效则关闭码 4401。客户端每 30 秒发 `{"type":"PING"}`，服务端回 `PONG`。90 秒无帧则断开。服务端推送：`MESSAGE`（含 `orderId`）、`NOTIFICATION`、`ORDER_STATUS`（`orderId`、`status`）、`BADGE`。客户端只发 `PING`。关闭码 4401 重新登录后再连。重连间隔 1、2、4、8 秒，之后 15 秒。重连后拉角标，当前订单用 `afterId` 补拉。单用户最多 3 个连接。

## 11. 举报与管理端

举报 `POST /api/reports`，已认证：`{ "goodsId", "description", "evidenceFileIds" }`。说明 1–300 字。图片 0–3 张，用途 `EVIDENCE`，每张 ≤5 MB。商品须对调用者可见，不能举报自己的商品。

同一人对同一商品已有 `PENDING` 举报：409 `DUPLICATE_REPORT`，不新建。上一张已驳回或已下架后可以再报。

`GET /api/me/reports` 只返回本人的单：`id`、`goodsId`、`description`、`status`、`reason`（未处理为 null）、`createdAt`、`handledAt`。不返回别人的举报、举报人名单和公示。

管理员处理 `POST /api/admin/reports/{id}/handle`：`{ "action", "reason" }`。`reason` 2–100 字，两种动作都必填。

| action | 结果 |
| --- | --- |
| `DISMISS` | 举报变为 `DISMISSED`。商品状态不变 |
| `REMOVE_GOODS` | 举报变为 `REMOVED`，商品变为 `REMOVED` 并记下理由。该商品上的未结束订单保持原状态，不取消、不改支付 |

处理完给提交人写 `REPORT_HANDLED`，`content` 含理由，`ref` 指向这条举报。已处理再点：409 `REPORT_ALREADY_HANDLED`。

管理员直接 `POST /api/admin/goods/{goodsId}/remove` 同样只改商品为 `REMOVED`，不取消未结束订单。申诉中的商品也可以下架，订单继续按 §8 走。恢复只把 `REMOVED` 改回 `OFF_SHELF`。

管理员登录失败记 `LOGIN_FAIL`，不说明是账号错还是密码错。`mustChangePassword=true` 时，除改密码外都返回 403 `PASSWORD_CHANGE_REQUIRED`。改密后旧 token 失效。审核通过记 `ADMIN_VERIFY_APPROVE`，驳回记 `ADMIN_VERIFY_REJECT`。

审计 `detail` 只记字段名、资源 ID、金额、命中的联系方式类别，不记原文。

| 类别 | 事件 |
| --- | --- |
| SECURITY | `TAMPER_FIELD`、`ACCESS_DENIED`、`TOKEN_INVALID`（过期不记）、`LOGIN_FAIL`、`RATE_LIMITED`、`PAY_NOTIFY_BAD_SIGN`、`PAY_NOTIFY_REPLAY`、`PAY_NOTIFY_EXPIRED`、`PAY_NOTIFY_AMOUNT_MISMATCH`、`FILE_REJECTED`、`DEV_LOGIN_BLOCKED` |
| BUSINESS | `ORDER_STOCK_CONFLICT`、`ORDER_STATUS_CHANGED`、`PAYMENT_SUCCESS`、`PAYMENT_LATE_REFUND`、`GOODS_PRICE_CHANGED`、`AI_DRAFT_FAILED`、`AI_QUOTE_FAILED` |
| ADMIN | `ADMIN_VERIFY_APPROVE`、`ADMIN_VERIFY_REJECT`、`ADMIN_GOODS_REMOVE`、`ADMIN_GOODS_RESTORE`、`ADMIN_DISPUTE_RESOLVE`、`ADMIN_REPORT_HANDLE`、`ADMIN_PUBLISH_BAN` |

`ADMIN_PUBLISH_BAN` 在疑似欺诈结案并禁止再发布时写入，`detail` 含订单 ID 和卖家 ID。

Redis 键都加 `campus:`：`lock:goods:{id}` 5 秒；`idem:{userId}:{key}` 10 分钟；`pay:nonce:{nonce}` 10 分钟；`session:{jti}` 到 token 过期；`rl:{规则}:{用户或IP}:{时间窗}`；`ai:draft:count:{userId}:{yyyyMMdd}`、`ai:quote:count:{userId}:{yyyyMMdd}` 到北京时间当日结束；商品列表短缓存。下单和支付的状态只写 MySQL。释放锁用 Lua：值等于自己的 requestId 才删除。

模块对应包：`/api/auth` → `auth`；`/api/me` 与 `/api/users` → `user`；元数据、楼栋、分类、文件 → `common`；商品 → `goods`；订单与订单聊天 → `order`；支付 → `pay`；通知 → `im`；举报 → `report`；管理端账号和审计 → `admin`。`/api/ai/**` → `ai`。下单改商品状态只能调商品模块的方法。下单和创建支付单不读 AI 草稿和建议价。

## 12. AI

模型密钥只在服务端配置。地址和密钥写在 `application-local.yml` 的 `app.ai`，不进仓库。建议价只出现在本节的建议价接口里。商品详情和订单只有卖家标价。发布仍走 `POST /api/goods`，入库价格以用户提交的 `price` 为准。

从后端发出模型请求起计时，上限 10 秒。超过 10 秒、上游关闭或结果不可用，该次记失败并计入当日次数。

每个用户按北京时间每个自然日，草稿和建议各最多 20 次，成功和失败都计数。第 21 次返回 429 `AI_DAILY_QUOTA`，`Retry-After` 到当日结束的秒数，不再发出模型请求。未认证、图片不可用、联系方式、字段校验、看不到的商品，不计入次数。

**草稿** `POST /api/ai/goods-draft`。已认证。请求：`imageFileIds`（1–9 个，本人已上传且 `purpose=GOODS`）、`prompt`（可选，1–200 字；含联系方式则 `CONTACT_INFO_NOT_ALLOWED`）。

成功时 `filled` 为 true，并带 `draftId`、`title`、`description`、`categoryId`、`condition`、`suggestedPrice`、`aliases`、`tradeNote`、`expiresInSeconds`（600）。这些值只供卖家修改后，随 `POST /api/goods` 提交。发布接口不读 `draftId`。草稿不写进商品详情。

模型字段先按发布规则检查。不合规则的字段改为 null，其余保留。至少还有一个非空字段则 `filled` 为 true。全部不可用或上游失败：`filled` 为 false，字段为 null，HTTP 200。卖家已在页面上填的内容由页面保留，本接口不接收、也不清空那些内容。

**建议价** `POST /api/ai/quotes`。登录即可，含未认证。请求只收 `{ "goodsId" }`。商品须对调用者可见且仍可看详情；否则 404。

成功：`{ "goodsId", "listPrice", "available": true, "suggestedPrice", "disclaimer": "仅供参考" }`。`suggestedPrice` 是整数分。下单不读这个值。

失败或超过 10 秒：HTTP 200，`{ "goodsId", "listPrice", "available": false, "hint": "暂无建议价" }`。响应里没有 `suggestedPrice`，也不用成色区间中点顶上。记 `AI_QUOTE_FAILED`。

## 13. 错误码

| code | HTTP | 含义 |
| --- | --- | --- |
| `OK` | 200 | 成功 |
| `BAD_REQUEST` | 400 | JSON 或类型错误 |
| `VALIDATION_FAILED` | 400 | 字段不合法，看 `fieldErrors` |
| `UNKNOWN_FIELD` | 400 | 请求体有文档外字段 |
| `CONTACT_INFO_NOT_ALLOWED` | 400 | 文本含联系方式 |
| `INVALID_CODE` | 400 | 注册验证码错误 |
| `CODE_EXPIRED` | 400 | 注册验证码过期、作废或不存在 |
| `SETUP_TOKEN_INVALID` | 400 | 设置密码凭证无效或已过期 |
| `WX_LOGIN_FAILED` | 400 | 微信 code 无效或已用过 |
| `FILE_TYPE_NOT_ALLOWED` | 400 | 不是 JPEG、PNG、WebP |
| `FILE_NOT_USABLE` | 400 | fileId 不能用 |
| `PAY_CHANNEL_UNSUPPORTED` | 400 | 渠道不是 `MOCK` |
| `UNAUTHORIZED` | 401 | 未登录或 token 失效 |
| `BAD_CREDENTIALS` | 401 | 学号或密码错误；管理员登录时是账号或密码错。响应不说明具体是哪一项 |
| `FORBIDDEN` | 403 | 不是资源所属人，或没有管理权限 |
| `NOT_VERIFIED` | 403 | 需要校园认证 |
| `PUBLISH_BANNED` | 403 | 因疑似欺诈被禁止再发布，账号仍可登录 |
| `PASSWORD_CHANGE_REQUIRED` | 403 | 管理员须先改初始密码 |
| `CHAT_NOT_OPEN` | 409 | 未到待面交。管理员在申诉不是 `OPEN` 时读取该聊天仍用这个 code，但 HTTP 是 403，响应没有消息正文 |
| `CHAT_READONLY` | 409 | 订单已结束，只能看记录 |
| `NOT_FOUND` | 404 | 不存在或不可见 |
| `CONFLICT` | 409 | 请求处理中或并发冲突 |
| `ALREADY_VERIFIED` | 409 | 已经认证 |
| `VERIFICATION_PENDING` | 409 | 认证审核中 |
| `STUDENT_NO_TAKEN` | 409 | 学号已被绑定 |
| `USERNAME_TAKEN` | 409 | 用户名已被占用 |
| `EMAIL_TAKEN` | 409 | 邮箱已被注册 |
| `WX_ALREADY_BOUND` | 409 | 微信已属于别人，或当前账号已绑定另一个微信 |
| `GOODS_NOT_EDITABLE` | 409 | 当前商品状态不能改 |
| `GOODS_NOT_AVAILABLE` | 409 | 不能下单。可带 `myOrderId` |
| `CANNOT_BUY_OWN_GOODS` | 409 | 不能买自己的商品 |
| `ORDER_STATUS_INVALID` | 409 | 订单状态不允许该操作 |
| `ORDER_EXPIRED` | 409 | 订单已超时 |
| `PAYMENT_NOT_PAYABLE` | 409 | 支付单已结束或过期 |
| `DISPUTE_NOT_OPEN` | 409 | 没有进行中的申诉 |
| `DUPLICATE_REPORT` | 409 | 该商品已有自己的待处理举报 |
| `VERIFICATION_NOT_PENDING` | 409 | 认证申请已被处理 |
| `REPORT_ALREADY_HANDLED` | 409 | 举报已处理 |
| `FILE_TOO_LARGE` | 413 | 超过 5 MB |
| `RATE_LIMITED` | 429 | 看 `Retry-After` |
| `AI_DAILY_QUOTA` | 429 | 当日草稿或建议价已满 20 次 |
| `LOGIN_LOCKED` | 429 | 学号密码或管理员登录失败次数过多 |
| `INTERNAL_ERROR` | 500 | 服务器错误 |
| `UPSTREAM_UNAVAILABLE` | 503 | 微信或模型上游不可用 |

支付通知的 `EXPIRED_TIMESTAMP`、`INVALID_SIGN`、`REPLAYED`、`PAYMENT_NOT_FOUND`、`AMOUNT_MISMATCH` 只出现在给沙箱的响应里。

验收时至少要看到这些结果：下单带 `price` 被拒绝且无订单；并发抢一件只有一单成功；伪造或改金额的通知不改变订单；取消或超时后的成功通知保持已取消，支付为模拟退回，新订单不动；A 的 token 不能读 B 的订单和聊天；待支付、已完成、已取消时不能发消息；管理员在申诉结案后不能再读该聊天；`sort` 不能注入；标题和聊天里的 `<script>` 原样保存但不执行；服务器上 `dev-login` 为 404；登出后旧 token 失效。

## 14. 待确认

已定：模拟网关、域名 `simpleyi.site`、库名 `ershou`、小程序默认连服务器、学号注册登录与微信登录并存、订单聊天代替微信号。

未定：微信小程序的 AppID 何时写入服务器配置；管理端页面放在小程序里还是单独网页；学号是否收紧到本校号段；图片放磁盘还是对象存储。AppID 未就绪时，学号注册和学号密码登录可以先联调。`wx-login` 与 `wx-bind` 的换票要等 `wx.app-id`、`wx.app-secret` 写入 `application-local.yml`。本机仍可用 `dev-login`，它不进入验收。

## 变更记录

| 版本 | 日期 | 说明 |
| --- | --- | --- |
| v0.1 | 2026-09-29 | 初稿 |
| v0.2 | 2026-09-29 | 小程序默认连 `https://simpleyi.site`；库名 `ershou` |
| v0.3 | 2026-09-29 | 压缩篇幅。接口、字段、状态机、签名和错误码不变 |
| v0.4 | 2026-09-30 | 注册改为学号+用户名+邮箱验证码，登录改为学号+密码 |
| v0.5–v0.10 | 2026-10-05 至 2026-11-02 | 认证、商品、订单、模拟支付、聊天、举报、管理端和 AI 按当时约定实现并部署。细节见 git 历史 |
| v1.6 | 2026-11-04 | 按说明书 V1.6 改约定：微信登录与五项认证材料；商品补待面交/申诉中；支付改为待支付/已支付/已取消/模拟退回；聊天改到订单上并按阶段开关；申诉四种类型和三种结案；举报只处理商品且不取消订单；建议价失败返回「暂无建议价」，草稿和建议每日各 20 次。撤下收藏、浏览记录、评价、议价话术、按商品聊天、面交结构化字段、封号和模拟发票。线上进程尚未按本版修改 |
| v1.6.1 | 2026-11-04 | 保留学号邮箱注册和学号密码登录，并增加微信登录与登录后绑定。学号注册的账号在注册时占用学号；仅微信建立的账号在校园认证时写入学号。两种方式绑定后进入同一个用户 |
