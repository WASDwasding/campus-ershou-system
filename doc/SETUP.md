# 启动

Git 不会用先看 [TONEW.md](./TONEW.md)。

需要：Git、JDK 17+、Maven、Node 18+。现在不需要数据库。

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

前端另开终端：

```bash
cd frontend
npm install
npm run dev
```

打开 http://127.0.0.1:5173 。

macOS 若 `java` 报找不到 Runtime：

```bash
export JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home
export PATH="$JAVA_HOME/bin:$PATH"
```

数据库、OSS、部署等做到再补。
