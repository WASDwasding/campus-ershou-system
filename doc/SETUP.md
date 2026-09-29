# 启动

Git 不会用先看 [TONEW.md](./TONEW.md)。接口字段看 [base-API.md](./base-API.md)。

需要：Git、JDK 17+、Maven、Node 18+、[微信开发者工具](https://developers.weixin.qq.com/miniprogram/dev/devtools/download.html)。现在跑健康检查不需要数据库。开始写业务表之后，本机再装 MySQL 和 Redis，或用仓库里的 `docker-compose.yml`。

```bash
git clone git@github.com:WASDwasding/campus-ershou-system.git
cd campus-ershou-system
```

后端：

```bash
cd backend
mvn spring-boot:run
```

`curl http://127.0.0.1:8080/api/health` 应返回 `{"service":"campus-ershou","status":"UP"}`。

前端（uni-app → 微信小程序）另开终端：

```bash
cd frontend
npm install
npm run dev
```

编译产物在 `frontend/dist/dev/mp-weixin`（已 gitignore，不要提交）。用微信开发者工具「导入项目」，目录选这一层。

开发者工具里勾选：**不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书**。否则请求不到本机 `http://127.0.0.1:8080`。

真机预览时，把 `frontend/src/config.js` 里的地址改成电脑的局域网 IP，不要改完提交。微信 AppID 只在本机填写，仓库里保持空。还没注册正式 AppID 时，本机用接口文档里的免微信登录；体验版和手机扫码要等 AppID。

每人在自己电脑上开发和自测。五个人对接口、跑 Apifox，用云服务器上的 test 环境；给老师看用 demo 环境。两套都用独立的库和 Redis 前缀，密钥放本机配置，不进仓库。细节见 [base-API.md](./base-API.md) 第 2 节。

macOS 若 `java` 报找不到 Runtime：

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
```
