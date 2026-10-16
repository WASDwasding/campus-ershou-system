# 启动

Git 不会用先看 [TONEW.md](./TONEW.md)。接口字段看 [base-API.md](./base-API.md)。表结构看 [../sql/README.md](../sql/README.md)。

需要：Git、JDK 17+、Maven、[微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)。前端不用在本机装数据库，也不用在本机起后端。后端写接口时才需要本机的 MySQL 和 Redis，可以用仓库里的 `docker-compose.yml`。

```bash
git clone git@github.com:WASDwasding/campus-ershou-system.git
cd campus-ershou-system
```

前端是原生微信小程序，不用编译。用微信开发者工具「导入项目」，目录选 `campus-trade/miniprogram`。`campus-trade/miniprogram/utils/config.js` 默认请求 `https://simpleyi.site`，健康检查是 <https://simpleyi.site/api/health>。

开发者工具里勾选：**不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书**。还没注册正式 AppID 时，先用开发者工具看页面；体验版和手机扫码要等 AppID，并在小程序后台把 `simpleyi.site` 配进合法域名。

后端在本机写接口。不要用系统自带的 3306 去套 `campus` 密码——本机那套 root 密码和 docker-compose 不是同一个，硬连会 `Access denied`。

推荐用项目自己的 MySQL（**3307**，不占用 3306）：

```bash
# 终端 1，保持运行（第一次会初始化数据目录 .data/mysql，已 gitignore）
bash sql/start-local-mysql.sh

# 终端 2
cd backend
mvn spring-boot:run
```

库名 `campus_ershou`，账号 `root` / `campus`，地址 `127.0.0.1:3307`。本机还要有 Redis（`127.0.0.1:6379`）。首次启动 Flyway 会建表并写入分类、楼栋。

`application-local.yml` 已被 gitignore。把 example 复制过去后填写 163 SMTP（发信账号 `campus_cat_bazaar@163.com`，密码填客户端授权码）；数据源默认指向 3307。本机若 `app.mail.enabled: true`，注册会发真实邮件，响应不再带 `debugCode`。

本机健康检查：`curl http://127.0.0.1:8080/api/health`。测自己的后端时，把 `campus-trade/miniprogram/utils/config.js` 临时改成 `http://127.0.0.1:8080`，测完改回去，不要提交。

若坚持用系统 MySQL 3306：在 `application-local.yml` 把端口改成 3306，并填你自己的 root 密码。可用 `docker compose up -d` 起一套密码为 `campus` 的 MySQL/Redis，那是另一套库，和系统 3306 无关。

停掉项目 MySQL：`bash sql/stop-local-mysql.sh`。

线上已在 2026-10-10 部署。再更新时：把 jar 传到 `/opt/campus-ershou/campus-ershou.jar`，然后 `sudo systemctl restart campus-ershou`。服务器配置在 `/opt/campus-ershou/application-local.yml`，不要用本机整份覆盖（库名是 `ershou`，和本机 `campus_ershou` 不同）。邮件、图片目录 `/opt/campus-ershou/files` 和 `public-base-url: https://simpleyi.site` 已经写在那份配置里。没测完的包不要先传上去。启动时 Flyway 会自己迁到当前版本，不要在服务器上手动导入 `schema.sql`。

macOS 若 `java` 报找不到 Runtime：

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
```
