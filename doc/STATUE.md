# 现状（本机 vs 服务器）

核对时间：2026-10-05。  
约定全文见 [base-API.md](./base-API.md)。下面的参数是给前端对接用的。

**2026-10-05 已部署到 `https://simpleyi.site`。** 健康检查、分类、楼栋、注册登录、资料、上传、商品接口已在线上。订单、支付、聊天、管理端审核还没有。

统一响应：`{ "code", "message", "data", "traceId" }`。成功时 `code` 为 `OK`。下面只写 `data`。  
登录后请求头：`Authorization: Bearer <token>`。  
金额单位是**分**。图片地址拼在 `public-base-url` 上（本机 `http://127.0.0.1:8080`，上线后是 `https://simpleyi.site`）。

---

## 两边对照

| | 本机 | 服务器 |
| --- | --- | --- |
| 入口 | `http://127.0.0.1:8080` | `https://simpleyi.site` |
| 代码 | 当前仓库 | `/opt/campus-ershou/campus-ershou.jar`（2026-10-05 已换新包，旧包留了 bak） |
| MySQL | `127.0.0.1:3307` / `campus_ershou` | `127.0.0.1:3306` / `ershou`，Flyway 已建到 v2 |
| 图片 | `.data/files` | `/opt/campus-ershou/files`，公网 `https://simpleyi.site` |

---

## 给前端的接口

还没做、先不要调：订单、支付、聊天、WebSocket、通知、举报、管理端审核。  
`GET /api/me/badges` 三个数字目前固定是 `0`。`stats` 里的订单数也是 `0`。  
校园认证提交后停在 `PENDING`。管理员账号已经可以登录，但通过/驳回接口还没做，所以普通注册用户发不了商品（`403 NOT_VERIFIED`）。

### 管理员

和管理员登录用的不是学号，而是用户名 + 密码。令牌 12 小时，不能调用普通用户接口。

| 方法 | 路径 | 登录 | 请求 | 成功时 data |
| --- | --- | --- | --- | --- |
| POST | `/api/admin/auth/login` | 否 | `{ username, password }` | `{ token, expiresAt, mustChangePassword, admin: { id, username, emailMasked } }` |
| POST | `/api/admin/auth/logout` | 管理员 | 无 body | `{}` |
| GET | `/api/admin/me` | 管理员 | 无 | `{ id, username, emailMasked, mustChangePassword }` |
| POST | `/api/admin/auth/password` | 管理员 | `{ oldPassword, newPassword }`，新密码 12–64 位 | `{}`。旧 token 失效 |

账号或密码错都是 `401 BAD_CREDENTIALS`。普通用户 token 调管理接口是 `403`。

### 注册登录

| 方法 | 路径 | 登录 | 请求 | 成功时 data |
| --- | --- | --- | --- | --- |
| POST | `/api/auth/register` | 否 | `{ studentNo, username, email }` | `{ emailMasked, expiresInSeconds }`。本机开了真实邮件，**没有** `debugCode` |
| POST | `/api/auth/register/verify-code` | 否 | `{ email, code }` 验证码 6 位 | `{ setupToken, expiresInSeconds }` |
| POST | `/api/auth/register/set-password` | 否 | `{ setupToken, password }` 密码 8–64 位 | `{ token, expiresAt, isNewUser: true, user }` |
| POST | `/api/auth/login` | 否 | `{ studentNo, password }` | `{ token, expiresAt, isNewUser: false, user }` |
| POST | `/api/auth/logout` | 是 | 无 body | `{}`。旧 token 失效 |

学号 8–12 位数字。用户名 1–16，中文/字母/数字/下划线。邮箱存小写。

`user` 与 `GET /api/me` 相同：`id`、`username`、`nickname`、`emailMasked`、`studentNoMasked`、`avatarUrl`、`bio`、`status`、`verifyStatus`、`dormBuilding`（`{id,name}` 或 null）、`createdAt`。

