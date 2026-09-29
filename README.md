# 校园智能二手交易平台 (campus-ershou-system)

对抗赛题目：同题竞技 + 安全攻防。后端 Spring Boot，前端 uni-app（微信小程序）。

当前进度：代码仍是空壳，后端只有健康检查，小程序能编译进微信开发者工具。底座接口、项目计划和技术过程已经写在 `doc/`，业务按 [doc/base-API.md](./doc/base-API.md) 实现。数据库和对象存储还没接。

项目 A（AI 议价）和项目 B（成色与仲裁）共用这套底座，分叉关系见 [doc/tree.html](./doc/tree.html)。

## 启动

```bash
cd backend && mvn spring-boot:run

# 另开终端
cd frontend && npm install && npm run dev
```

环境说明见 [doc/SETUP.md](./doc/SETUP.md)。  
小程序默认请求 `https://simpleyi.site`。服务器健康检查：<https://simpleyi.site/api/health>  
小程序：用微信开发者工具打开 `frontend/dist/dev/mp-weixin`。

前端只跑小程序，连服务器上的后端。后端在本机把接口测通后，由一个人把 jar 部署到服务器并重启，不要把没测完的包传上去。

## 目录

```
backend/     Spring Boot
frontend/    uni-app（微信小程序）
sql/         数据库
doc/         文档
```

| 文档 | 内容 |
| --- | --- |
| [doc/SETUP.md](./doc/SETUP.md) | 怎么跑起来 |
| [doc/ARCH.md](./doc/ARCH.md) | 模块和约定 |
| [doc/base-API.md](./doc/base-API.md) | 底座接口（设计稿，除健康检查外尚未实现） |
| [doc/校园智能二手交易平台软件项目计划.docx](./doc/校园智能二手交易平台软件项目计划.docx) | 项目计划书 |
| [doc/技术过程.txt](./doc/技术过程.txt) | 计划书第 4 部分 |
| [doc/tree.html](./doc/tree.html) | 底座与项目 A、B 的分叉 |
| [sql/](./sql/) | 数据库 |
| [doc/TONEW.md](./doc/TONEW.md) | Git 新手教程 |

## 协作

`main` 不要直接 push。开分支、开 PR，要合的时候跟管理员说一声。Git 不会用看 [TONEW](./doc/TONEW.md)。

密钥和微信 AppID 不要提交，只放本地 `application-local.yml` / `.env`，或只在本机改 `manifest.json`。后端如果把 `config.js` 临时改成 `127.0.0.1`，不要提交。

## 许可证

[MIT](./LICENSE)
