ALTER TABLE users
    MODIFY student_no_hash CHAR(64) NULL COMMENT '学号 HMAC，仅微信且未认证时可空',
    MODIFY student_no_masked VARCHAR(16) NULL COMMENT '学号脱敏，仅微信且未认证时可空',
    MODIFY email VARCHAR(128) NULL COMMENT '邮箱，仅微信且未认证时可空',
    MODIFY password_hash VARCHAR(72) NULL COMMENT 'BCrypt，仅微信且未设密码时可空',
    ADD COLUMN openid VARCHAR(64) NULL COMMENT '微信 openid，任何接口都不返回' AFTER password_hash,
    ADD COLUMN publish_banned TINYINT(1) NOT NULL DEFAULT 0 COMMENT '1 禁止再发布，账号仍可登录' AFTER verify_status,
    ADD UNIQUE KEY uk_users_openid (openid);

ALTER TABLE campus_verifications
    ADD COLUMN real_name VARCHAR(20) NULL COMMENT '姓名，仅管理员详情返回' AFTER user_id,
    ADD COLUMN student_no VARCHAR(12) NULL COMMENT '完整学号，仅管理员详情返回' AFTER real_name,
    ADD COLUMN college VARCHAR(30) NULL COMMENT '学院，仅管理员详情返回' AFTER student_no;

CREATE TABLE goods_aliases (
    goods_id BIGINT NOT NULL,
    sort     INT NOT NULL,
    alias    VARCHAR(20) NOT NULL,
    PRIMARY KEY (goods_id, sort),
    CONSTRAINT fk_alias_goods FOREIGN KEY (goods_id) REFERENCES goods (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='卖家确认的同义叫法';

ALTER TABLE orders
    ADD COLUMN goods_description VARCHAR(500) NULL AFTER goods_title,
    ADD COLUMN goods_category_id BIGINT NULL AFTER goods_description,
    ADD COLUMN goods_category_name VARCHAR(32) NULL AFTER goods_category_id,
    ADD COLUMN goods_condition VARCHAR(16) NULL AFTER goods_category_name,
    ADD COLUMN goods_price INT NULL AFTER goods_condition,
    ADD COLUMN goods_trade_note VARCHAR(100) NULL AFTER goods_price,
    ADD COLUMN goods_meetup_building_id BIGINT NULL AFTER goods_trade_note,
    ADD COLUMN goods_meetup_building_name VARCHAR(32) NULL AFTER goods_meetup_building_id,
    ADD COLUMN goods_aliases_json VARCHAR(400) NULL AFTER goods_meetup_building_name,
    ADD COLUMN goods_images_json VARCHAR(800) NULL AFTER goods_aliases_json,
    ADD COLUMN buyer_last_read BIGINT NOT NULL DEFAULT 0 AFTER seller_handover_at,
    ADD COLUMN seller_last_read BIGINT NOT NULL DEFAULT 0 AFTER buyer_last_read;

ALTER TABLE payments
    ADD COLUMN reason VARCHAR(200) NULL AFTER status;

UPDATE payments
SET status = CASE status
    WHEN 'CREATED' THEN 'PENDING'
    WHEN 'SUCCESS' THEN 'PAID'
    WHEN 'FAILED' THEN 'CANCELLED'
    WHEN 'CLOSED' THEN 'CANCELLED'
    WHEN 'REFUNDED' THEN 'MOCK_REFUNDED'
    ELSE status
END;

CREATE TABLE payment_notifies (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    payment_id  BIGINT NOT NULL,
    notify_no   VARCHAR(40) NOT NULL,
    arrived_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    amount      INT NOT NULL,
    adopted     TINYINT(1) NOT NULL,
    reason      VARCHAR(200) NULL,
    UNIQUE KEY uk_payment_notify_no (notify_no),
    KEY idx_payment_notify_payment (payment_id, id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='沙箱支付通知';

CREATE TABLE order_messages (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id      BIGINT NOT NULL,
    sender_id     BIGINT NOT NULL,
    type          VARCHAR(16) NOT NULL,
    content       VARCHAR(500) NOT NULL,
    client_msg_id VARCHAR(40) NULL,
    created_at    DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_order_msg_client (order_id, sender_id, client_msg_id),
    KEY idx_order_msg (order_id, id),
    CONSTRAINT fk_order_msg_order FOREIGN KEY (order_id) REFERENCES orders (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单文字聊天';

ALTER TABLE reports
    MODIFY reason VARCHAR(32) NULL,
    ADD COLUMN handle_reason VARCHAR(100) NULL AFTER action;
