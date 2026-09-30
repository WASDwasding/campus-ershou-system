-- 第一批初始数据：区域、楼栋、商品分类。
-- 在 schema.sql 之后执行。与 Flyway V1__init.sql 中的 INSERT 保持一致。

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
