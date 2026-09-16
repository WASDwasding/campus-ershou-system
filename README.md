# 校园智能二手交易平台 (campus-ershou-system)

对抗赛题目：同题竞技 + 安全攻防。后端 Spring Boot，前端 uni-app（微信小程序）。

空壳阶段：后端能启动（健康检查），小程序能编译进微信开发者工具；数据库和 OSS 还没接。文档会跟着开发补。

## 启动

```bash
cd backend && mvn spring-boot:run

# 另开终端
cd frontend && npm install && npm run dev
```

环境说明见 [doc/SETUP.md](./doc/SETUP.md)。  
健康检查：<http://127.0.0.1:8080/api/health>  
小程序：用微信开发者工具打开 `frontend/dist/dev/mp-weixin`。

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
| [doc/COURSE_PLAN.md](./doc/COURSE_PLAN.md) | 课程大致进度 |
| [sql/](./sql/) | 数据库 |
| [doc/TONEW.md](./doc/TONEW.md) | Git 新手教程 |

## 协作

`main` 不要直接 push。开分支、开 PR，要合的时候跟管理员说一声。Git 不会用看 [TONEW](./doc/TONEW.md)。

密钥和微信 AppID 不要提交，只放本地 `application-local.yml` / `.env`，或只在本机改 `manifest.json`。

## 许可证

[MIT](./LICENSE)
