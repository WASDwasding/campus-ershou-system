# 新手上手教程

> 第一次用 git 或 GitHub 的同学，从这里开始，按顺序做完即可。
> 已经熟悉 git 的同学直接看 [README 的协作规范](../README.md#协作规范) 就行。
> clone 之后怎么启动，看 [SETUP.md](./SETUP.md)。
>
> 实在看不明白，也可以把这份文档丢给 AI agent 让它看着办。

## 目录

- [第 0 步：先搞懂 5 个概念](#第-0-步先搞懂-5-个概念)
- [第 1 步：安装 git 并做一次性配置](#第-1-步安装-git-并做一次性配置)
- [第 2 步：配置 SSH（国内网络几乎必须）](#第-2-步配置-ssh国内网络几乎必须)
- [第 3 步：克隆仓库](#第-3-步克隆仓库)
- [第 4 步：日常开发循环](#第-4-步日常开发循环)
- [常用命令速查](#常用命令速查)
- [出问题了怎么办](#出问题了怎么办)

## 第 0 步：先搞懂 5 个概念

不理解这几个词，后面的命令就只是在瞎背，出问题也不知道怎么救。

**仓库（repository）**：就是我们这个项目文件夹，但它额外记录了每一次改动的历史。分两种：**远程仓库**在 GitHub 服务器上，是大家共享的那一份；**本地仓库**在你自己电脑上，是你的副本。两者靠 `push`（上传）和 `pull`（下载）同步。

**提交（commit）**：一次改动的存档点，像游戏存档。每个 commit 有一句说明，告诉别人你这次改了什么。git 的历史就是一串 commit。

**暂存区（staging area）**：commit 之前的一个中间篮子。你用 `git add` 把想提交的文件放进篮子，再用 `git commit` 把整篮子打包成一个存档点。这样设计是为了让你能只提交部分文件，而不是每次都全交。

**分支（branch）**：一条独立的开发线。你在自己的分支上改代码，不会影响别人，也不会影响 `main`。写完再合并回去。这是多人协作不打架的关键。

**`main` 分支**：项目的主线，永远保持可运行状态。仓库是公开的，`main` 已开启分支保护：直接 push 会被 GitHub 拒绝，也不能强推或删除。改完开 Pull Request。这是个人仓库，有写入权限的成员在平台上仍可以自己点合并；按约定，合并前跟仓库管理员说一声。

## 第 1 步：安装 git 并做一次性配置

### 安装

- **Windows**：装 [Git for Windows](https://git-scm.com/download/win)。装完在开始菜单里会多一个 **Git Bash**，后面本教程所有命令都在 Git Bash 里执行（不要用 CMD 或 PowerShell，很多命令语法不一样）。
- **macOS**：终端里执行 `git --version`，如果没装系统会弹窗提示安装。
- **Linux**：`sudo apt install git` 或对应包管理器命令。

验证安装成功：

```bash
git --version
```

### 告诉 git 你是谁

每个 commit 都会记录作者。这一步不做的话没法提交。

```bash
git config --global user.name "你的名字"
git config --global user.email "你的GitHub注册邮箱"
```

> 用注册 GitHub 的那个邮箱，这样提交才会关联到你的账号，网页上才会显示你的头像。
> 不想公开真实邮箱的话，去 [GitHub 邮箱设置](https://github.com/settings/emails) 勾选 "Keep my email addresses private"，然后用页面上显示的 `xxx@users.noreply.github.com` 作为邮箱填进去。

### 一个强烈建议的配置

```bash
git config --global pull.rebase true
```

这让 `git pull` 用 rebase 而不是 merge 的方式同步，能避免历史里出现大量无意义的 "Merge branch 'main'..." 提交。多人协作时历史会干净很多。

## 第 2 步：配置 SSH（国内网络几乎必须）

国内网络（尤其校园网）对 `github.com` 的 **HTTPS 有干扰**，症状是 `git clone` / `git push` 卡住十几秒后报错：

```
fatal: unable to access 'https://github.com/...': Error in the HTTP2 framing layer
fatal: unable to access 'https://github.com/...': Failed to connect to github.com port 443
```

这不是你的问题，也不是仓库权限问题，重试一百次也一样。解决办法是改用 SSH 协议，并让 SSH 走 443 端口。下面几步只需做一次，之后一劳永逸。

**2.1 生成密钥**（如果 `~/.ssh/id_ed25519` 已存在就跳过这步）

```bash
ssh-keygen -t ed25519 -C "你的邮箱"
```

一路回车即可（保存路径用默认，密码短语留空——留空的话以后 push 不用反复输密码）。

**2.2 复制公钥**

```bash
cat ~/.ssh/id_ed25519.pub
```

输出的一整行（`ssh-ed25519 AAAA... 你的邮箱`）全部复制。

**2.3 添加到 GitHub**

打开 [github.com/settings/ssh/new](https://github.com/settings/ssh/new)，Title 随便填（比如 `我的笔记本`），Key 框里粘贴刚才复制的内容，点 **Add SSH key**。

> 注意是 **SSH key**，不是 GPG key，别加错地方。

**2.4 让 SSH 走 443 端口**

```bash
cat >> ~/.ssh/config <<'EOF'
Host github.com
    HostName ssh.github.com
    Port 443
    User git
    IdentityFile ~/.ssh/id_ed25519
    IdentitiesOnly yes
EOF
chmod 600 ~/.ssh/config
```

> Windows 用户在 Git Bash 里执行上面的命令即可，`~` 会自动对应到 `C:\Users\你的用户名`。

**2.5 验证**

```bash
ssh -T git@github.com
```

第一次会问 `Are you sure you want to continue connecting?`，输入 `yes` 回车。看到下面这句就成功了：

```
Hi 你的用户名! You've successfully authenticated, but GitHub does not provide shell access.
```

后半句 "does not provide shell access" 是正常的，不是错误。

## 第 3 步：克隆仓库

进到你想存放项目的目录（比如 `~/Documents`），然后：

```bash
git clone git@github.com:WASDwasding/campus-ershou-system.git
cd campus-ershou-system
```

`clone` 会自动创建 `campus-ershou-system` 文件夹、下载全部代码和历史、并把远程仓库地址记为 `origin`。这一步只做一次，以后更新代码用 `git pull`，**不要反复 clone**。

> 如果你之前已经用 HTTPS 地址克隆过，不用重新 clone，改一下远程地址就行：
> ```bash
> git remote set-url origin git@github.com:WASDwasding/campus-ershou-system.git
> ```

## 第 4 步：日常开发循环

这是你以后每天重复的流程，**每做一个功能就完整走一遍**。

### 4.1 先同步最新的 main

**每次开新功能前都要做这一步**，不然你的分支基于过时的代码，后面合并必然冲突。

```bash
git switch main   # 切到 main 分支
git pull          # 把队友的最新改动下载下来
```

### 4.2 开一个自己的分支

```bash
git switch -c feat/goods-publish
```

`-c` 是 create 的意思。分支名格式为 `类型/简短描述`，见 [README 的分支命名规范](../README.md#分支命名)。

一个分支只做一件事。要做两个不相关的功能，就开两个分支。

### 4.3 写代码

用你的 IDE 正常改代码。随时可以用这个命令查看当前状态：

```bash
git status
```

**`git status` 是你最重要的命令**。任何时候不知道自己在哪、改了什么、下一步该干啥，都先跑它。它会告诉你当前在哪个分支、哪些文件改了、哪些已进暂存区，还会直接提示你可用的命令。

### 4.4 提交

```bash
git add .                                              # 把所有改动放进暂存区
git commit -m "feat(goods): 支持发布二手商品并上传图片"    # 打包成一个存档点
```

关于 `git add .`：它会添加当前目录下所有改动。项目里的 `.gitignore` 已经帮你排除了 `node_modules/`、`target/`、`.env` 等不该提交的东西，所以正常情况下用 `.` 是安全的。但**提交前建议先跑一次 `git status` 看一眼列表**，确认没有意外的文件。

只想提交某几个文件：

```bash
git add src/main/java/com/xxx/GoodsController.java src/main/java/com/xxx/GoodsService.java
```

提交信息怎么写见 [README 的提交信息规范](../README.md#提交信息)。一个功能可以分多次 commit，比如先提交后端接口，再提交前端页面——小步提交比憋一个巨大的 commit 好得多，出问题容易定位。

### 4.5 推送到 GitHub

```bash
git push -u origin feat/goods-publish
```

`-u origin 分支名` 只有该分支第一次推送时需要，作用是建立本地分支与远程分支的关联。之后在这个分支上再推，直接 `git push` 就行。

### 4.6 开 Pull Request

推送成功后，打开 [仓库页面](https://github.com/WASDwasding/campus-ershou-system)，顶部会出现一个黄色提示条 **"Compare & pull request"**，点它。

或者手动点 **Pull requests** 标签页 → **New pull request**，base 选 `main`，compare 选你的分支。

填写 PR：

- **标题**：一句话说清这个 PR 做了什么
- **描述**：改了哪些地方、怎么测试的、有没有改数据库结构、有没有需要队友注意的坑
- **右侧 Reviewers**：可以指派管理员

开完后在群里跟管理员说一声，等他看完合并。

### 4.7 根据 review 意见修改

队友可能会提出修改建议。改法很简单——**不用关掉 PR 重开**，直接在同一个分支上继续改、继续提交、继续推：

```bash
# 改完代码
git add .
git commit -m "fix(goods): 按 review 意见补充图片大小校验"
git push
```

PR 会自动更新，包含你的新提交。

### 4.8 合并

**不要自己点 Merge，也不要推 `main`。** 开完 PR 跟管理员说一声，由管理员合并。

### 4.9 收尾，回到 4.1

合并后远程分支会自动删除。清理本地：

```bash
git switch main
git pull                          # 把刚合并的内容拉下来
git branch -d feat/goods-publish  # 删掉本地的功能分支
```

然后开始下一个功能，重新从 4.1 开始。

## 常用命令速查

| 命令 | 作用 |
| --- | --- |
| `git status` | **看当前状态，迷路时先跑这个** |
| `git switch main` | 切换到 main 分支 |
| `git switch -c 分支名` | 新建并切换到该分支 |
| `git switch 分支名` | 切换到已存在的分支 |
| `git branch` | 列出本地所有分支，`*` 标记当前所在 |
| `git pull` | 下载远程最新改动并合并到当前分支 |
| `git add 文件名` / `git add .` | 把改动放进暂存区 |
| `git commit -m "说明"` | 把暂存区打包成一个提交 |
| `git push` | 上传当前分支的提交到 GitHub |
| `git log --oneline -10` | 看最近 10 条提交历史 |
| `git diff` | 看还没 `add` 的改动具体是什么 |
| `git diff --staged` | 看已经 `add` 但还没 commit 的改动 |

## 出问题了怎么办

### 改错了，想丢掉改动

```bash
# 丢掉某个文件的未暂存改动（改回上次提交的样子）
git restore src/main/java/com/xxx/GoodsController.java

# 丢掉所有未暂存改动
git restore .
```

> `git restore` 会**永久丢弃**这些改动，无法恢复。执行前确认清楚。

### 已经 `git add` 了，想撤出暂存区

```bash
git restore --staged 文件名
```

文件改动会保留，只是退出篮子。

### commit 信息写错了（还没 push）

```bash
git commit --amend -m "正确的提交信息"
```

### 想撤销上一个 commit，但保留代码改动（还没 push）

```bash
git reset --soft HEAD~1
```

改动会退回暂存区，你可以重新组织后再提交。

### 推送被拒绝：`Updates were rejected`

说明远程有你本地没有的提交（通常是队友推了东西，或者你在另一台电脑上推过）。先拉再推：

```bash
git pull
git push
```

### 推送被拒绝：`protected branch hook declined`

你正在往 `main` 推。这是被禁止的。确认一下自己在哪个分支：

```bash
git branch
```

如果显示 `* main`，说明你不小心把代码直接写在 main 上了。把这些改动挪到新分支上即可：

```bash
git switch -c feat/我的功能   # 带着改动切到新分支
git push -u origin feat/我的功能
```

### 遇到冲突（conflict）

冲突是指你和队友改了同一个文件的同一处，git 不知道该保留谁的。**冲突很正常，不是谁做错了。**

当 `git pull` 或 `git rebase` 报冲突时，打开报错提到的文件，你会看到这样的标记：

```
<<<<<<< HEAD
这是当前分支的内容
=======
这是要合并进来的内容
>>>>>>> main
```

你要做的是：**手动编辑这段，改成最终想要的样子，并删掉 `<<<<<<<`、`=======`、`>>>>>>>` 这三行标记**。可能是保留其中一边，也可能是两边都要、手工融合。改不明白就问改了这块代码的队友。

处理完所有冲突文件后：

```bash
git add 冲突的文件
git rebase --continue   # 如果是 rebase 过程中的冲突
# 或
git commit              # 如果是 pull/merge 过程中的冲突
```

想中途放弃、回到冲突前的状态：

```bash
git rebase --abort   # 或 git merge --abort
```

### PR 显示有冲突，无法合并

`main` 在你开发期间有了新提交。在你自己的分支上 rebase：

```bash
git switch main && git pull
git switch 你的分支名
git rebase main
# 按上面的方法解决冲突
git push --force-with-lease
```

> `--force-with-lease` 只能用在**你自己的功能分支**上，因为 rebase 会重写提交历史，必须强推才能更新远程。
> **永远不要对 `main` 强推**，也不要对别人也在用的分支强推。

### 彻底乱了，想推倒重来

如果你的本地状态已经完全混乱、又不在乎本地改动，可以强制回到与远程一致的状态：

```bash
git fetch origin
git switch main
git reset --hard origin/main
```

> `--hard` 会**删除所有未提交的改动**，无法恢复。真的确定了再执行。
> 如果有想保留的代码，先手动复制到项目外的地方。
