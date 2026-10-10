ALTER TABLE users
    ADD COLUMN dev_openid VARCHAR(40) NULL COMMENT '仅本机 dev-login' AFTER token_version,
    ADD UNIQUE KEY uk_users_dev_openid (dev_openid);

CREATE TABLE conversations (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    goods_id         BIGINT NOT NULL,
    buyer_id         BIGINT NOT NULL,
    seller_id        BIGINT NOT NULL,
    buyer_last_read  BIGINT NOT NULL DEFAULT 0,
    seller_last_read BIGINT NOT NULL DEFAULT 0,
    last_message_at  DATETIME(3) NULL,
    created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_conv_goods_buyer (goods_id, buyer_id),
    KEY idx_conv_buyer (buyer_id, id),
    KEY idx_conv_seller (seller_id, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品会话';

CREATE TABLE messages (
    id               BIGINT PRIMARY KEY AUTO_INCREMENT,
    conversation_id  BIGINT NOT NULL,
    sender_id        BIGINT NULL,
    type             VARCHAR(16) NOT NULL,
    content          VARCHAR(500) NOT NULL,
    raw_content      VARCHAR(500) NULL,
    client_msg_id    VARCHAR(40) NULL,
    masked           TINYINT(1) NOT NULL DEFAULT 0,
    created_at       DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_msg_client (conversation_id, sender_id, client_msg_id),
    KEY idx_msg_conv (conversation_id, id),
    CONSTRAINT fk_msg_conv FOREIGN KEY (conversation_id) REFERENCES conversations (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='聊天消息';

CREATE TABLE orders (
    id                   BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no             VARCHAR(32) NOT NULL,
    buyer_id             BIGINT NOT NULL,
    seller_id            BIGINT NOT NULL,
    goods_id             BIGINT NOT NULL,
    status               VARCHAR(16) NOT NULL,
    amount               INT NOT NULL,
    quantity             INT NOT NULL,
    goods_title          VARCHAR(30) NOT NULL,
    goods_cover_file_id  VARCHAR(64) NULL,
    conversation_id      BIGINT NULL,
    meetup_building_id   BIGINT NULL,
    meetup_place         VARCHAR(40) NULL,
    meetup_time          DATETIME(3) NULL,
    buyer_handover_at    DATETIME(3) NULL,
    seller_handover_at   DATETIME(3) NULL,
    cancel_by            VARCHAR(16) NULL,
    cancel_reason        VARCHAR(200) NULL,
    expire_at            DATETIME(3) NULL,
    paid_at              DATETIME(3) NULL,
    completed_at         DATETIME(3) NULL,
    cancelled_at         DATETIME(3) NULL,
    created_at           DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at           DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    active_goods_id      BIGINT GENERATED ALWAYS AS (
        CASE WHEN status IN ('PENDING_PAY', 'PAID', 'DISPUTED') THEN goods_id ELSE NULL END
    ) STORED,
    UNIQUE KEY uk_orders_no (order_no),
    UNIQUE KEY uk_orders_active_goods (active_goods_id),
    KEY idx_orders_buyer (buyer_id, status, id),
    KEY idx_orders_seller (seller_id, status, id),
    KEY idx_orders_expire (status, expire_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单';

CREATE TABLE order_events (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id    BIGINT NOT NULL,
    from_status VARCHAR(16) NULL,
    to_status   VARCHAR(16) NOT NULL,
    actor_type  VARCHAR(16) NOT NULL,
    actor_id    BIGINT NULL,
    created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_order_events (order_id, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单状态流水';

CREATE TABLE payments (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_no        VARCHAR(32) NOT NULL,
    order_id          BIGINT NOT NULL,
    order_no          VARCHAR(32) NOT NULL,
    buyer_id          BIGINT NOT NULL,
    amount            INT NOT NULL,
    channel           VARCHAR(16) NOT NULL,
    status            VARCHAR(16) NOT NULL,
    gateway_trade_no  VARCHAR(64) NULL,
    gateway_result    VARCHAR(16) NULL,
    notify_attempts   INT NOT NULL DEFAULT 0,
    expire_at         DATETIME(3) NOT NULL,
    created_at        DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at        DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_payments_no (payment_no),
    KEY idx_payments_order (order_id, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付单';

CREATE TABLE disputes (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id     BIGINT NOT NULL,
    opener_id    BIGINT NOT NULL,
    reason       VARCHAR(32) NOT NULL,
    description  VARCHAR(500) NOT NULL,
    status       VARCHAR(16) NOT NULL,
    result       VARCHAR(16) NULL,
    remark       VARCHAR(300) NULL,
    created_at   DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    resolved_at  DATETIME(3) NULL,
    KEY idx_disputes_order (order_id, id),
    KEY idx_disputes_status (status, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='申诉';

CREATE TABLE dispute_files (
    dispute_id  BIGINT NOT NULL,
    file_id     VARCHAR(64) NOT NULL,
    sort        INT NOT NULL,
    PRIMARY KEY (dispute_id, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='申诉证据';

CREATE TABLE reviews (
    id           BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id     BIGINT NOT NULL,
    goods_id     BIGINT NOT NULL,
    reviewer_id  BIGINT NOT NULL,
    reviewee_id  BIGINT NOT NULL,
    score        TINYINT NOT NULL,
    content      VARCHAR(200) NULL,
    created_at   DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_reviews_once (order_id, reviewer_id),
    KEY idx_reviews_reviewee (reviewee_id, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价';

CREATE TABLE reports (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    reporter_id       BIGINT NOT NULL,
    target_type       VARCHAR(16) NOT NULL,
    target_id         BIGINT NOT NULL,
    reason            VARCHAR(32) NOT NULL,
    description       VARCHAR(300) NULL,
    status            VARCHAR(16) NOT NULL,
    action            VARCHAR(32) NULL,
    message_snapshot  VARCHAR(500) NULL,
    created_at        DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    handled_at        DATETIME(3) NULL,
    KEY idx_reports_target (target_type, target_id, created_at),
    KEY idx_reports_reporter (reporter_id, id),
    KEY idx_reports_status (status, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='举报';

CREATE TABLE report_files (
    report_id  BIGINT NOT NULL,
    file_id    VARCHAR(64) NOT NULL,
    sort       INT NOT NULL,
    PRIMARY KEY (report_id, sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='举报证据';
