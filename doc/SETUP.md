# 开发环境与启动教程

Git 不会用先看 [TONEW.md](./TONEW.md)。模块约定见 [ARCH.md](./ARCH.md)。

当前仓库是**目录骨架**：后端只有 `GET /api/health`，各业务包是空的。不需要先起 MySQL 也能把项目跑起来。

---

## 1. 安装必备软件

| 软件 | 用途 | 确认 |
| --- | --- | --- |
| Git | 拉代码 | `git --version` |
| JDK 17+ | 跑后端 | `java -version` |
| Maven 3.9+ | 编译后端 | `mvn -v` |
| Node 18+ | 跑前端 | `node -v` |

Windows 用 **Git Bash**。没配 GitHub SSH 的按 [TONEW 第 2 步](./TONEW.md#第-2-步配置-ssh国内网络几乎必须)。

**macOS 若 `java` 报找不到 Runtime**（Homebrew 已装 OpenJDK 时）：

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
```

把这两行写进 `~/.zshrc` 可永久生效。Maven：`brew install maven`。

---

## 2. 克隆

```bash
git clone git@github.com:WASDwasding/campus-ershou-system.git
cd campus-ershou-system
git switch main
git pull
```

---

## 3. 启动（现在就能做）

**终端 1 — 后端**

```bash
cd backend
mvn spring-boot:run
```

```bash
curl http://127.0.0.1:8080/api/health
```

期望：`{"service":"campus-ershou","status":"UP"}`

**终端 2 — 前端**

```bash
cd frontend
npm install
npm run dev
```

打开 http://127.0.0.1:5173 。后端开着时，首页应显示健康检查 **UP**。

前端 `/api` 由 Vite 转到 `8080`，见 `frontend/vite.config.js`。

| 现象 | 处理 |
| --- | --- |
| `Unable to locate a Java Runtime` | 导出 `JAVA_HOME`，见第 1 节 |
| `Port 8080 already in use` | 关掉占用进程 |
| 首页显示「后端未启动」 | 先把 `mvn spring-boot:run` 跑起来 |

---

## 4. 数据库、Redis、OSS（写业务时再配）

空骨架不连库。开始做登录、商品时再准备 MySQL 8 和 Redis，并把依赖加进 `backend/pom.xml`。

**本机 Docker**（需先装 Docker Desktop）：

```bash
docker compose up -d
```

会起空的 MySQL（库 `campus_ershou`，root / `campus`）和 Redis，**不会建业务表**。表结构和说明写在 [`sql/`](../sql/)。

**云服务器：** 先有一台能连的 MySQL/Redis 即可，建表以后再说。开发期建议 SSH 隧道，避免 3306 对公网开放：

```bash
ssh -N -L 3306:127.0.0.1:3306 -L 6379:127.0.0.1:6379 root@<云服务器IP>
```

然后：

```bash
cp backend/src/main/resources/application-local.yml.example \
   backend/src/main/resources/application-local.yml
```

取消注释并填写数据库、OSS、JWT。`application-local.yml` 已被 gitignore，**不要提交**。

OSS：阿里云 Bucket 与 endpoint 同区域；第一期可用公共读。没配 OSS 时不要做图片上传。

---

## 5. 代码写在哪

| 功能 | 包 |
| --- | --- |
| 注册登录 | `backend/.../auth/` |
| 用户资料 | `user/` |
| 商品 | `goods/` |
| 下单库存 | `order/` |
| 模拟支付 | `pay/` |
| 聊天 | `im/` |
| 定价议价 | `ai/` |
| JWT / OSS / 统一异常 | `common/` |

前端页面继续放在 `frontend/src/`。需要路由、axios 时再 `npm install vue-router axios pinia`。

写代码前：

```bash
git switch main && git pull
git switch -c feat/你的功能
```

`main` 不要直接 push。开 PR 后跟管理员说一声再合，流程见 [TONEW 第 4 步](./TONEW.md#第-4-步日常开发循环)。

安全约定（价格、超卖、越权、支付、SQL、XSS）写在 [ARCH.md](./ARCH.md)，实现对应功能时必须遵守。

---

## 6. 上线时再看

功能做完后：`mvn -DskipTests package` 得到 jar，`npm run build` 得到 `frontend/dist`。云主机 Nginx 反代 `/api` 到 8080，静态资源托管 `dist`。安全组只开 22 / 80 / 443。