常见错误：`STUDENT_NO_TAKEN` / `USERNAME_TAKEN` / `EMAIL_TAKEN`（409），`INVALID_CODE` / `CODE_EXPIRED` / `SETUP_TOKEN_INVALID`（400），`BAD_CREDENTIALS`（401），`LOGIN_LOCKED`（429，看 `Retry-After`），`USER_BANNED`（403，`data.reason`、`data.until`）。

### 资料、主页、角标

| 方法 | 路径 | 请求 | data |
| --- | --- | --- | --- |
| GET | `/api/me` | 无 | 见上 |
| PUT | `/api/me/profile` | 只传要改的字段：`nickname`（1–16）、`avatarFileId`、`bio`（≤60，`null` 或 `""` 清空）、`dormBuildingId`（须是宿舍，`null` 清空） | 同 `GET /api/me` |
| GET | `/api/me/stats` | 无 | `onSaleCount`、`offShelfCount`、`soldCount`、`favoriteCount`、`pendingOrderCount`、`sellingOrderCount` |
| GET | `/api/me/badges` | 无 | `unreadMessages`、`unreadNotifications`、`buyerTodos`。现在都是 0 |
| GET | `/api/users/{userId}` | 无 | `id`、`nickname`、`avatarUrl`、`bio`、`verified`、`relation: {sameBuilding, sameArea}`、`rating`（现在 null）、`soldCount`、`onSaleCount`。不返回学号和楼名 |

### 校园认证

| 方法 | 路径 | 请求 | data |
| --- | --- | --- | --- |
| GET | `/api/auth/campus-verification` | 无 | `{ status, dormBuilding: {id,name} 或 null, rejectReason, submittedAt }`。没提交过则 `status=UNVERIFIED` |
| POST | `/api/auth/campus-verification` | `{ dormBuildingId, cardImageFileId }` | 同上，`status=PENDING` |

`cardImageFileId` 必须先用 `purpose=VERIFICATION` 上传。已认证 `ALREADY_VERIFIED`，审核中 `VERIFICATION_PENDING`。

### 上传

`POST /api/files`，`multipart/form-data`：字段 `file`、`purpose`。

`purpose`：`AVATAR`（登录即可）、`VERIFICATION`（未认证或已驳回）、`GOODS` / `IM` / `EVIDENCE`（必须已认证）。

只收 JPEG、PNG；WebP 仅在服务器能解码时接受。最大 5MB。会转成 JPEG 并去掉位置信息。

data：`{ fileId, purpose, url, thumbUrl }`。私有图的 `url` 约 10 分钟有效。  
再取地址：`GET /api/files/{fileId}/url`（要登录，且是本人或公开图）。

公开图可直接显示：`url` 形如 `http://127.0.0.1:8080/files/public/{fileId}`，缩略图加 `?size=thumb`。

错误：`FILE_TYPE_NOT_ALLOWED`、`FILE_TOO_LARGE`、`FILE_NOT_USABLE`、`NOT_VERIFIED`。

### 基础数据（不用登录）

| 方法 | 路径 | 参数 | data |
| --- | --- | --- | --- |
| GET | `/api/health` | 无 | 不走统一包：`{"service":"campus-ershou","status":"UP"}` |
| GET | `/api/meta` | 无 | `enums`（成色、商品状态等 `value/label`）、`limits`（价格、标题长度等） |
| GET | `/api/categories` | 无 | `{ list: [{ id, name, icon, sort }] }` |
| GET | `/api/campus/buildings` | `type` 可选；`meetupOnly=true` 只返回面交点 | `{ list: [{ id, name, areaId, areaName, type, meetupPoint, sort }] }` |

`type`：`DORM` `TEACHING` `CANTEEN` `LIBRARY` `GATE` `OTHER`。

### 商品（都要登录）

列表统一：`{ list, page, size, total, hasMore }`。`page` 从 1，`size` 默认 20、最大 50。

