# 现状（本机 vs 服务器）

核对时间：2026-11-04。

前后端按 [base-API.md](./base-API.md) 的 **v1.6** 对接。字段、状态机和错误码以那份约定为准，不要再按下面这份旧进程开发。

**线上和本机代码都还是 2026-11-02 的旧包。** `https://simpleyi.site` 仍提供学号邮箱注册、学号密码登录、收藏、浏览记录、评价、按商品聊天、议价话术，以及支付状态 `CREATED` / `SUCCESS` / `FAILED` / `CLOSED` / `REFUNDED`。这些和说明书 V1.6 不一致。后端按 v1.6 改完并重新部署之前，抓线上响应不能当作新接口。

`POST /api/auth/dev-login` 只在本机打开，服务器返回 404。

统一响应仍是 `{ "code", "message", "data", "traceId" }`。成功时 `code` 为 `OK`。支付回调 `POST /api/pay/notify/mock` 成功时是 `{ "code": "SUCCESS", "message": "OK" }`。  
登录后请求头：`Authorization: Bearer <token>`。  
金额单位是**分**。图片地址拼在 `public-base-url` 上（本机 `http://127.0.0.1:8080`，线上是 `https://simpleyi.site`）。

---

## 两边对照

| | 本机 | 服务器 |
| --- | --- | --- |
| 入口 | `http://127.0.0.1:8080` | `https://simpleyi.site` |
| 代码 | 当前仓库，业务仍是旧实现 | `/opt/campus-ershou/campus-ershou.jar`（2026-11-02 旧包，上一版留在 bak-20261102） |
| 接口约定 | [base-API.md](./base-API.md) v1.6，代码尚未跟上 | 同左，尚未按 v1.6 部署 |
| MySQL | `127.0.0.1:3307` / `campus_ershou` | `127.0.0.1:3306` / `ershou`，Flyway 已建到 v5 |
| 图片 | `.data/files` | `/opt/campus-ershou/files`，公网 `https://simpleyi.site` |

WebSocket 地址仍是 `/ws/im`。v1.6 里客户端只发 `{"type":"PING"}`，服务端回 `{"type":"PONG"}`，token 放在请求头，不放 URL。

---

## 旧包还在、v1.6 不再对接的能力

| 旧路径或行为 | v1.6 改成 |
| --- | --- |
| `POST /api/auth/register`、验证码、设密码、`POST /api/auth/login` | `POST /api/auth/wx-login`。学号在校园认证里提交 |
| 认证只交宿舍楼和校园卡 | 再加姓名、学号、学院 |
| 收藏、浏览记录、评价、`/api/ai/bargain` | 不做 |
| `POST /api/im/conversations`，按商品聊天，详情 `canChat` | `GET/POST /api/orders/{orderId}/messages`，待面交和申诉中才能发文字 |
| `PUT /api/orders/{orderId}/meetup` | 时间和地点写在订单聊天里 |
| 待面交卖家可以取消 | 已支付后买卖双方都不能取消 |
| 支付 `REFUNDED` / 「已退款」 | `MOCK_REFUNDED` / 模拟退回 |
| 举报用户、消息、警告、封号 | 只举报商品；下架或驳回，且不取消未结束订单 |

管理员登录仍是用户名 + 密码，令牌 12 小时。审核详情在 v1.6 里要能看到姓名、完整学号、学院、楼栋和校园卡；旧包只有用户名、昵称、脱敏学号和宿舍楼。

---

## 部署记录

2026-11-02 换上当时的分支 A。库 `ershou` 没有新的 Flyway 版本，AI 草稿和报价令牌只放 Redis。服务器配置只加了 `app.ai`。旧 jar 留在 `/opt/campus-ershou/campus-ershou.jar.bak-20261102`，旧配置留在 `application-local.yml.bak-20261102`。

2026-10-10 换上带审核和通知的包。Flyway 从 v3 迁到 v4。旧 jar 留在 `bak-20261010`。

2026-10-05：服务器库名和数据库账号没改。邮件 `expose-code` 为 false。图片目录是 `/opt/campus-ershou/files`。JWT、学号 HMAC、文件签名用的是服务器上的密钥。Nginx 转发 `/files/public/`。更早的包留了 `bak-20261005`。
