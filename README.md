# 校园二手交易系统 (campus-ershou-system)

面向校园场景的二手物品交易平台。

## 技术栈
待定

## 本地启动
待定

## 快速开始

```bash
git clone git@github.com:WASDwasding/campus-ershou-system.git
cd campus-ershou-system
```

> 国内网络对 `github.com` 的 HTTPS 有干扰，请用上面的 SSH 地址。
> 没配过 SSH 的同学看 [新手上手教程 · 第 2 步](./doc/TONEW.md#第-2-步配置-ssh国内网络几乎必须)。

## 文档

| 文档 | 内容 |
| --- | --- |
| [doc/TONEW.md](./doc/TONEW.md) | **新手上手教程**。第一次用 git 的同学从这里开始：概念、环境配置、SSH、克隆、日常开发循环、命令速查、常见问题急救 |

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
- 改了数据库结构，把 SQL 迁移脚本一并提交，并在描述里明确说明，方便队友同步本地数据库。
- 指派至少一位 reviewer。
- 所有 review 评论都要点 **Resolve conversation** 才能合并。

## 团队约定

- **绝对不要提交敏感信息**。本仓库是公开的，数据库密码、OSS/短信密钥、JWT secret 一律放本地 `.env` 或 `application-local.yml`，这些路径已在 `.gitignore` 里忽略。需要共享配置结构时，提交一份去掉了真实值的 `.env.example`。
  > 一旦密钥被提交并推送，即使后续删掉，它仍然留在 git 历史里可被检索。真的推上去了，立刻在对应服务里吊销/更换该密钥，并告知全组。
- **不要提交依赖和构建产物**：`node_modules/`、`target/`、`dist/` 已忽略。
- **不要提交 IDE 个人配置**：`.idea/`、`.vscode/` 已忽略。
- 每天开工前先 `git switch main && git pull`，让自己的代码基于最新的主线。

## 许可证

[MIT](./LICENSE)