列表项：`id`、`title`、`coverUrl`、`price`、`originalPrice`、`condition`、`status`、`category: {id,name,icon}`、`meetupBuilding: {id,name,areaId,areaName}`、`seller: {id,nickname,avatarUrl,verified,relation}`、`favoriteCount`、`createdAt`。

| 方法 | 路径 | 参数 |
| --- | --- | --- |
| GET | `/api/goods` | 只返回在售。查询：`keyword`（1–30）、`categoryId`、`condition`、`minPrice`、`maxPrice`、`meetupBuildingId`、`areaId`、`sort`、`page`、`size`。`sort`：`latest`（默认）、`price_asc`、`price_desc`、`nearby`（没填宿舍则按最新） |
| GET | `/api/goods/{goodsId}` | 详情多：`description`、`tradeNote`、`images: [{fileId,url,thumbUrl}]`、`viewCount`、`seller.rating`、`viewer: {isMine, favorited, canBuy, canChat, activeOrderId}`、`updatedAt`。卖家看下架商品；别人看不到下架和已删除 |
| POST | `/api/goods` | 已认证。body 见下表。成功返回详情 |
| PUT | `/api/goods/{goodsId}` | 已认证且是卖家，仅在售或已下架。body 同发布，整体替换。`description` / `originalPrice` / `tradeNote` 传 null 即清空 |
| POST | `/api/goods/{goodsId}/off-shelf` | 卖家，在售 → 下架。data：`{ id, status }` |
| POST | `/api/goods/{goodsId}/on-shelf` | 已认证且是卖家，下架 → 在售 |
| DELETE | `/api/goods/{goodsId}` | 卖家软删除。data：`{ id, status: "DELETED" }` |
| GET | `/api/me/goods` | `status` 可选：`ON_SALE` `RESERVED` `SOLD` `OFF_SHELF` `REMOVED`。不传则除已删除外都返回 |
| GET | `/api/users/{userId}/goods` | `status` 只能 `ON_SALE`（默认）或 `SOLD` |
| PUT | `/api/goods/{goodsId}/favorite` | 无 body。重复调用也成功。data 是该商品列表项 |
| DELETE | `/api/goods/{goodsId}/favorite` | 取消收藏 |
| GET | `/api/me/favorites` | 分页 |
| GET | `/api/me/history` | 分页。最近 100 条，30 天 |
| DELETE | `/api/me/history` | 清空。data `{}` |

发布 / 编辑 body：

| 字段 | 规则 |
| --- | --- |
| `title` | 必填，2–30，不能含手机号、微信、QQ、邮箱 |
| `description` | 0–500，同样不能含联系方式 |
| `categoryId` | 必填，启用中的分类 |
| `condition` | `NEW` `LIKE_NEW` `GOOD` `FAIR` `POOR` |
| `price` | 必填，1–9999900 分 |
| `originalPrice` | 可选，1–99999900 |
| `imageFileIds` | 1–9 个，不重复，先用 `purpose=GOODS` 上传。第一张是封面 |
| `meetupBuildingId` | 必填，且是面交点 |
| `tradeNote` | 0–100 |

不要传 `sellerId`、`status`、`stock`，多出来的字段会 400。  
未认证发布：`403 NOT_VERIFIED`。状态不允许改：`409 GOODS_NOT_EDITABLE`。

`condition` 中文名从 `GET /api/meta` 的 `enums.goodsCondition` 取。

---

## 这次部署做了什么

服务器库名和数据库账号没改。邮件已合并进服务器配置，`expose-code` 为 false。图片目录是 `/opt/campus-ershou/files`，公网地址是 `https://simpleyi.site`。JWT、学号 HMAC、文件签名用的是服务器上新生成的密钥，和本机不是同一套。Nginx 的 `/files/public/` 改为转发到后端。旧 jar 和旧配置都留了 `bak-20261005`。
