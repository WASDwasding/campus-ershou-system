-- 校园智能二手交易平台 初始表结构
-- 字符集 utf8mb4。在已创建的 campus_ershou 库中执行。

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(32)  NOT NULL,
    password_hash VARCHAR(100) NOT NULL,
    nickname      VARCHAR(32)  NOT NULL,
    phone         VARCHAR(20)  NULL,
    avatar_url    VARCHAR(512)  NULL,
    role          VARCHAR(16)  NOT NULL DEFAULT 'USER',
    deleted       TINYINT       NOT NULL DEFAULT 0,
    created_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS goods (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id   BIGINT         NOT NULL,
    title       VARCHAR(80)    NOT NULL,
    description TEXT           NULL,
    price       DECIMAL(10, 2) NOT NULL,
    stock       INT            NOT NULL DEFAULT 1,
    version     INT            NOT NULL DEFAULT 0,
    status      VARCHAR(16)    NOT NULL DEFAULT 'ON_SALE',
    cover_url   VARCHAR(512)  NULL,
    deleted     TINYINT        NOT NULL DEFAULT 0,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_seller (seller_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 金额 amount 只由服务端按商品表现价计算，禁止信任前端传入的价格
-- 状态机：PENDING_PAY -> PAID -> COMPLETED；或 PENDING_PAY -> CANCELLED
CREATE TABLE IF NOT EXISTS orders (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no    VARCHAR(32)    NOT NULL,
    buyer_id    BIGINT         NOT NULL,
    seller_id   BIGINT         NOT NULL,
    goods_id    BIGINT         NOT NULL,
    quantity    INT            NOT NULL DEFAULT 1,
    amount      DECIMAL(10, 2) NOT NULL,
    status      VARCHAR(16)    NOT NULL,
    paid_at     DATETIME        NULL,
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_buyer (buyer_id),
    KEY idx_seller (seller_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS chat_messages (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    from_user_id  BIGINT        NOT NULL,
    to_user_id    BIGINT        NOT NULL,
    goods_id      BIGINT        NULL,
    content       VARCHAR(2000) NOT NULL,
    created_at    DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    KEY idx_pair (from_user_id, to_user_id),
    KEY idx_goods (goods_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
