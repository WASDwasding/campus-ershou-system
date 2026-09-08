# 校园智能二手交易平台 (campus-ershou-system)

对抗赛题目：同题竞技 + 安全攻防。后端 Spring Boot，前端 Vue3，图片走 OSS。

## 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Java 17、Spring Boot 3.4（Security / MyBatis / Redis 实现功能时再加） |
| 前端 | Vue 3、Vite |
| 数据 | MySQL 8、Redis（写业务时再连） |
| 对象存储 | 阿里云 OSS |
| AI | 大模型 HTTP API |

更细的模块划分和安全约定见 [doc/ARCH.md](./doc/ARCH.md)。

## 仓库结构

```
backend/     Spring Boot
frontend/    Vue3
sql/         建表脚本
doc/         文档
```

## 本地启动

逐步说明（JDK、Docker / 云数据库、OSS、启动、排错、代码写在哪）见 **[doc/SETUP.md](./doc/SETUP.md)**。

最短路径（现在不需要数据库也能启动）：

```bash
cd backend && mvn spring-boot:run

# 另开终端
cd frontend && npm install && npm run dev
```

macOS 若 `java -version` 报找不到 Runtime：

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
```

健康检查：<http://127.0.0.1:8080/api/health>  
前端：<http://127.0.0.1:5173>

## 云服务器与 OSS

- MySQL、Redis 可以装在云服务器上，把 `application-local.yml` 的地址改过去。
- OSS 的 endpoint、Bucket、AccessKey 填进同一份 local 配置。**不要提交真实密钥**。
- 上线时 Nginx 反代 `/api`、`/ws`，前端 `npm run build` 后的 `dist` 作静态站点。

## 当前进度

目录骨架。后端仅 `GET /api/health`；`auth` / `goods` / `order` 等包是空的，登录、下单、支付尚未实现。

## 文档

| 文档 | 内容 |
| --- | --- |
| [doc/COURSE_PLAN.md](./doc/COURSE_PLAN.md) | **14 周课程开发计划**（对齐得分节点） |
| [doc/SETUP.md](./doc/SETUP.md) | 环境安装、启动、OSS/云库、排错、代码写在哪 |
| [doc/ARCH.md](./doc/ARCH.md) | 模块、下单支付约定、部署要点 |
| [doc/TONEW.md](./doc/TONEW.md) | Git 新手教程 |

---

# 协作规范

`main` 是受保护分支，不允许直接 push，所有改动都通过 Pull Request 合并，需 1 个 approve。

完整操作步骤见 [新手上手教程 · 第 4 步](./doc/TONEW.md#第-4-步日常开发循环)。

## 分支命名

格式 `类型/简短描述`，描述用英文小写加连字符：

| 前缀 | 用途 | 示例 |
| --- | --- | --- |
| `feat/` | 新功能 | `feat/goods-publish` |
| `fix/` | 修 bug | `fix/login-token-expire` |
| `refactor/` | 重构，不改变功能 | `refactor/order-service` |
| `docs/` | 只改文档 | `docs/api-readme` |
| `chore/` | 构建、依赖、配置等杂项 | `chore/upgrade-mybatis` |
| `test/` | 补测试 | `test/goods-controller` |

## 提交信息

用 [Conventional Commits](https://www.conventionalcommits.org/zh-hans/) 风格：

```
类型(范围): 简短描述
```

类型同上表（`feat`、`fix`、`refactor`、`docs`、`chore`、`test`），范围是模块名，描述用中文写清楚做了什么。

好的例子：

```
feat(goods): 支持发布二手商品并上传图片
fix(auth): 修复 token 过期后未跳转登录页的问题
refactor(order): 抽取订单状态机，消除重复的状态判断
```

不好的例子：

```
update            # 改了什么？
修改bug           # 哪个 bug？
1                 # ...
```

## PR 要求

- **一个 PR 只做一件事**。混着改三个功能的 PR 没人看得动，也没法单独回滚。
- 描述里写清**改动内容**和**怎么验证**。
- 改动数据库结构时，把 SQL 迁移脚本一并提交，并在 PR 描述里说明。
- 指派至少一位 reviewer。
- 所有 review 评论都要点 **Resolve conversation** 才能合并。

## 团队约定

- **绝对不要提交敏感信息**。本仓库是公开的，数据库密码、OSS/短信密钥、JWT secret、大模型 Key 一律放本地 `.env` 或 `application-local.yml`。仓库里只放 `.example`。
  > 一旦密钥被提交并推送，即使后续删掉，它仍然留在 git 历史里可被检索。立刻去对应服务里更换密钥。
- **不要提交依赖和构建产物**：`node_modules/`、`target/`、`dist/` 已忽略。
- **不要提交 IDE 个人配置**：`.idea/`、`.vscode/` 已忽略。
- 每天开工前先 `git switch main && git pull`。

## 许可证

[MIT](./LICENSE)
