#!/usr/bin/env python3
"""生成验收用演示数据 SQL。

200 名已认证学生、500 件商品（在售不少于 300），以及第 14 周三条样例：
货不对版结案、取消后的迟到支付、待面交订单聊天。

学号摘要必须用服务器 application-local.yml 里的 app.student-no.hmac-key 计算。
生成的 SQL 含这个密钥算出的摘要，不要提交。

用法（在服务器上）：

    python3 sql/seed-demo.py --hmac-key '服务器上的hmac密钥' > /tmp/seed-demo.sql
    mysql -u ... ershou < /tmp/seed-demo.sql

重复执行会先删掉 bio 为 SEED-DEMO 的用户及其商品、订单，再重新插入。
每种商品用 sql/demo-images 里对应的封面。文件名是 fileId.jpg 和
fileId_thumb.jpg，放到服务器图片目录（线上是 /opt/campus-ershou/files）。
"""

import argparse
import hashlib
import hmac
import sys

PASSWORD_HASH = "$2a$10$rRf.fXHqJVoQl1VtZRCoR.2vsX3RvqBiNaoGv3lrqUIWwOJs3Lbu2"
PASSWORD = "DemoPass-2026"
BIO = "SEED-DEMO"
USER_COUNT = 200
GOODS_COUNT = 500
ON_SALE_BULK = 317

COVERS = {
    "高等数学": "demo-math",
    "线性代数": "demo-linalg",
    "大学物理": "demo-physics",
    "台灯": "demo-lamp",
    "计算器": "demo-calc",
    "篮球": "demo-ball",
    "耳机": "demo-earphone",
    "键盘": "demo-keyboard",
    "书桌": "demo-desk",
    "收纳箱": "demo-box",
    "运动鞋": "demo-shoes",
    "书包": "demo-bag",
    "词典": "demo-dict",
    "充电宝": "demo-power",
}
# 与 categories 初始数据一致：1 教材书籍 2 数码电子 3 宿舍电器 4 生活日用 5 运动户外 6 服饰鞋包
CATEGORIES = {
    "高等数学": 1,
    "线性代数": 1,
    "大学物理": 1,
    "词典": 1,
    "计算器": 2,
    "耳机": 2,
    "键盘": 2,
    "台灯": 3,
    "充电宝": 3,
    "书桌": 4,
    "收纳箱": 4,
    "篮球": 5,
    "运动鞋": 6,
    "书包": 6,
}
TITLES = list(COVERS)
CONDITIONS = ["NEW", "LIKE_NEW", "GOOD", "FAIR", "POOR"]
NAMES = {1: "周宁", 2: "林晓", 3: "陈可"}


def q(value):
    return "'" + str(value).replace("\\", "\\\\").replace("'", "''") + "'"


def student_no(index):
    return f"{26000000 + index:08d}"


def username(index):
    return "d" + student_no(index)


def mask(student_no_text):
    return student_no_text[:4] + "****" + student_no_text[-2:]


def digest(key, student_no_text):
    mac = hmac.new(key.encode("utf-8"), student_no_text.encode("utf-8"), hashlib.sha256)
    return mac.hexdigest()


