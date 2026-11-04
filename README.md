# 校园智能二手交易平台 (campus-ershou-system)

对抗赛题目：同题竞技 + 安全攻防。后端 Spring Boot，前端是 `campus-trade/miniprogram` 里的原生微信小程序。

当前进度：接口约定已按说明书 V1.6 写成 [doc/base-API.md](./doc/base-API.md)。前后端按这份约定改。仓库里的代码和 `https://simpleyi.site` 仍是旧实现，线上实际进程见 [doc/STATUE.md](./doc/STATUE.md)。表结构在 [sql/](./sql/)，现有库还没迁到 V1.6。

本期范围以说明书为准：微信登录、校园认证、发布与浏览、建议价、下单、沙箱支付、订单内聊天、面交、申诉、通知、商品举报。AI 只做商品草稿和建议价，建议价不进订单。书籍视觉分档不在本期。

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
| [doc/base-API.md](./doc/base-API.md) | 前后端按此对接的 v1.6 接口约定 |
| [doc/STATUE.md](./doc/STATUE.md) | 本机和线上旧进程的现状，不是新接口说明 |
| [doc/D3-第一组-软件需求规格说明书-V1.6.docx](./doc/D3-第一组-软件需求规格说明书-V1.6.docx) | 最终需求说明书 |
| [doc/第一组项目计划书.docx](./doc/第一组项目计划书.docx) | 项目计划书 |
| [sql/](./sql/) | 当前库的表结构、初始数据和说明 |
| [doc/TONEW.md](./doc/TONEW.md) | Git 新手教程 |

## 协作

仓库是公开的。`main` 已开启分支保护：必须开拉取请求才能合并，不能直接 push，也不能强推或删除。这是个人仓库，GitHub 不能把合并权限只留给管理员；有写入权限的成员仍可以合并拉取请求。按约定，合并前跟管理员说一声。Git 不会用看 [TONEW](./doc/TONEW.md)。

密钥和微信 AppID 不要提交，只放本地 `application-local.yml` / `.env`。小程序地址在 `campus-trade/miniprogram/utils/config.js`，默认是 `https://simpleyi.site`。临时改成 `127.0.0.1` 时不要提交。`project.config.json` 里的 `appid` 在没有正式 AppID 时保持 `touristappid`。

## 许可证

[MIT](./LICENSE)
