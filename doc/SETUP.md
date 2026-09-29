# 启动

Git 不会用先看 [TONEW.md](./TONEW.md)。接口字段看 [base-API.md](./base-API.md)。

需要：Git、JDK 17+、Maven、Node 18+、[微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)。前端不用在本机装数据库，也不用在本机起后端。后端写接口时才需要本机的 MySQL 和 Redis，可以用仓库里的 `docker-compose.yml`。

```bash
git clone git@github.com:WASDwasding/campus-ershou-system.git
cd campus-ershou-system
```

前端（uni-app → 微信小程序）：

```bash
cd frontend
npm install
npm run dev
```

编译产物在 `frontend/dist/dev/mp-weixin`（已 gitignore，不要提交）。用微信开发者工具「导入项目」，目录选这一层。`frontend/src/config.js` 默认请求 `https://simpleyi.site`，健康检查是 <https://simpleyi.site/api/health>。

开发者工具里勾选：**不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书**。还没注册正式 AppID 时，先用开发者工具看页面；体验版和手机扫码要等 AppID，并在小程序后台把 `simpleyi.site` 配进合法域名。

后端在本机写接口：

```bash
cd backend
mvn spring-boot:run
```

本机健康检查：`curl http://127.0.0.1:8080/api/health`。测自己的后端时，把 `config.js` 临时改成 `http://127.0.0.1:8080`，测完改回去，不要提交。

接口测通后，由一个人部署到服务器：把 jar 传到 `/opt/campus-ershou/campus-ershou.jar`，然后 `sudo systemctl restart campus-ershou`。服务器上的数据库密码在 `/opt/campus-ershou/application-local.yml`，不要用本机配置覆盖，也不要提交。库名是 `ershou`。没测完的包不要先传上去。

macOS 若 `java` 报找不到 Runtime：

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
```
