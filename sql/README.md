# 数据库

当前进度：注册登录、资料、校园认证申请、上传、商品、收藏。表结构以本目录为准。服务器库 `ershou` 已由 Flyway 建到 v2。

不建 `user_balance`。以后订单表名用 `orders`。

## 库名与执行

| 环境 | 库名 | 怎么建表 |
| --- | --- | --- |
| 本机 | `campus_ershou` | 推荐 `bash sql/start-local-mysql.sh`（`127.0.0.1:3307`，root / campus）。启动时 Flyway 执行 `V1__init.sql`、`V2__catalog.sql`，与 `schema.sql` + `seed.sql` 一致 |
| 服务器 | `ershou` | 2026-10-05 部署后 Flyway 已执行到 v2。已上线的库不要再手动导入 `schema.sql` |

建库：

```sql
CREATE DATABASE campus_ershou CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
-- 服务器：
-- CREATE DATABASE ershou CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

手动导入（已用 Flyway 的库不要再跑一遍，会和已有表冲突）：

```bash
mysql -u root -p campus_ershou < sql/schema.sql
mysql -u root -p campus_ershou < sql/seed.sql
```

改表时：**先改本目录，再同步改 Flyway 迁移**（已上线后用新的 `V2__*.sql`，不要改已经执行过的 `V1`）。

## 文件

| 文件 | 内容 |
| --- | --- |
| [schema.sql](./schema.sql) | 建表 |
| [seed.sql](./seed.sql) | 区域、楼栋、分类初始数据 |
| [start-local-mysql.sh](./start-local-mysql.sh) | 启动本机 3307 实例 |
| [stop-local-mysql.sh](./stop-local-mysql.sh) | 停止该实例 |
| `users` 无种子数据，由注册接口写入 | |

## 表一览

```
areas 1──n buildings
              ▲
              └── users.dorm_building_id（可空）
categories     （独立）
users 1──n stored_files / campus_verifications / goods
goods 1──n goods_images
users + goods → favorites
```

| 表 | 用途 | 谁在用 |
| --- | --- | --- |
| `areas` | 校区区域（东区 / 西区） | 楼栋归属；`GET /api/campus/buildings` 带出 `areaId` / `areaName` |
| `buildings` | 楼栋 | 楼栋列表、资料里的宿舍、商品面交点 |
| `categories` | 商品一级分类 | `GET /api/categories`、发布商品 |
| `users` | 账号 | 注册、登录、资料、公开主页 |
| `stored_files` | 上传后的图片 | `POST /api/files` |
| `campus_verifications` | 校园认证申请 | `POST/GET /api/auth/campus-verification` |
| `goods` / `goods_images` | 商品和图片 | 商品列表、详情、发布 |
| `favorites` | 收藏 | 收藏接口 |
| `admins` | 管理员，与 `users` 分开 | `POST /api/admin/auth/login`。不使用学号 |

验证码、设置密码凭证、登录失败计数、JWT 黑名单、浏览记录、每日浏览去重在 **Redis**（前缀 `campus:`），不建表。

## `areas`

| 列 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT PK | 自增 |
| `name` | VARCHAR(32) | 区域名 |
| `sort` | INT | 越小越靠前 |

初始：`1 东区`，`2 西区`。

## `buildings`

| 列 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT PK | 自增 |
| `name` | VARCHAR(32) | 楼栋名 |
| `area_id` | BIGINT FK → areas | 所属区域 |
| `type` | VARCHAR(16) | `DORM` `TEACHING` `CANTEEN` `LIBRARY` `GATE` `OTHER` |
| `meetup_point` | TINYINT(1) | `1` 可作为面交点 |
| `enabled` | TINYINT(1) | `0` 停用，接口不返回 |
| `sort` | INT | 越小越靠前 |

接口：`GET /api/campus/buildings?type=&meetupOnly=`。停用的不返回。

初始楼栋：学上公寓 1～14，全部为宿舍（`DORM`）且是面交点。1～7 属东区，8～14 属西区。

## `categories`

| 列 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT PK | 自增 |
| `name` | VARCHAR(32) | 分类名 |
| `icon` | VARCHAR(64) | 小程序本地图片键 |
| `enabled` | TINYINT(1) | `0` 停用，接口不返回 |
| `sort` | INT | 越小越靠前 |

只有一级。初始 8 个：教材书籍、数码电子、宿舍电器、生活日用、运动户外、服饰鞋包、考研考证、其他。

## `users`

| 列 | 类型 | 说明 |
| --- | --- | --- |
| `id` | BIGINT PK | 自增，JWT `sub` |
| `student_no_hash` | CHAR(64) UNIQUE | `HMAC-SHA256(app.student-no.hmac-key, 学号)` 的十六进制。登录用学号现算再查 |
| `student_no_masked` | VARCHAR(16) | 前 4 后 2，如 `2023****56` |
| `username` | VARCHAR(16) UNIQUE | 注册用户名 |
| `nickname` | VARCHAR(16) | 展示名，注册时复制用户名 |
| `email` | VARCHAR(128) UNIQUE | 小写 |
| `password_hash` | VARCHAR(72) | BCrypt |
| `avatar_file_id` | VARCHAR(64) NULL | 头像 fileId；`GET /api/me` 的 `avatarUrl` 指向公开图 |
| `bio` | VARCHAR(60) NULL | 简介 |
| `dorm_building_id` | BIGINT NULL FK → buildings | 宿舍楼 |
| `status` | VARCHAR(16) | `ACTIVE` / `BANNED` |
| `verify_status` | VARCHAR(16) | 注册完成后为 `UNVERIFIED`（邮箱≠校园认证） |
| `ban_reason` | VARCHAR(200) NULL | 封禁原因 |
| `ban_until` | DATETIME(3) NULL | 截止时间；永久封禁则为 null |
| `token_version` | INT | 与 JWT `ver` 对应；登出加一 |
| `created_at` / `updated_at` | DATETIME(3) | 时间 |

不存明文学号、明文密码。日志也不要打完整学号。
