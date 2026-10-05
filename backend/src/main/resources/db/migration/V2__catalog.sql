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
