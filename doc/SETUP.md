# 开发环境与启动教程

给第一次把本仓库跑起来的同学。Git 还不会的先看 [TONEW.md](./TONEW.md)。模块和安全约定见 [ARCH.md](./ARCH.md)。

读完并做完本文，你应该能：

1. 本机打开前端页面
2. 后端 `/api/health` 返回 `UP`
3. 知道代码写在哪个包、密钥放哪、改完怎么提 PR

---

## 0. 先弄清三件事

**这是前后端分离。** 后端 `8080` 提供 JSON 接口，前端 `5173` 是网页。浏览器只访问 `5173`，网页里的 `/api/...` 会被 Vite 转到 `8080`。你不需要在浏览器里手输 `8080`，除非在查后端是否活着。

**现在是空骨架。** 登录、发商品、下单都返回 HTTP 501（尚未实现）。能启动、健康检查是 UP，就算成功。

**密钥不能进 GitHub。** 仓库是公开的。`application-local.yml`、`.env` 已被 gitignore。队友之间用微信/飞书私发配置，不要开 PR 贴 AccessKey。

---

## 1. 安装必备软件

| 软件 | 用途 | 怎么确认装好 |
| --- | --- | --- |
| Git | 拉代码、提 PR | `git --version` |
| JDK 17 或更高 | 跑后端 | `java -version` |
| Maven 3.9+ | 编译后端 | `mvn -v` |
| Node 18+ | 跑前端 | `node -v` 和 `npm -v` |
| MySQL 8 | 存用户/商品/订单 | 见第 3 节 |
| Redis | 缓存和以后的锁 | 见第 3 节 |

Windows 请用 **Git Bash** 执行本文所有命令，不要用 CMD。

### 1.1 Git / Node

