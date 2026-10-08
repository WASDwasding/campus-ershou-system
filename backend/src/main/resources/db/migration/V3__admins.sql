CREATE TABLE admins (
    id                    BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员 ID',
    username              VARCHAR(32) NOT NULL COMMENT '登录名',
    email                 VARCHAR(128) NOT NULL COMMENT '邮箱，小写',
    password_hash         VARCHAR(72) NOT NULL COMMENT 'BCrypt 密码摘要',
    status                VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE / DISABLED',
    must_change_password  TINYINT(1) NOT NULL DEFAULT 0 COMMENT '1 时除改密外都拒绝',
    token_version         INT NOT NULL DEFAULT 1 COMMENT 'JWT ver；登出或改密时加一',
    created_at            DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at            DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    UNIQUE KEY uk_admins_username (username),
    UNIQUE KEY uk_admins_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员，与 users 分开';