def main():
    parser = argparse.ArgumentParser(description="生成 200 用户 / 500 商品 / 三条样例的 SQL")
    parser.add_argument("--hmac-key", required=True, help="app.student-no.hmac-key，只在服务器上使用")
    args = parser.parse_args()
    if len(args.hmac_key.encode("utf-8")) < 16:
        sys.exit("hmac-key 至少 16 字节")

    out = []
    w = out.append
    w("-- 验收演示数据。不要提交这个文件。")
    w("SET NAMES utf8mb4;")
    w("START TRANSACTION;")
    w("")
    w("-- 清掉上一轮演示数据")
    for table, sql in [
        ("order_messages", "DELETE om FROM order_messages om JOIN orders o ON o.id = om.order_id JOIN users u ON u.id = o.buyer_id OR u.id = o.seller_id WHERE u.bio = {bio}"),
        ("payment_notifies", "DELETE pn FROM payment_notifies pn JOIN payments p ON p.id = pn.payment_id JOIN orders o ON o.id = p.order_id JOIN users u ON u.id = o.buyer_id OR u.id = o.seller_id WHERE u.bio = {bio}"),
        ("payments", "DELETE p FROM payments p JOIN orders o ON o.id = p.order_id JOIN users u ON u.id = o.buyer_id OR u.id = o.seller_id WHERE u.bio = {bio}"),
        ("dispute_files", "DELETE df FROM dispute_files df JOIN disputes d ON d.id = df.dispute_id JOIN orders o ON o.id = d.order_id JOIN users u ON u.id = o.buyer_id OR u.id = o.seller_id WHERE u.bio = {bio}"),
        ("disputes", "DELETE d FROM disputes d JOIN orders o ON o.id = d.order_id JOIN users u ON u.id = o.buyer_id OR u.id = o.seller_id WHERE u.bio = {bio}"),
        ("order_events", "DELETE e FROM order_events e JOIN orders o ON o.id = e.order_id JOIN users u ON u.id = o.buyer_id OR u.id = o.seller_id WHERE u.bio = {bio}"),
        ("orders", "DELETE o FROM orders o JOIN users u ON u.id = o.buyer_id OR u.id = o.seller_id WHERE u.bio = {bio}"),
        ("notifications", "DELETE n FROM notifications n JOIN users u ON u.id = n.user_id WHERE u.bio = {bio}"),
        ("goods_aliases", "DELETE a FROM goods_aliases a JOIN goods g ON g.id = a.goods_id JOIN users u ON u.id = g.seller_id WHERE u.bio = {bio}"),
        ("goods_images", "DELETE i FROM goods_images i JOIN goods g ON g.id = i.goods_id JOIN users u ON u.id = g.seller_id WHERE u.bio = {bio}"),
        ("goods", "DELETE g FROM goods g JOIN users u ON u.id = g.seller_id WHERE u.bio = {bio}"),
        ("stored_files", "DELETE f FROM stored_files f JOIN users u ON u.id = f.owner_id WHERE u.bio = {bio}"),
        ("users", "DELETE FROM users WHERE bio = {bio}"),
    ]:
        w(sql.format(bio=q(BIO)) + ";")
    w("")
    w("INSERT INTO users (student_no_hash, student_no_masked, username, nickname, email, password_hash, bio, dorm_building_id, status, verify_status, publish_banned, token_version) VALUES")
    rows = []
    for i in range(1, USER_COUNT + 1):
        no = student_no(i)
        nick = NAMES.get(i, f"同学{i:03d}")
        rows.append(
            "({hash}, {masked}, {user}, {nick}, {email}, {pw}, {bio}, {dorm}, 'ACTIVE', 'VERIFIED', 0, 1)".format(
                hash=q(digest(args.hmac_key, no)),
                masked=q(mask(no)),
                user=q(username(i)),
                nick=q(nick),
                email=q(username(i) + "@seed.local"),
                pw=q(PASSWORD_HASH),
                bio=q(BIO),
                dorm=((i - 1) % 14) + 1,
            )
        )
    w(",\n".join(rows) + ";")
    w("")
    file_rows = ",\n".join(
        f"SELECT {q(file_id)}, id, 'GOODS', 1 FROM users WHERE username = {q(username(1))}"
        for file_id in COVERS.values()
    )
    w("INSERT INTO stored_files (id, owner_id, purpose, public_access)\n" + file_rows + ";")
    w("")

    goods_rows = []
    for n in range(1, GOODS_COUNT - 2):
        status = "ON_SALE" if n <= ON_SALE_BULK else "SOLD"
        seller = ((n - 1) % USER_COUNT) + 1
        kind = TITLES[n % len(TITLES)]
        title = f"{kind}{n:03d}"
        price = 800 + (n * 37) % 9000
        goods_rows.append(
            "({seller}, {title}, {desc}, {cat}, {cond}, {price}, {orig}, {meetup}, {note}, {status}, 1, 0)".format(
                seller=f"(SELECT id FROM users WHERE username = {q(username(seller))})",
                title=q(title[:30]),
                desc=q("演示商品，用于列表和性能测量"),
                cat=CATEGORIES[kind],
                cond=q(CONDITIONS[n % len(CONDITIONS)]),
                price=price,
                orig=price + 2000,
                meetup=((n - 1) % 14) + 1,
                note=q("面交前先在订单里约定时间"),
                status=q(status),
            )
        )
    w("INSERT INTO goods (seller_id, title, description, category_id, condition_code, price, original_price, meetup_building_id, trade_note, status, stock, version) VALUES")
    w(",\n".join(goods_rows) + ";")
    w("")

    def user_id(index):
        return f"(SELECT id FROM users WHERE username = {q(username(index))})"

    w("-- 样例 1：货不对版，已结案取消，支付为模拟退回，商品回到在售")
    w("INSERT INTO goods (seller_id, title, description, category_id, condition_code, price, original_price, meetup_building_id, trade_note, status, stock, version) VALUES")
    w(f"({user_id(1)}, '高等数学第七版', '同济教材，笔记少量，无缺页', 1, 'GOOD', 1800, 4500, 1, '晚上图书馆方向的宿舍区面交', 'ON_SALE', 1, 0);")
    w("SET @g_mismatch = LAST_INSERT_ID();")
    w(f"INSERT INTO goods_aliases (goods_id, sort, alias) VALUES (@g_mismatch, 0, '高数');")
    w(f"INSERT INTO orders (order_no, buyer_id, seller_id, goods_id, status, amount, quantity, goods_title, goods_cover_file_id, goods_description, goods_category_id, goods_category_name, goods_condition, goods_price, goods_meetup_building_id, goods_meetup_building_name, goods_aliases_json, cancel_by, cancel_reason, paid_at, cancelled_at) VALUES")
    w(f"('ODEMO-MISMATCH', {user_id(2)}, {user_id(1)}, @g_mismatch, 'CANCELLED', 1800, 1, '高等数学第七版', {q(COVERS['高等数学'])}, '同济教材，笔记少量，无缺页', 1, '教材书籍', 'GOOD', 1800, 1, '学上公寓1', '[\"高数\"]', 'ADMIN', '货不对版，取消订单', NOW(3), NOW(3));")
    w("SET @o_mismatch = LAST_INSERT_ID();")
    w(f"INSERT INTO payments (payment_no, order_id, order_no, buyer_id, amount, channel, status, reason, expire_at) VALUES")
    w(f"('PDEMO-MISMATCH', @o_mismatch, 'ODEMO-MISMATCH', {user_id(2)}, 1800, 'MOCK', 'MOCK_REFUNDED', '货不对版，取消订单', DATE_ADD(NOW(3), INTERVAL 1 DAY));")
    w("SET @p_mismatch = LAST_INSERT_ID();")
    w("INSERT INTO payment_notifies (payment_id, notify_no, amount, adopted, reason) VALUES (@p_mismatch, 'NDEMO-MISMATCH', 1800, 1, NULL);")
    w(f"INSERT INTO disputes (order_id, opener_id, reason, description, status, result, remark, resolved_at) VALUES")
    w(f"(@o_mismatch, {user_id(2)}, 'GOODS_MISMATCH', '收到的书和详情里的版本不一致，缺了后面两章。', 'RESOLVED', 'CANCEL', '货不对版，取消订单', NOW(3));")
    w(f"INSERT INTO notifications (user_id, type, title, content, ref_type, ref_id) VALUES")
    w(f"({user_id(2)}, 'DISPUTE_RESOLVED', '申诉已处理', '货不对版，取消订单', 'ORDER', @o_mismatch),")
    w(f"({user_id(1)}, 'DISPUTE_RESOLVED', '申诉已处理', '货不对版，取消订单', 'ORDER', @o_mismatch),")
    w(f"({user_id(2)}, 'PAYMENT_MOCK_REFUNDED', '模拟退回', '货不对版，取消订单', 'ORDER', @o_mismatch),")
    w(f"({user_id(1)}, 'PAYMENT_MOCK_REFUNDED', '模拟退回', '货不对版，取消订单', 'ORDER', @o_mismatch);")
    w("")

    w("-- 样例 2：旧单已取消且模拟退回，新买家占用同一商品")
    w("INSERT INTO goods (seller_id, title, description, category_id, condition_code, price, meetup_building_id, trade_note, status, stock, version) VALUES")
    w(f"({user_id(1)}, '宿舍台灯样例', '迟到支付演示，旧单不能恢复', 3, 'LIKE_NEW', 3500, 2, '宿舍楼下见面', 'RESERVED', 1, 0);")
    w("SET @g_late = LAST_INSERT_ID();")
    w(f"INSERT INTO orders (order_no, buyer_id, seller_id, goods_id, status, amount, quantity, goods_title, goods_cover_file_id, goods_price, cancel_by, cancel_reason, cancelled_at) VALUES")
    w(f"('ODEMO-LATE-OLD', {user_id(2)}, {user_id(1)}, @g_late, 'CANCELLED', 3500, 1, '宿舍台灯样例', {q(COVERS['台灯'])}, 3500, 'BUYER', '买家取消', NOW(3));")
    w("SET @o_late_old = LAST_INSERT_ID();")
    w(f"INSERT INTO payments (payment_no, order_id, order_no, buyer_id, amount, channel, status, reason, expire_at) VALUES")
    w(f"('PDEMO-LATE-OLD', @o_late_old, 'ODEMO-LATE-OLD', {user_id(2)}, 3500, 'MOCK', 'MOCK_REFUNDED', '取消或释放后的迟到支付', DATE_ADD(NOW(3), INTERVAL 1 DAY));")
    w("SET @p_late_old = LAST_INSERT_ID();")
    w("INSERT INTO payment_notifies (payment_id, notify_no, amount, adopted, reason) VALUES (@p_late_old, 'NDEMO-LATE', 3500, 0, '取消或释放后的迟到支付');")
    w(f"INSERT INTO orders (order_no, buyer_id, seller_id, goods_id, status, amount, quantity, goods_title, goods_cover_file_id, goods_price, expire_at) VALUES")
    w(f"('ODEMO-LATE-NEW', {user_id(3)}, {user_id(1)}, @g_late, 'PENDING_PAY', 3500, 1, '宿舍台灯样例', {q(COVERS['台灯'])}, 3500, DATE_ADD(NOW(3), INTERVAL 2 HOUR));")
    w("SET @o_late_new = LAST_INSERT_ID();")
    w(f"INSERT INTO payments (payment_no, order_id, order_no, buyer_id, amount, channel, status, expire_at) VALUES")
    w(f"('PDEMO-LATE-NEW', @o_late_new, 'ODEMO-LATE-NEW', {user_id(3)}, 3500, 'MOCK', 'PENDING', DATE_ADD(NOW(3), INTERVAL 2 HOUR));")
    w(f"INSERT INTO notifications (user_id, type, title, content, ref_type, ref_id) VALUES")
    w(f"({user_id(2)}, 'PAYMENT_MOCK_REFUNDED', '模拟退回', '取消或释放后的迟到支付', 'ORDER', @o_late_old),")
    w(f"({user_id(1)}, 'PAYMENT_MOCK_REFUNDED', '模拟退回', '取消或释放后的迟到支付', 'ORDER', @o_late_old);")
    w("")

    w("-- 样例 3：待面交，双方已在订单里约定时间和地点")
    w("INSERT INTO goods (seller_id, title, description, category_id, condition_code, price, meetup_building_id, trade_note, status, stock, version) VALUES")
    w(f"({user_id(1)}, '篮球面交样例', '待面交聊天演示，页面不出现微信号', 5, 'FAIR', 2500, 1, '先在订单里约时间', 'MEETUP', 1, 0);")
    w("SET @g_meet = LAST_INSERT_ID();")
    w(f"INSERT INTO orders (order_no, buyer_id, seller_id, goods_id, status, amount, quantity, goods_title, goods_cover_file_id, goods_price, paid_at, expire_at) VALUES")
    w(f"('ODEMO-MEET', {user_id(2)}, {user_id(1)}, @g_meet, 'PAID', 2500, 1, '篮球面交样例', {q(COVERS['篮球'])}, 2500, NOW(3), DATE_ADD(NOW(3), INTERVAL 1 DAY));")
    w("SET @o_meet = LAST_INSERT_ID();")
    w(f"INSERT INTO payments (payment_no, order_id, order_no, buyer_id, amount, channel, status, expire_at) VALUES")
    w(f"('PDEMO-MEET', @o_meet, 'ODEMO-MEET', {user_id(2)}, 2500, 'MOCK', 'PAID', DATE_ADD(NOW(3), INTERVAL 1 DAY));")
    w(f"INSERT INTO order_messages (order_id, sender_id, type, content, client_msg_id) VALUES")
    w(f"(@o_meet, {user_id(2)}, 'TEXT', '晚上七点学上公寓1楼下可以吗', 'demo-meet-1'),")
    w(f"(@o_meet, {user_id(1)}, 'TEXT', '可以，我带球过去', 'demo-meet-2');")
    w(f"INSERT INTO notifications (user_id, type, title, content, ref_type, ref_id) VALUES")
    w(f"({user_id(1)}, 'CHAT_MESSAGE', '新消息', '你有一条新的订单消息', 'ORDER', @o_meet);")
    w("")
    cases = " ".join(
        f"WHEN g.title LIKE {q(name + '%')} THEN {q(file_id)}"
        for name, file_id in COVERS.items()
    )
    cases += " WHEN g.title LIKE " + q("宿舍台灯%") + " THEN " + q(COVERS["台灯"])
    w("INSERT INTO goods_images (goods_id, file_id, sort)")
    w("SELECT g.id, CASE " + cases + " ELSE " + q(COVERS["高等数学"]) + " END, 0")
    w("FROM goods g JOIN users u ON u.id = g.seller_id WHERE u.bio = " + q(BIO) + ";")
    w("COMMIT;")

    sys.stdout.write("\n".join(out) + "\n")
    print("演示账号密码都是 " + PASSWORD, file=sys.stderr)
    print("卖家周宁 学号 26000001  用户名 d26000001", file=sys.stderr)
    print("买家林晓 学号 26000002  用户名 d26000002", file=sys.stderr)
    print("买家陈可 学号 26000003  用户名 d26000003", file=sys.stderr)
    print("样例订单 ODEMO-MISMATCH、ODEMO-LATE-OLD、ODEMO-LATE-NEW、ODEMO-MEET", file=sys.stderr)


if __name__ == "__main__":
    main()
