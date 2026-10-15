# 校园智能二手交易平台 (campus-ershou-system)

对抗赛题目：同题竞技 + 安全攻防。后端 Spring Boot，前端是 `campus-trade/miniprogram` 里的原生微信小程序。

当前进度：仓库处在底座（base）。后端已有注册登录、资料、上传、校园认证审核、通知、商品、订单、模拟支付、聊天、举报和管理端。线上范围以 [doc/STATUE.md](./doc/STATUE.md) 为准。表结构在 [sql/](./sql/)。微信登录还要等 AppID。

底座之后分两条线，只加各自的接口，不改底座已有字段、状态机和错误码。分叉关系见 [doc/tree.html](./doc/tree.html)。

1. **AI 模块**：大模型填表、建议价、议价。范围见 [doc/base-API.md](./doc/base-API.md) 第 1 节和 [doc/tree.html](./doc/tree.html)。
2. **书籍图像质量评估**：在底座上单独加。针对校园里的实体书籍，用图像评估质量。接口和规则还没写，文档待整理。

环境说明见 [doc/SETUP.md](./doc/SETUP.md)。  
小程序默认请求 `https://simpleyi.site`。服务器健康检查：<https://simpleyi.site/api/health>  
小程序：用微信开发者工具打开 `campus-trade/miniprogram`。

前端只跑小程序，连服务器上的后端。后端在本机把接口测通后，由一个人把 jar 部署到服务器并重启，不要把没测完的包传上去。

## 目录

```
backend/        Spring Boot
campus-trade/   前端。小程序在 miniprogram/
sql/            表结构、种子数据、本机 3307 MySQL 脚本
doc/            文档
```

| 文档 | 内容 |
| --- | --- |
| [doc/SETUP.md](./doc/SETUP.md) | 怎么跑起来 |
| [doc/ARCH.md](./doc/ARCH.md) | 模块和约定 |
| [doc/base-API.md](./doc/base-API.md) | 底座接口约定。已实现的范围和参数见 [doc/STATUE.md](./doc/STATUE.md) |
| [doc/STATUE.md](./doc/STATUE.md) | 线上现状和给前端的接口参数 |
| [doc/校园智能二手交易平台软件项目计划.docx](./doc/校园智能二手交易平台软件项目计划.docx) | 项目计划书 |
| [doc/技术过程.txt](./doc/技术过程.txt) | 计划书第 4 部分 |
| [sql/](./sql/) | 表结构、初始数据和说明 |
| [doc/TONEW.md](./doc/TONEW.md) | Git 新手教程 |

## 协作

仓库是公开的。`main` 已开启分支保护：必须开拉取请求才能合并，不能直接 push，也不能强推或删除。这是个人仓库，GitHub 不能把合并权限只留给管理员；有写入权限的成员仍可以合并拉取请求。按约定，合并前跟管理员说一声。Git 不会用看 [TONEW](./doc/TONEW.md)。

密钥和微信 AppID 不要提交，只放本地 `application-local.yml` / `.env`。小程序地址在 `campus-trade/miniprogram/utils/config.js`，默认是 `https://simpleyi.site`。临时改成 `127.0.0.1` 时不要提交。`project.config.json` 里的 `appid` 在没有正式 AppID 时保持 `touristappid`。

## 许可证

[MIT](./LICENSE)
