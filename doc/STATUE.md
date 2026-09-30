# 服务器已部署接口

地址：`https://simpleyi.site`  
库名：`ershou`（表还没建；仓库里的表结构见 [../sql/README.md](../sql/README.md)）  
核对时间：2026-09-30

约定见 [base-API.md](./base-API.md)。下表只列服务器上现在能调用的接口。

| 方法 | 路径 | 状态 | 说明 |
| --- | --- | --- | --- |
| GET | `/api/health` | 已部署 | 返回 `{"service":"campus-ershou","status":"UP"}`，不走统一响应包 |

其余接口访问服务器会得到 404。新接口上线后改这一份。

## 本仓库已实现（尚未部署）

本机已跑通：`http://127.0.0.1:8080`。MySQL 用 `bash sql/start-local-mysql.sh`（`127.0.0.1:3307` / 库 `campus_ershou` / root / campus），另需 Redis。Flyway 已建表：`areas`、`buildings`、`categories`、`users`。楼栋种子为学上公寓 1～14。本机 `application-local.yml` 已开 QQ SMTP，注册发真实邮件，不返回 `debugCode`。

核对时间：2026-09-30（本机）。服务器仍只有 health。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/health` | 健康检查 |
| GET | `/api/meta` | 枚举与上限 |
| GET | `/api/categories` | 商品分类 |
| GET | `/api/campus/buildings` | 楼栋 |
| POST | `/api/auth/register` | 提交学号、用户名、邮箱并发送验证码 |
| POST | `/api/auth/register/verify-code` | 校验验证码，换取 setupToken |
| POST | `/api/auth/register/set-password` | 设置密码并登录 |
| POST | `/api/auth/login` | 学号 + 密码登录 |
| POST | `/api/auth/logout` | 登出 |
| GET | `/api/me` | 当前用户资料 |
