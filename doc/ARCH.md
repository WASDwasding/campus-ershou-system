# 架构说明

单体仓库：`backend`（Spring Boot 3 / Java 17）+ `frontend`（Vue3）。部署时前端 `npm run build` 产出静态文件，后端单独跑 JAR。图片走阿里云 OSS。

## 模块

| 包 | 职责 | 对应攻击面 |
| --- | --- | --- |
| `auth` | 注册登录、签发 JWT | 弱口令、token 伪造 |
| `user` | 用户资料 | 越权读他人信息 |
| `goods` | 发布、浏览、上下架 | XSS、越权改别人的商品 |
| `order` | 下单、库存 | 价格篡改、并发超卖 |
| `pay` | 模拟支付 | 支付绕过 |
| `im` | 即时通讯 | 越权拉聊天记录 |
| `ai` | 定价建议、议价客服 | 密钥泄露、提示词注入 |
| `common` | 鉴权、异常、OSS、统一响应 | 全局配置错误 |

## 下单与支付约定

1. 下单接口只收 `goodsId` 和 `quantity`，`amount` 按库里的 `goods.price` 计算，丢弃前端传来的价格。
2. 扣库存必须走 `GoodsMapper.deductStock`：`UPDATE ... SET stock = stock - n WHERE stock >= n`，影响行数为 0 就失败。不要先 SELECT 再减。
3. 订单状态只允许：`PENDING_PAY → PAID → COMPLETED`，或 `PENDING_PAY → CANCELLED`。模拟支付接口自己改状态，禁止客户端传 `paid=true`。
4. 查订单、聊天、改商品时用 `SecurityUtils.current().getUserId()` 核对归属，不能只靠隐藏按钮。

## 本地依赖

- MySQL 8、Redis 7。可用根目录 `docker-compose.yml`，或把 `application-local.yml` 指到云服务器。
- OSS、JWT、大模型 Key 只写 `application-local.yml`。

## 云服务器

后端监听 `8080`，前面用 Nginx 反代 `/api` 和 `/ws`。前端静态资源也由 Nginx 托管。OSS 建议绑定独立域名，Bucket 设为私有 + 签名 URL 也可以，第一期用公共读简化演示。安全组只开放 22 / 80 / 443。
