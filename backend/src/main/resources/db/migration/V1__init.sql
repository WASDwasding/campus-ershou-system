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

INSERT INTO areas (id, name, sort) VALUES
    (1, '东区', 1),
    (2, '西区', 2);

INSERT INTO buildings (id, name, area_id, type, meetup_point, enabled, sort) VALUES
    (1,  '学上公寓1',  1, 'DORM', 1, 1, 10),
    (2,  '学上公寓2',  1, 'DORM', 1, 1, 20),
    (3,  '学上公寓3',  1, 'DORM', 1, 1, 30),
    (4,  '学上公寓4',  1, 'DORM', 1, 1, 40),
    (5,  '学上公寓5',  1, 'DORM', 1, 1, 50),
    (6,  '学上公寓6',  1, 'DORM', 1, 1, 60),
    (7,  '学上公寓7',  1, 'DORM', 1, 1, 70),
    (8,  '学上公寓8',  2, 'DORM', 1, 1, 80),
    (9,  '学上公寓9',  2, 'DORM', 1, 1, 90),
    (10, '学上公寓10', 2, 'DORM', 1, 1, 100),
    (11, '学上公寓11', 2, 'DORM', 1, 1, 110),
    (12, '学上公寓12', 2, 'DORM', 1, 1, 120),
    (13, '学上公寓13', 2, 'DORM', 1, 1, 130),
    (14, '学上公寓14', 2, 'DORM', 1, 1, 140);

INSERT INTO categories (id, name, icon, enabled, sort) VALUES
    (1, '教材书籍', 'books', 1, 10),
    (2, '数码电子', 'digital', 1, 20),
    (3, '宿舍电器', 'appliance', 1, 30),
    (4, '生活日用', 'daily', 1, 40),
    (5, '运动户外', 'sports', 1, 50),
    (6, '服饰鞋包', 'clothing', 1, 60),
    (7, '考研考证', 'exam', 1, 70),
    (8, '其他', 'other', 1, 80);
