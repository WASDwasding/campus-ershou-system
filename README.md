# 校园二手交易系统 (campus-ershou-system)

面向校园场景的二手物品交易平台。后端 Spring Boot，前端 Vue。

## 技术栈

| 层 | 技术 |
| --- | --- |
| 后端 | Spring Boot |
| 前端 | Vue |
| 数据库 | 待定 |

## 本地启动

待补充。

## 协作流程

`main` 是受保护分支，不允许直接 push，所有改动都通过 Pull Request 合并。

### 1. 首次克隆

推荐用 SSH（原因见下方「国内网络注意事项」）：

```bash
git clone git@github.com:WASDwasding/campus-ershou-system.git
cd campus-ershou-system
```

### 国内网络注意事项

国内网络（尤其校园网）对 `github.com` 的 **HTTPS 常有干扰**，表现为 `git push` / `git clone` 卡住后报
`Error in the HTTP2 framing layer` 或 `Failed to connect to github.com port 443`。
解决办法是改用 SSH，并让 SSH 走 443 端口。

一次性配置（每位协作者在自己电脑上做一遍）：

```bash
# 1. 生成密钥（已有 ~/.ssh/id_ed25519 可跳过）
ssh-keygen -t ed25519 -C "你的邮箱"

# 2. 复制公钥，到 https://github.com/settings/ssh/new 添加
cat ~/.ssh/id_ed25519.pub

# 3. 让 SSH 走 443 端口
cat >> ~/.ssh/config <<'EOF'
Host github.com
    HostName ssh.github.com
    Port 443
    User git
    IdentityFile ~/.ssh/id_ed25519
    IdentitiesOnly yes
EOF
chmod 600 ~/.ssh/config

# 4. 验证，出现 "Hi <你的用户名>!" 即成功
ssh -T git@github.com
```

如果已经用 HTTPS 克隆过，切换远程地址即可：

```bash
git remote set-url origin git@github.com:WASDwasding/campus-ershou-system.git
```

### 2. 开发一个功能

从最新的 `main` 切出分支，分支名用 `类型/简短描述`：

```bash
git switch main
git pull
git switch -c feat/goods-publish
```

分支类型前缀约定：

- `feat/` 新功能
- `fix/` 修 bug
- `refactor/` 重构
- `docs/` 文档
- `chore/` 构建、依赖、配置等杂项

### 3. 提交

提交信息用 [Conventional Commits](https://www.conventionalcommits.org/zh-hans/) 风格，方便日后追溯和生成变更日志：

```bash
git add .
git commit -m "feat(goods): 支持发布二手商品并上传图片"
```

### 4. 推送并开 PR

```bash
git push -u origin feat/goods-publish
```

推送后在 GitHub 页面上开 Pull Request（或用 `gh pr create`），指派一位同学 review。至少 1 个 approve 后才能合并。

### 5. 合并后清理

```bash
git switch main
git pull
git branch -d feat/goods-publish
```

### 冲突处理

如果 `main` 有了新提交导致 PR 冲突，在自己分支上 rebase：

```bash
git switch main && git pull
git switch feat/goods-publish
git rebase main
# 解决冲突后
git add <冲突文件>
git rebase --continue
git push --force-with-lease
```

> 只在自己的功能分支上用 `--force-with-lease`，永远不要对 `main` 强推。

## 团队约定

- **不要提交敏感信息**：数据库密码、密钥、token 一律放本地 `.env` 或 `application-local.yml`，这些路径已在 `.gitignore` 中忽略。仓库里只放 `.env.example` 之类的模板。
- **不要提交依赖和构建产物**：`node_modules/`、`target/`、`dist/` 已忽略。
- **一个 PR 只做一件事**，方便 review。
- 改动数据库结构时，把 SQL 迁移脚本一并提交，并在 PR 描述里说明。

## 许可证

[MIT](./LICENSE)
