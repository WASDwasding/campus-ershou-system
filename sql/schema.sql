-- 校园二手 · 第一批表结构（注册登录、用户、分类、楼栋）
-- 本机库名 campus_ershou；服务器库名 ershou。
-- 字符集 utf8mb4 / utf8mb4_unicode_ci。不建 user_balance。
-- 手动执行：先建库，再 source schema.sql，再 source seed.sql。
-- 应用启动时 Flyway 也会执行 backend/src/main/resources/db/migration/V1__init.sql。

CREATE TABLE areas (
    id    BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '区域 ID',
    name  VARCHAR(32) NOT NULL COMMENT '区域名，如东区、西区',
    sort  INT NOT NULL DEFAULT 0 COMMENT '越小越靠前'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='校区区域';

CREATE TABLE buildings (
    id            BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '楼栋 ID',
    name          VARCHAR(32) NOT NULL COMMENT '楼栋名',
    area_id       BIGINT NOT NULL COMMENT '所属区域',
    type          VARCHAR(16) NOT NULL COMMENT 'DORM / TEACHING / CANTEEN / LIBRARY / GATE / OTHER',
    meetup_point  TINYINT(1) NOT NULL DEFAULT 0 COMMENT '1 可作为面交点',
    enabled       TINYINT(1) NOT NULL DEFAULT 1 COMMENT '0 停用，接口不返回',
    sort          INT NOT NULL DEFAULT 0 COMMENT '越小越靠前',
    CONSTRAINT fk_buildings_area FOREIGN KEY (area_id) REFERENCES areas (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='楼栋';

CREATE TABLE categories (
    id       BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '分类 ID',
    name     VARCHAR(32) NOT NULL COMMENT '分类名',
    icon     VARCHAR(64) NOT NULL DEFAULT '' COMMENT '小程序本地图标键',
    enabled  TINYINT(1) NOT NULL DEFAULT 1 COMMENT '0 停用，接口不返回',
    sort     INT NOT NULL DEFAULT 0 COMMENT '越小越靠前'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品一级分类';

CREATE TABLE users (
    id                 BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户 ID',
    student_no_hash    CHAR(64) NOT NULL COMMENT '学号 HMAC-SHA256 十六进制，不存明文',
    student_no_masked  VARCHAR(16) NOT NULL COMMENT '学号脱敏，前 4 后 2',
    username           VARCHAR(16) NOT NULL COMMENT '注册用户名，唯一',
    nickname           VARCHAR(16) NOT NULL COMMENT '展示昵称，注册时与用户名相同',
    email              VARCHAR(128) NOT NULL COMMENT '邮箱，小写，唯一',
    password_hash      VARCHAR(72) NOT NULL COMMENT 'BCrypt 密码摘要',
    avatar_file_id     VARCHAR(64) NULL COMMENT '头像 fileId，本批未接上传',
    bio                VARCHAR(60) NULL COMMENT '简介',
    dorm_building_id   BIGINT NULL COMMENT '宿舍楼，须为 DORM',
    status             VARCHAR(16) NOT NULL DEFAULT 'ACTIVE' COMMENT 'ACTIVE / BANNED',
    verify_status      VARCHAR(16) NOT NULL DEFAULT 'UNVERIFIED' COMMENT 'UNVERIFIED / PENDING / VERIFIED / REJECTED',
    ban_reason         VARCHAR(200) NULL COMMENT '封禁原因',
    ban_until          DATETIME(3) NULL COMMENT '封禁截止；status=BANNED 且此项为空表示永久',
    token_version      INT NOT NULL DEFAULT 1 COMMENT 'JWT ver；登出或封禁时加一',
    created_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) COMMENT '注册时间',
    updated_at         DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3) COMMENT '更新时间',
    UNIQUE KEY uk_users_student_no_hash (student_no_hash),
    UNIQUE KEY uk_users_username (username),
    UNIQUE KEY uk_users_email (email),
    CONSTRAINT fk_users_dorm FOREIGN KEY (dorm_building_id) REFERENCES buildings (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户';

-- 第二批：上传、校园认证、商品、收藏。浏览记录在 Redis，不建表。

CREATE TABLE stored_files (
    id             VARCHAR(64) PRIMARY KEY COMMENT 'fileId',
    owner_id       BIGINT NOT NULL COMMENT '上传人',
    purpose        VARCHAR(16) NOT NULL COMMENT 'GOODS / AVATAR / IM / VERIFICATION / EVIDENCE',
    public_access  TINYINT(1) NOT NULL COMMENT '1 可直接访问 /files/public',
    created_at     DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_files_owner FOREIGN KEY (owner_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='上传文件';

CREATE TABLE campus_verifications (
    id                BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id           BIGINT NOT NULL,
    dorm_building_id  BIGINT NOT NULL,
    card_file_id      VARCHAR(64) NOT NULL,
    status            VARCHAR(16) NOT NULL COMMENT 'PENDING / APPROVED / REJECTED',
    reject_reason     VARCHAR(100) NULL,
    created_at        DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    reviewed_at       DATETIME(3) NULL,
    CONSTRAINT fk_ver_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_ver_building FOREIGN KEY (dorm_building_id) REFERENCES buildings (id),
    CONSTRAINT fk_ver_file FOREIGN KEY (card_file_id) REFERENCES stored_files (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='校园认证申请';

CREATE TABLE goods (
    id                  BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id           BIGINT NOT NULL,
    title               VARCHAR(30) NOT NULL,
    description         VARCHAR(500) NULL,
    category_id         BIGINT NOT NULL,
    condition_code      VARCHAR(16) NOT NULL,
    price               INT NOT NULL COMMENT '标价，单位分',
    original_price      INT NULL COMMENT '原价，单位分',
    meetup_building_id  BIGINT NOT NULL,
    trade_note          VARCHAR(100) NULL,
    status              VARCHAR(16) NOT NULL,
    stock               INT NOT NULL DEFAULT 1,
    version             INT NOT NULL DEFAULT 0,
    view_count          INT NOT NULL DEFAULT 0,
    favorite_count      INT NOT NULL DEFAULT 0,
    removed_reason      VARCHAR(200) NULL,
    created_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    updated_at          DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
    KEY idx_goods_status_id (status, id),
    KEY idx_goods_seller (seller_id, status),
    CONSTRAINT fk_goods_seller FOREIGN KEY (seller_id) REFERENCES users (id),
    CONSTRAINT fk_goods_category FOREIGN KEY (category_id) REFERENCES categories (id),
    CONSTRAINT fk_goods_meetup FOREIGN KEY (meetup_building_id) REFERENCES buildings (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品';

CREATE TABLE goods_images (
    goods_id  BIGINT NOT NULL,
    file_id   VARCHAR(64) NOT NULL,
    sort      INT NOT NULL,
    PRIMARY KEY (goods_id, sort),
    UNIQUE KEY uk_goods_image_file (goods_id, file_id),
    CONSTRAINT fk_gi_goods FOREIGN KEY (goods_id) REFERENCES goods (id),
    CONSTRAINT fk_gi_file FOREIGN KEY (file_id) REFERENCES stored_files (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品图片';

CREATE TABLE favorites (
    user_id     BIGINT NOT NULL,
    goods_id    BIGINT NOT NULL,
    created_at  DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    PRIMARY KEY (user_id, goods_id),
    CONSTRAINT fk_fav_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_fav_goods FOREIGN KEY (goods_id) REFERENCES goods (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏';

-- 管理员与 users 分开。登录走 /api/admin/auth/login，不使用学号。

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='管理员';
