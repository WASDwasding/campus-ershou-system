ALTER TABLE campus_verifications
    ADD COLUMN reviewed_by BIGINT NULL COMMENT '审核管理员 ID' AFTER reviewed_at,
    ADD KEY idx_ver_status_id (status, id);

CREATE TABLE notifications (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT NOT NULL,
    type       VARCHAR(32) NOT NULL COMMENT '通知类型，如 VERIFICATION_APPROVED',
    title      VARCHAR(64) NOT NULL,
    content    VARCHAR(500) NOT NULL,
    ref_type   VARCHAR(16) NULL COMMENT 'ORDER / GOODS / REPORT',
    ref_id     BIGINT NULL,
    read_at    DATETIME(3) NULL COMMENT '空表示未读',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_notifications_user (user_id, id),
    KEY idx_notifications_unread (user_id, read_at),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站内通知';

CREATE TABLE audit_logs (
    id         BIGINT PRIMARY KEY AUTO_INCREMENT,
    category   VARCHAR(16) NOT NULL COMMENT 'SECURITY / BUSINESS / ADMIN',
    event      VARCHAR(64) NOT NULL,
    admin_id   BIGINT NULL,
    user_id    BIGINT NULL,
    ip         VARCHAR(64) NULL,
    detail     VARCHAR(500) NOT NULL COMMENT '资源 ID 等，不记原文',
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    KEY idx_audit_event (event, id),
    KEY idx_audit_user (user_id, id),
    KEY idx_audit_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='审计日志';