- Windows： [Git](https://git-scm.com/download/win)、[Node LTS](https://nodejs.org/)
- macOS：已有 Homebrew 的话 `brew install git node`
- 没配过 GitHub SSH 的，按 [TONEW 第 2 步](./TONEW.md#第-2-步配置-ssh国内网络几乎必须) 做完再 clone

### 1.2 JDK 和 Maven

**macOS（本仓库创建者这台机器的情况）：** 系统自带的 `java` 可能报 `Unable to locate a Java Runtime`，但 Homebrew 已经装了 OpenJDK。每次新开终端先执行：

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

要一劳永逸，把上面两行 `export` 写进 `~/.zshrc`，然后 `source ~/.zshrc`。

建议团队统一 **JDK 17**。若你装的是 21/25 也可以跑，`pom.xml` 里编译目标仍是 17。

Maven：

```bash
# macOS
brew install maven

# 确认
mvn -v
```

输出里应能看到 Java version，且不要再报找不到 Runtime。

**Windows：** 安装 [Eclipse Temurin 17](https://adoptium.net/)，安装勾选 “Set JAVA_HOME”。Maven 可用 [Maven 官网](https://maven.apache.org/download.cgi) 或 scoop/choco。

### 1.3 还缺 MySQL 和 Redis

两条路，选一条即可：

- **A. 本机 Docker**（有 Docker Desktop 时最省事）
- **B. 用已有的云服务器**（你们手里有云主机时推荐，全组连同一套库，数据能对上）

没有 Docker 又没有云库，就得本机安装 MySQL 8 和 Redis，步骤与 B 类似，地址填 `127.0.0.1`。

---

## 2. 克隆仓库

```bash
git clone git@github.com:WASDwasding/campus-ershou-system.git
cd campus-ershou-system
git switch main
git pull
```

目录应类似：

```
campus-ershou-system/
  backend/     后端
  frontend/    前端
  sql/         建表脚本
  doc/         文档
  docker-compose.yml
```

---

## 3. 准备 MySQL 和 Redis

### 方案 A：Docker（本机）

先安装 [Docker Desktop](https://www.docker.com/products/docker-desktop/)，打开它等到左下角变绿。

在**仓库根目录**执行：

```bash
docker compose up -d
docker compose ps
```

应看到 `campus-ershou-mysql`、`campus-ershou-redis` 为 running。

首次启动会自动建库 `campus_ershou` 并执行 `sql/init.sql`。默认账号：

| 项 | 值 |
| --- | --- |
| MySQL 地址 | `127.0.0.1:3306` |
| 库名 | `campus_ershou` |
| 用户 | `root` |
| 密码 | `campus` |
| Redis | `127.0.0.1:6379` 无密码 |

以后开机若容器没起来，再执行一次 `docker compose up -d`。

> 只在「空数据卷第一次创建」时自动跑 `init.sql`。容器已经存在、你后来改了 SQL，需要自己进库再执行一遍，或删卷重建（会丢数据）：
> `docker compose down -v && docker compose up -d`

### 方案 B：云服务器（推荐给全组共用）

在云主机上安装 MySQL 8 和 Redis（Ubuntu 示例）：

```bash
sudo apt update
sudo apt install -y mysql-server redis-server
sudo systemctl enable --now mysql redis-server
```

建库并导入表结构（把仓库里的 `sql/init.sql` 拷到服务器，或本机 `mysql` 客户端直连）：

```sql
CREATE DATABASE campus_ershou DEFAULT CHARACTER SET utf8mb4;
```

```bash
mysql -h <云服务器IP> -u root -p campus_ershou < sql/init.sql
```

**安全组 / 防火墙：** 开发期可以临时对队员 IP 开放 `3306`、`6379`。不要对 `0.0.0.0/0` 裸开。更好的做法是 SSH 隧道，本机连隧道端口：

```bash
# 另开一个终端，保持不关
ssh -N -L 3306:127.0.0.1:3306 -L 6379:127.0.0.1:6379 root@<云服务器IP>
```

这样 `application-local.yml` 仍写 `127.0.0.1`，数据库实际在云上。

MySQL 若允许远程直连，需要：

1. `CREATE USER 'ershou'@'%' IDENTIFIED BY '强密码'; GRANT ALL ON campus_ershou.* TO 'ershou'@'%'; FLUSH PRIVILEGES;`
2. `bind-address = 0.0.0.0`（`mysqld.cnf`）后重启 MySQL
3. 云安全组只放行队员 IP

Redis 默认只绑 127.0.0.1。要远程用必须设密码，并限制来源 IP。开发更省事的是上面的 SSH 隧道。

---

## 4. 填写本地配置（每人做一次）

### 4.1 后端

```bash
cd backend
cp src/main/resources/application-local.yml.example src/main/resources/application-local.yml
```

用编辑器打开 `application-local.yml`，按你的方案改：

**连本机 Docker：** 默认值即可，但请立刻改 `app.jwt.secret`（至少 32 个随机字符）。

**连云 MySQL（直连）：**

```yaml
spring:
  datasource:
    url: jdbc:mysql://<云服务器IP>:3306/campus_ershou?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: ershou
    password: 你的强密码
  data:
    redis:
      host: <云服务器IP>
      port: 6379
      password: 若有则填写
```

### 4.2 OSS（阿里云）

控制台创建 Bucket（与 `endpoint` 同区域，例如杭州 `oss-cn-hangzhou`）。第一期演示可用 **公共读**，目录前缀已设为 `campus-ershou/`。

创建子账号 AccessKey（不要用主账号永久密钥），只授这个 Bucket 的读写。填：

```yaml
app:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com   # 换成你的区域，不要加 https://
    bucket: 你的bucket名
    access-key-id: LTAI...
    access-key-secret: ...
    custom-domain: ""   # 有绑定域名再填，如 https://cdn.xxx.com
    dir-prefix: campus-ershou/
```

没填 OSS 时，**后端仍能启动**。真正上传图片才会报「OSS 未配置」。

### 4.3 JWT 和大模型

```yaml
app:
  jwt:
    secret: 请改成一长串随机字符至少32位不要用示例值
  ai:
    api-key: ""    # 做智能定价/议价时再填，例如 DeepSeek
```

全组开发可以用同一套 JWT secret，方便互相拿 token 调接口；**不要提交到仓库**。

### 4.4 前端

```bash
cd frontend
cp .env.example .env
```

开发默认 `VITE_API_BASE=/api`，一般不用改。

---

## 5. 启动后端

开一个终端，**先保证 JDK 在 PATH 里**（见 1.2），然后：

```bash
cd campus-ershou-system/backend
mvn spring-boot:run
```

第一次会下载依赖，可能要几分钟。成功的标志是日志里出现：

```
Started CampusErshouApplication
```

另开浏览器或终端访问：

```bash
curl http://127.0.0.1:8080/api/health
```

期望类似：

```json
{"code":0,"message":"ok","data":{"service":"campus-ershou","status":"UP","time":"..."}}
```

试一个尚未实现的接口：

```bash
curl -i -X POST http://127.0.0.1:8080/api/auth/login
```

应看到 `HTTP/1.1 501`，body 里 `登录尚未实现`。这说明安全过滤器和占位接口是通的。

常用：

| 现象 | 处理 |
| --- | --- |
| `Unable to locate a Java Runtime` | 没导出 `JAVA_HOME`，见 1.2 |
| `Communications link failure` / `Connection refused` 3306 | MySQL 没起，或 IP/端口/安全组不对 |
| `Unable to connect to Redis` 或启动很久后失败 | Redis 没起，检查 host/port |
| `Port 8080 already in use` | 关掉占用 8080 的进程，或改 `application-local.yml` 里 `server.port` |
| 改了 Java 代码没生效 | 停掉（终端 Ctrl+C）再 `mvn spring-boot:run` |

用 IDE 跑：用 IntelliJ 打开 `backend` 文件夹（或打开仓库根目录后把 backend 当 Maven 项目），运行 `CampusErshouApplication`。同样要先配好 JDK 17。

---

## 6. 启动前端

**再开一个终端**（后端那个不要关）：

```bash
cd campus-ershou-system/frontend
npm install
npm run dev
```

看到 `Local: http://localhost:5173/` 后，浏览器打开该地址。

正常现象：

- 顶栏「校园智能二手」，能点「商品」「登录」
- 首页「后端健康检查」为 **UP**（后端没开则显示「后端未启动或数据库未连通」）
- 登录按钮是禁用的，商品页是占位说明——这是骨架，不是坏了

改 Vue 文件后保存，浏览器会自动刷新。

---

## 7. 请求是怎么走的

```
浏览器 5173  →  /api/health
        Vite 代理到  →  127.0.0.1:8080/api/health
                              Spring  →  HealthController
```

`frontend/vite.config.js` 里配置了 `/api` 和 `/ws` 的代理。前端 `src/api/http.js` 会自动带上 `localStorage` 里的 `token`（登录实现后）。

需要登录的接口现在会返回 401 JSON：`未登录或登录已过期`。公开接口目前只有：

- `GET /api/health`
- `/api/auth/**`（注册登录，仍是 501）
- `GET /api/goods`、`GET /api/goods/{id}`
- WebSocket `/ws/**`

---

## 8. 代码写在哪

按包分工，**不要把所有逻辑塞进一个 Controller**。

| 你要做的功能 | 主要改这些 |
| --- | --- |
| 注册 / 登录 | `backend/.../auth/` ，密码用已有的 `PasswordEncoder`（BCrypt），登录成功后用 `JwtService.issue(...)` |
| 当前用户 | `user/controller`，用 `SecurityUtils.current().getUserId()` |
| 商品发布 / 列表 | `goods/` ，图片调用 `OssStorageService.upload(...)` |
| 下单 / 库存 | `order/` + `GoodsMapper.deductStock`，金额只信数据库里的 `goods.price` |
| 模拟支付 | `pay/` ，只允许 `PENDING_PAY → PAID` |
| 聊天 | `im/` + `common/ws/WebSocketConfig` |
| 定价 / 议价 | `ai/` + `common/ai/AiClient` |

表结构在 `sql/init.sql`。**改表必须同时改这个文件**（或新增 `sql/00x_xxx.sql`），并在 PR 里说明，方便队友同步。

前端页面在 `frontend/src/views/`，接口封装往 `frontend/src/api/` 加，登录状态用 `stores/user.js`。

写代码前：

```bash
git switch main
git pull
git switch -c feat/你的功能
```

提交和开 PR 见 [TONEW 第 4 步](./TONEW.md#第-4-步日常开发循环)。`main` 不能直接 push。

---

## 9. 写业务时必须遵守（对抗赛会打这些）

1. **价格**：下单接口不要接收前端的 `price`/`amount`，服务端按商品表计算。
2. **超卖**：调用 `GoodsMapper.deductStock`，影响行数是 0 就返回失败。不要 `SELECT stock` 再 `UPDATE`。
3. **越权**：查/改订单、商品、聊天时比对 `SecurityUtils.current().getUserId()` 是否为买家/卖家。
4. **支付**：禁止请求体里的 `paid=true`；状态机见 `OrderStatus`。
5. **SQL**：MyBatis 只用 `#{}`，禁止 `${}` 拼 SQL。
6. **XSS**：用户输入的标题、描述不要当 HTML 插；Vue 默认会转义，不要用 `v-html` 显示用户内容。
7. **密钥**：OSS、JWT、AI Key、数据库密码只存在 `application-local.yml`。

---

## 10. 云服务器部署（功能做完再做，开发期可跳过）

目标：一台云主机对外 80/443，本机开发仍用 5173。

1. 服务器安装 JDK 17、Nginx、MySQL、Redis（若尚未安装）。
2. 本机打包：

```bash
cd backend && mvn -DskipTests package
# 得到 backend/target/ershou-0.0.1-SNAPSHOT.jar

cd frontend && npm run build
# 得到 frontend/dist/
```

3. 把 jar 和 `dist/` 传到服务器。用 systemd 跑 jar（`--spring.profiles.active=local` 且服务器上放一份不含进 git 的 `application-local.yml`，或改用环境变量）。
4. Nginx 示例：

```nginx
server {
    listen 80;
    server_name 你的域名或IP;

    location /api/ {
        proxy_pass http://127.0.0.1:8080;
    }
    location /ws/ {
        proxy_pass http://127.0.0.1:8080;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }
    location / {
        root /var/www/ershou;
        try_files $uri $uri/ /index.html;
    }
}
```

5. 把 `dist` 内容放到 `/var/www/ershou`。安全组只开 22、80、443。
6. 后端 `app.cors.allowed-origins` 加上 `http://你的域名`（或 https）。

---

## 11. 日常命令速查

```bash
# 更新主线
git switch main && git pull

# 后端
cd backend && mvn spring-boot:run

# 前端
cd frontend && npm run dev

# 只编译后端（不启动，用来确认没写错）
cd backend && mvn -DskipTests compile

# Docker 库
docker compose ps
docker compose logs -f mysql
docker compose down          # 停容器，数据还在
```

---

## 12. 当前接口一览（骨架）

| 方法 | 路径 | 现在的行为 |
| --- | --- | --- |
| GET | `/api/health` | 返回 UP |
| POST | `/api/auth/register` | 501 |
| POST | `/api/auth/login` | 501 |
| GET | `/api/users/me` | 需登录，501 |
| GET | `/api/goods` | 501（无需登录） |
| GET | `/api/goods/{id}` | 501（无需登录） |
| POST | `/api/goods` | 需登录，501 |
| POST | `/api/orders` | 需登录，501 |
| GET | `/api/orders` | 需登录，501 |
| POST | `/api/pay/mock` | 需登录，501 |
| GET | `/api/im/messages` | 需登录，501 |
| POST | `/api/ai/price-suggest` | 需登录，501 |
| POST | `/api/ai/bargain` | 需登录，501 |

建议实现顺序：**注册登录 → 商品列表/发布（含 OSS）→ 下单+扣库存 → 模拟支付 → IM → AI**。登录没做完，后面带权限的接口都调不通。
