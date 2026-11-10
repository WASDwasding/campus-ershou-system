-- 把 499 张商品展示图换上。只替换当前封面仍是 demo-cover 的商品。
-- 商品 501「卷毛大眼仔」不在这里。
SET NAMES utf8mb4;
START TRANSACTION;
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '03d24ead3a043805576e7fb68adafd85', seller_id, 'GOODS', 1 FROM goods WHERE id = 1;
UPDATE goods_images SET file_id = '03d24ead3a043805576e7fb68adafd85' WHERE goods_id = 1 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '03d24ead3a043805576e7fb68adafd85', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"03d24ead3a043805576e7fb68adafd85"') WHERE goods_id = 1 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'bfed2d86308564b8f34b83f38feb5d0f', seller_id, 'GOODS', 1 FROM goods WHERE id = 2;
UPDATE goods_images SET file_id = 'bfed2d86308564b8f34b83f38feb5d0f' WHERE goods_id = 2 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'bfed2d86308564b8f34b83f38feb5d0f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"bfed2d86308564b8f34b83f38feb5d0f"') WHERE goods_id = 2 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f2c7776e0728e9db0b84155840fa5421', seller_id, 'GOODS', 1 FROM goods WHERE id = 3;
UPDATE goods_images SET file_id = 'f2c7776e0728e9db0b84155840fa5421' WHERE goods_id = 3 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f2c7776e0728e9db0b84155840fa5421', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f2c7776e0728e9db0b84155840fa5421"') WHERE goods_id = 3 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2551870e47a2fb71f867f5dbc2a9316d', seller_id, 'GOODS', 1 FROM goods WHERE id = 4;
UPDATE goods_images SET file_id = '2551870e47a2fb71f867f5dbc2a9316d' WHERE goods_id = 4 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2551870e47a2fb71f867f5dbc2a9316d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2551870e47a2fb71f867f5dbc2a9316d"') WHERE goods_id = 4 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '34aa295799c1cb5a52d48ca4c81b30ba', seller_id, 'GOODS', 1 FROM goods WHERE id = 5;
UPDATE goods_images SET file_id = '34aa295799c1cb5a52d48ca4c81b30ba' WHERE goods_id = 5 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '34aa295799c1cb5a52d48ca4c81b30ba', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"34aa295799c1cb5a52d48ca4c81b30ba"') WHERE goods_id = 5 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4ac0c35b4d3ff6b98ccbfd8a21227b3a', seller_id, 'GOODS', 1 FROM goods WHERE id = 6;
UPDATE goods_images SET file_id = '4ac0c35b4d3ff6b98ccbfd8a21227b3a' WHERE goods_id = 6 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4ac0c35b4d3ff6b98ccbfd8a21227b3a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4ac0c35b4d3ff6b98ccbfd8a21227b3a"') WHERE goods_id = 6 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4fe1b0e6e2034ff0bc65655591d747a6', seller_id, 'GOODS', 1 FROM goods WHERE id = 7;
UPDATE goods_images SET file_id = '4fe1b0e6e2034ff0bc65655591d747a6' WHERE goods_id = 7 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4fe1b0e6e2034ff0bc65655591d747a6', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4fe1b0e6e2034ff0bc65655591d747a6"') WHERE goods_id = 7 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3d06a60caa24b7df5f2fa2e959ca85ae', seller_id, 'GOODS', 1 FROM goods WHERE id = 8;
UPDATE goods_images SET file_id = '3d06a60caa24b7df5f2fa2e959ca85ae' WHERE goods_id = 8 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3d06a60caa24b7df5f2fa2e959ca85ae', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3d06a60caa24b7df5f2fa2e959ca85ae"') WHERE goods_id = 8 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '88e4262afbb51a0df23ab00bb3eb4734', seller_id, 'GOODS', 1 FROM goods WHERE id = 9;
UPDATE goods_images SET file_id = '88e4262afbb51a0df23ab00bb3eb4734' WHERE goods_id = 9 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '88e4262afbb51a0df23ab00bb3eb4734', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"88e4262afbb51a0df23ab00bb3eb4734"') WHERE goods_id = 9 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '890095451ecdb76373373796af42159c', seller_id, 'GOODS', 1 FROM goods WHERE id = 10;
UPDATE goods_images SET file_id = '890095451ecdb76373373796af42159c' WHERE goods_id = 10 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '890095451ecdb76373373796af42159c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"890095451ecdb76373373796af42159c"') WHERE goods_id = 10 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5ac15fd0ed1627f7c7d6bf408ee83de8', seller_id, 'GOODS', 1 FROM goods WHERE id = 11;
UPDATE goods_images SET file_id = '5ac15fd0ed1627f7c7d6bf408ee83de8' WHERE goods_id = 11 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5ac15fd0ed1627f7c7d6bf408ee83de8', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5ac15fd0ed1627f7c7d6bf408ee83de8"') WHERE goods_id = 11 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '53f1216f8e16074837d2d427a7521517', seller_id, 'GOODS', 1 FROM goods WHERE id = 12;
UPDATE goods_images SET file_id = '53f1216f8e16074837d2d427a7521517' WHERE goods_id = 12 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '53f1216f8e16074837d2d427a7521517', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"53f1216f8e16074837d2d427a7521517"') WHERE goods_id = 12 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd864181a85c2dc58774c1390598d66e7', seller_id, 'GOODS', 1 FROM goods WHERE id = 13;
UPDATE goods_images SET file_id = 'd864181a85c2dc58774c1390598d66e7' WHERE goods_id = 13 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd864181a85c2dc58774c1390598d66e7', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d864181a85c2dc58774c1390598d66e7"') WHERE goods_id = 13 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b25b943ddaab55f37eb8c42d92ea3fa3', seller_id, 'GOODS', 1 FROM goods WHERE id = 14;
UPDATE goods_images SET file_id = 'b25b943ddaab55f37eb8c42d92ea3fa3' WHERE goods_id = 14 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b25b943ddaab55f37eb8c42d92ea3fa3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b25b943ddaab55f37eb8c42d92ea3fa3"') WHERE goods_id = 14 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '12a1745962ecf07558d584f347409b3d', seller_id, 'GOODS', 1 FROM goods WHERE id = 15;
UPDATE goods_images SET file_id = '12a1745962ecf07558d584f347409b3d' WHERE goods_id = 15 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '12a1745962ecf07558d584f347409b3d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"12a1745962ecf07558d584f347409b3d"') WHERE goods_id = 15 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5bb324bad8560c70913eae781f60fabf', seller_id, 'GOODS', 1 FROM goods WHERE id = 16;
UPDATE goods_images SET file_id = '5bb324bad8560c70913eae781f60fabf' WHERE goods_id = 16 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5bb324bad8560c70913eae781f60fabf', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5bb324bad8560c70913eae781f60fabf"') WHERE goods_id = 16 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '682308f4a8b297aa6405e9dc988bfbda', seller_id, 'GOODS', 1 FROM goods WHERE id = 17;
UPDATE goods_images SET file_id = '682308f4a8b297aa6405e9dc988bfbda' WHERE goods_id = 17 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '682308f4a8b297aa6405e9dc988bfbda', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"682308f4a8b297aa6405e9dc988bfbda"') WHERE goods_id = 17 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5a9c30f342c9208fe468aff3317e2492', seller_id, 'GOODS', 1 FROM goods WHERE id = 18;
UPDATE goods_images SET file_id = '5a9c30f342c9208fe468aff3317e2492' WHERE goods_id = 18 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5a9c30f342c9208fe468aff3317e2492', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5a9c30f342c9208fe468aff3317e2492"') WHERE goods_id = 18 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'cda0e96804a759e39682590463831a01', seller_id, 'GOODS', 1 FROM goods WHERE id = 19;
UPDATE goods_images SET file_id = 'cda0e96804a759e39682590463831a01' WHERE goods_id = 19 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'cda0e96804a759e39682590463831a01', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"cda0e96804a759e39682590463831a01"') WHERE goods_id = 19 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9c1b752ae5f8b6d1fae9b3efdc12c87d', seller_id, 'GOODS', 1 FROM goods WHERE id = 20;
UPDATE goods_images SET file_id = '9c1b752ae5f8b6d1fae9b3efdc12c87d' WHERE goods_id = 20 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9c1b752ae5f8b6d1fae9b3efdc12c87d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9c1b752ae5f8b6d1fae9b3efdc12c87d"') WHERE goods_id = 20 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'aae60681a54612977b6f9048e6757ffe', seller_id, 'GOODS', 1 FROM goods WHERE id = 21;
UPDATE goods_images SET file_id = 'aae60681a54612977b6f9048e6757ffe' WHERE goods_id = 21 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'aae60681a54612977b6f9048e6757ffe', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"aae60681a54612977b6f9048e6757ffe"') WHERE goods_id = 21 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6fcb2669419bd2ccf9199cfbfc82f654', seller_id, 'GOODS', 1 FROM goods WHERE id = 22;
UPDATE goods_images SET file_id = '6fcb2669419bd2ccf9199cfbfc82f654' WHERE goods_id = 22 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6fcb2669419bd2ccf9199cfbfc82f654', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6fcb2669419bd2ccf9199cfbfc82f654"') WHERE goods_id = 22 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c3495a8b6f3a2459c91003655f63e08f', seller_id, 'GOODS', 1 FROM goods WHERE id = 23;
UPDATE goods_images SET file_id = 'c3495a8b6f3a2459c91003655f63e08f' WHERE goods_id = 23 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c3495a8b6f3a2459c91003655f63e08f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c3495a8b6f3a2459c91003655f63e08f"') WHERE goods_id = 23 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1f82f7160ea713e5cdbf9f1ed231e1fb', seller_id, 'GOODS', 1 FROM goods WHERE id = 24;
UPDATE goods_images SET file_id = '1f82f7160ea713e5cdbf9f1ed231e1fb' WHERE goods_id = 24 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1f82f7160ea713e5cdbf9f1ed231e1fb', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1f82f7160ea713e5cdbf9f1ed231e1fb"') WHERE goods_id = 24 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '55ab74aa374ca49905cabf922fca2584', seller_id, 'GOODS', 1 FROM goods WHERE id = 25;
UPDATE goods_images SET file_id = '55ab74aa374ca49905cabf922fca2584' WHERE goods_id = 25 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '55ab74aa374ca49905cabf922fca2584', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"55ab74aa374ca49905cabf922fca2584"') WHERE goods_id = 25 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e90289e1d1b1812ccd6302fca26f0411', seller_id, 'GOODS', 1 FROM goods WHERE id = 26;
UPDATE goods_images SET file_id = 'e90289e1d1b1812ccd6302fca26f0411' WHERE goods_id = 26 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e90289e1d1b1812ccd6302fca26f0411', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e90289e1d1b1812ccd6302fca26f0411"') WHERE goods_id = 26 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '68f31aa9b9a67a798cfe230817e83b63', seller_id, 'GOODS', 1 FROM goods WHERE id = 27;
UPDATE goods_images SET file_id = '68f31aa9b9a67a798cfe230817e83b63' WHERE goods_id = 27 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '68f31aa9b9a67a798cfe230817e83b63', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"68f31aa9b9a67a798cfe230817e83b63"') WHERE goods_id = 27 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fdad2059e8153c681ffc7286be7f59c4', seller_id, 'GOODS', 1 FROM goods WHERE id = 28;
UPDATE goods_images SET file_id = 'fdad2059e8153c681ffc7286be7f59c4' WHERE goods_id = 28 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fdad2059e8153c681ffc7286be7f59c4', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fdad2059e8153c681ffc7286be7f59c4"') WHERE goods_id = 28 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c6a2de0d52f10c62cc4fb343d42538f9', seller_id, 'GOODS', 1 FROM goods WHERE id = 29;
UPDATE goods_images SET file_id = 'c6a2de0d52f10c62cc4fb343d42538f9' WHERE goods_id = 29 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c6a2de0d52f10c62cc4fb343d42538f9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c6a2de0d52f10c62cc4fb343d42538f9"') WHERE goods_id = 29 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1933a990733376934ecfa90cb9bdc2a5', seller_id, 'GOODS', 1 FROM goods WHERE id = 30;
UPDATE goods_images SET file_id = '1933a990733376934ecfa90cb9bdc2a5' WHERE goods_id = 30 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1933a990733376934ecfa90cb9bdc2a5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1933a990733376934ecfa90cb9bdc2a5"') WHERE goods_id = 30 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9d9742e62ba35640903dcc024ceadcb4', seller_id, 'GOODS', 1 FROM goods WHERE id = 31;
UPDATE goods_images SET file_id = '9d9742e62ba35640903dcc024ceadcb4' WHERE goods_id = 31 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9d9742e62ba35640903dcc024ceadcb4', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9d9742e62ba35640903dcc024ceadcb4"') WHERE goods_id = 31 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'cb917cc8d0adedbdd988eabea1380c6d', seller_id, 'GOODS', 1 FROM goods WHERE id = 32;
UPDATE goods_images SET file_id = 'cb917cc8d0adedbdd988eabea1380c6d' WHERE goods_id = 32 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'cb917cc8d0adedbdd988eabea1380c6d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"cb917cc8d0adedbdd988eabea1380c6d"') WHERE goods_id = 32 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0608fa5816743edfd9cbb8c0130ac140', seller_id, 'GOODS', 1 FROM goods WHERE id = 33;
UPDATE goods_images SET file_id = '0608fa5816743edfd9cbb8c0130ac140' WHERE goods_id = 33 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0608fa5816743edfd9cbb8c0130ac140', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0608fa5816743edfd9cbb8c0130ac140"') WHERE goods_id = 33 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f114dca053357bb515f746bc1d6ed9e1', seller_id, 'GOODS', 1 FROM goods WHERE id = 34;
UPDATE goods_images SET file_id = 'f114dca053357bb515f746bc1d6ed9e1' WHERE goods_id = 34 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f114dca053357bb515f746bc1d6ed9e1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f114dca053357bb515f746bc1d6ed9e1"') WHERE goods_id = 34 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4e5195aec9fba2be1b5cace1fa8db2ad', seller_id, 'GOODS', 1 FROM goods WHERE id = 35;
UPDATE goods_images SET file_id = '4e5195aec9fba2be1b5cace1fa8db2ad' WHERE goods_id = 35 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4e5195aec9fba2be1b5cace1fa8db2ad', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4e5195aec9fba2be1b5cace1fa8db2ad"') WHERE goods_id = 35 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '418e93a193cd521861fe148d20bbb944', seller_id, 'GOODS', 1 FROM goods WHERE id = 36;
UPDATE goods_images SET file_id = '418e93a193cd521861fe148d20bbb944' WHERE goods_id = 36 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '418e93a193cd521861fe148d20bbb944', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"418e93a193cd521861fe148d20bbb944"') WHERE goods_id = 36 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd1e5b2f2e013019256a518c8a6c573c6', seller_id, 'GOODS', 1 FROM goods WHERE id = 37;
UPDATE goods_images SET file_id = 'd1e5b2f2e013019256a518c8a6c573c6' WHERE goods_id = 37 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd1e5b2f2e013019256a518c8a6c573c6', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d1e5b2f2e013019256a518c8a6c573c6"') WHERE goods_id = 37 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a299217436e541cadb6f3c50ab744fb9', seller_id, 'GOODS', 1 FROM goods WHERE id = 38;
UPDATE goods_images SET file_id = 'a299217436e541cadb6f3c50ab744fb9' WHERE goods_id = 38 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a299217436e541cadb6f3c50ab744fb9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a299217436e541cadb6f3c50ab744fb9"') WHERE goods_id = 38 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ff54095ef52bc3460aec7204a71c25a5', seller_id, 'GOODS', 1 FROM goods WHERE id = 39;
UPDATE goods_images SET file_id = 'ff54095ef52bc3460aec7204a71c25a5' WHERE goods_id = 39 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ff54095ef52bc3460aec7204a71c25a5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ff54095ef52bc3460aec7204a71c25a5"') WHERE goods_id = 39 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9fce554261f4e6cad572dd906aaa7780', seller_id, 'GOODS', 1 FROM goods WHERE id = 40;
UPDATE goods_images SET file_id = '9fce554261f4e6cad572dd906aaa7780' WHERE goods_id = 40 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9fce554261f4e6cad572dd906aaa7780', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9fce554261f4e6cad572dd906aaa7780"') WHERE goods_id = 40 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '271edb9ff5687ff09a057a89ab41fa03', seller_id, 'GOODS', 1 FROM goods WHERE id = 41;
UPDATE goods_images SET file_id = '271edb9ff5687ff09a057a89ab41fa03' WHERE goods_id = 41 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '271edb9ff5687ff09a057a89ab41fa03', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"271edb9ff5687ff09a057a89ab41fa03"') WHERE goods_id = 41 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f1d7842c623a0298acf2b5040646b6f3', seller_id, 'GOODS', 1 FROM goods WHERE id = 42;
UPDATE goods_images SET file_id = 'f1d7842c623a0298acf2b5040646b6f3' WHERE goods_id = 42 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f1d7842c623a0298acf2b5040646b6f3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f1d7842c623a0298acf2b5040646b6f3"') WHERE goods_id = 42 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e993bce6878cf1c0cf6de091900feb37', seller_id, 'GOODS', 1 FROM goods WHERE id = 43;
UPDATE goods_images SET file_id = 'e993bce6878cf1c0cf6de091900feb37' WHERE goods_id = 43 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e993bce6878cf1c0cf6de091900feb37', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e993bce6878cf1c0cf6de091900feb37"') WHERE goods_id = 43 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3935cdb6185256a621f4fbd0ed3ab74b', seller_id, 'GOODS', 1 FROM goods WHERE id = 44;
UPDATE goods_images SET file_id = '3935cdb6185256a621f4fbd0ed3ab74b' WHERE goods_id = 44 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3935cdb6185256a621f4fbd0ed3ab74b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3935cdb6185256a621f4fbd0ed3ab74b"') WHERE goods_id = 44 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8210667171ae4e071405356adb42c42c', seller_id, 'GOODS', 1 FROM goods WHERE id = 45;
UPDATE goods_images SET file_id = '8210667171ae4e071405356adb42c42c' WHERE goods_id = 45 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8210667171ae4e071405356adb42c42c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8210667171ae4e071405356adb42c42c"') WHERE goods_id = 45 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5c8d757f844d3832d8fd943af7087b9c', seller_id, 'GOODS', 1 FROM goods WHERE id = 46;
UPDATE goods_images SET file_id = '5c8d757f844d3832d8fd943af7087b9c' WHERE goods_id = 46 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5c8d757f844d3832d8fd943af7087b9c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5c8d757f844d3832d8fd943af7087b9c"') WHERE goods_id = 46 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f51dd53a9aa6a246e855374b93107660', seller_id, 'GOODS', 1 FROM goods WHERE id = 47;
UPDATE goods_images SET file_id = 'f51dd53a9aa6a246e855374b93107660' WHERE goods_id = 47 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f51dd53a9aa6a246e855374b93107660', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f51dd53a9aa6a246e855374b93107660"') WHERE goods_id = 47 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '27256b7bd8b794ad3ab6a9632912156b', seller_id, 'GOODS', 1 FROM goods WHERE id = 48;
UPDATE goods_images SET file_id = '27256b7bd8b794ad3ab6a9632912156b' WHERE goods_id = 48 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '27256b7bd8b794ad3ab6a9632912156b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"27256b7bd8b794ad3ab6a9632912156b"') WHERE goods_id = 48 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ca398820b36947f88eba9fbe0ca1b4cb', seller_id, 'GOODS', 1 FROM goods WHERE id = 49;
UPDATE goods_images SET file_id = 'ca398820b36947f88eba9fbe0ca1b4cb' WHERE goods_id = 49 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ca398820b36947f88eba9fbe0ca1b4cb', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ca398820b36947f88eba9fbe0ca1b4cb"') WHERE goods_id = 49 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a9367df85f43038bf6f462ded7c445b4', seller_id, 'GOODS', 1 FROM goods WHERE id = 50;
UPDATE goods_images SET file_id = 'a9367df85f43038bf6f462ded7c445b4' WHERE goods_id = 50 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a9367df85f43038bf6f462ded7c445b4', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a9367df85f43038bf6f462ded7c445b4"') WHERE goods_id = 50 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b16563f3f4d32583e08e34fbf9cdd583', seller_id, 'GOODS', 1 FROM goods WHERE id = 51;
UPDATE goods_images SET file_id = 'b16563f3f4d32583e08e34fbf9cdd583' WHERE goods_id = 51 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b16563f3f4d32583e08e34fbf9cdd583', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b16563f3f4d32583e08e34fbf9cdd583"') WHERE goods_id = 51 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e4bb59da917c57b49d72d0a1dcc92f55', seller_id, 'GOODS', 1 FROM goods WHERE id = 52;
UPDATE goods_images SET file_id = 'e4bb59da917c57b49d72d0a1dcc92f55' WHERE goods_id = 52 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e4bb59da917c57b49d72d0a1dcc92f55', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e4bb59da917c57b49d72d0a1dcc92f55"') WHERE goods_id = 52 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '81f57fb15da9c86993c70896236038f2', seller_id, 'GOODS', 1 FROM goods WHERE id = 53;
UPDATE goods_images SET file_id = '81f57fb15da9c86993c70896236038f2' WHERE goods_id = 53 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '81f57fb15da9c86993c70896236038f2', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"81f57fb15da9c86993c70896236038f2"') WHERE goods_id = 53 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9d77e65e0aa8312ada57678b4bc791a5', seller_id, 'GOODS', 1 FROM goods WHERE id = 54;
UPDATE goods_images SET file_id = '9d77e65e0aa8312ada57678b4bc791a5' WHERE goods_id = 54 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9d77e65e0aa8312ada57678b4bc791a5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9d77e65e0aa8312ada57678b4bc791a5"') WHERE goods_id = 54 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b3eae00180686edf3740b6d024e584ab', seller_id, 'GOODS', 1 FROM goods WHERE id = 55;
UPDATE goods_images SET file_id = 'b3eae00180686edf3740b6d024e584ab' WHERE goods_id = 55 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b3eae00180686edf3740b6d024e584ab', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b3eae00180686edf3740b6d024e584ab"') WHERE goods_id = 55 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4924c9e198f22804fcc691efab6095c0', seller_id, 'GOODS', 1 FROM goods WHERE id = 56;
UPDATE goods_images SET file_id = '4924c9e198f22804fcc691efab6095c0' WHERE goods_id = 56 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4924c9e198f22804fcc691efab6095c0', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4924c9e198f22804fcc691efab6095c0"') WHERE goods_id = 56 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b64f03fa34db3b04747a64005456577a', seller_id, 'GOODS', 1 FROM goods WHERE id = 57;
UPDATE goods_images SET file_id = 'b64f03fa34db3b04747a64005456577a' WHERE goods_id = 57 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b64f03fa34db3b04747a64005456577a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b64f03fa34db3b04747a64005456577a"') WHERE goods_id = 57 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'cf68fd62d2a9e6cadff922e10289b5a0', seller_id, 'GOODS', 1 FROM goods WHERE id = 58;
UPDATE goods_images SET file_id = 'cf68fd62d2a9e6cadff922e10289b5a0' WHERE goods_id = 58 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'cf68fd62d2a9e6cadff922e10289b5a0', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"cf68fd62d2a9e6cadff922e10289b5a0"') WHERE goods_id = 58 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3ad3ba5457ad4a1e0e51dcae6ebe13be', seller_id, 'GOODS', 1 FROM goods WHERE id = 59;
UPDATE goods_images SET file_id = '3ad3ba5457ad4a1e0e51dcae6ebe13be' WHERE goods_id = 59 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3ad3ba5457ad4a1e0e51dcae6ebe13be', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3ad3ba5457ad4a1e0e51dcae6ebe13be"') WHERE goods_id = 59 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f3db92786d39f3456c3324bfc8566665', seller_id, 'GOODS', 1 FROM goods WHERE id = 60;
UPDATE goods_images SET file_id = 'f3db92786d39f3456c3324bfc8566665' WHERE goods_id = 60 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f3db92786d39f3456c3324bfc8566665', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f3db92786d39f3456c3324bfc8566665"') WHERE goods_id = 60 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b280f147fe449e0a57bc4bed75d7436b', seller_id, 'GOODS', 1 FROM goods WHERE id = 61;
UPDATE goods_images SET file_id = 'b280f147fe449e0a57bc4bed75d7436b' WHERE goods_id = 61 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b280f147fe449e0a57bc4bed75d7436b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b280f147fe449e0a57bc4bed75d7436b"') WHERE goods_id = 61 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0ed7c429e3e90a1927b31cbcd582334a', seller_id, 'GOODS', 1 FROM goods WHERE id = 62;
UPDATE goods_images SET file_id = '0ed7c429e3e90a1927b31cbcd582334a' WHERE goods_id = 62 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0ed7c429e3e90a1927b31cbcd582334a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0ed7c429e3e90a1927b31cbcd582334a"') WHERE goods_id = 62 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6cb99ce12ab05fe6784c6271e3d871ae', seller_id, 'GOODS', 1 FROM goods WHERE id = 63;
UPDATE goods_images SET file_id = '6cb99ce12ab05fe6784c6271e3d871ae' WHERE goods_id = 63 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6cb99ce12ab05fe6784c6271e3d871ae', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6cb99ce12ab05fe6784c6271e3d871ae"') WHERE goods_id = 63 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6f9a96097f1daa21e620a0d43eedcbb9', seller_id, 'GOODS', 1 FROM goods WHERE id = 64;
UPDATE goods_images SET file_id = '6f9a96097f1daa21e620a0d43eedcbb9' WHERE goods_id = 64 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6f9a96097f1daa21e620a0d43eedcbb9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6f9a96097f1daa21e620a0d43eedcbb9"') WHERE goods_id = 64 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '49a72170e02a247740992d56b73de4e8', seller_id, 'GOODS', 1 FROM goods WHERE id = 65;
UPDATE goods_images SET file_id = '49a72170e02a247740992d56b73de4e8' WHERE goods_id = 65 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '49a72170e02a247740992d56b73de4e8', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"49a72170e02a247740992d56b73de4e8"') WHERE goods_id = 65 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '57dbea4e0ebf85fc0fd006ab7a986b60', seller_id, 'GOODS', 1 FROM goods WHERE id = 66;
UPDATE goods_images SET file_id = '57dbea4e0ebf85fc0fd006ab7a986b60' WHERE goods_id = 66 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '57dbea4e0ebf85fc0fd006ab7a986b60', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"57dbea4e0ebf85fc0fd006ab7a986b60"') WHERE goods_id = 66 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b102c316329923d9025ef903159e3b41', seller_id, 'GOODS', 1 FROM goods WHERE id = 67;
UPDATE goods_images SET file_id = 'b102c316329923d9025ef903159e3b41' WHERE goods_id = 67 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b102c316329923d9025ef903159e3b41', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b102c316329923d9025ef903159e3b41"') WHERE goods_id = 67 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '65c8f44c2b0f79c0b5a63752e46cf370', seller_id, 'GOODS', 1 FROM goods WHERE id = 68;
UPDATE goods_images SET file_id = '65c8f44c2b0f79c0b5a63752e46cf370' WHERE goods_id = 68 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '65c8f44c2b0f79c0b5a63752e46cf370', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"65c8f44c2b0f79c0b5a63752e46cf370"') WHERE goods_id = 68 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a70b71421457d4604f80a18aabbf124f', seller_id, 'GOODS', 1 FROM goods WHERE id = 69;
UPDATE goods_images SET file_id = 'a70b71421457d4604f80a18aabbf124f' WHERE goods_id = 69 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a70b71421457d4604f80a18aabbf124f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a70b71421457d4604f80a18aabbf124f"') WHERE goods_id = 69 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1cda911a383637988734b72a8ad77969', seller_id, 'GOODS', 1 FROM goods WHERE id = 70;
UPDATE goods_images SET file_id = '1cda911a383637988734b72a8ad77969' WHERE goods_id = 70 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1cda911a383637988734b72a8ad77969', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1cda911a383637988734b72a8ad77969"') WHERE goods_id = 70 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a555378afef8c897c400a91653321232', seller_id, 'GOODS', 1 FROM goods WHERE id = 71;
UPDATE goods_images SET file_id = 'a555378afef8c897c400a91653321232' WHERE goods_id = 71 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a555378afef8c897c400a91653321232', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a555378afef8c897c400a91653321232"') WHERE goods_id = 71 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9349afc655efd00450a42bbf8541aa81', seller_id, 'GOODS', 1 FROM goods WHERE id = 72;
UPDATE goods_images SET file_id = '9349afc655efd00450a42bbf8541aa81' WHERE goods_id = 72 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9349afc655efd00450a42bbf8541aa81', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9349afc655efd00450a42bbf8541aa81"') WHERE goods_id = 72 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2ff98ed7b2a4a16cbfcbf13be71418f7', seller_id, 'GOODS', 1 FROM goods WHERE id = 73;
UPDATE goods_images SET file_id = '2ff98ed7b2a4a16cbfcbf13be71418f7' WHERE goods_id = 73 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2ff98ed7b2a4a16cbfcbf13be71418f7', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2ff98ed7b2a4a16cbfcbf13be71418f7"') WHERE goods_id = 73 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1728009844e48bbfffa364362815e02a', seller_id, 'GOODS', 1 FROM goods WHERE id = 74;
UPDATE goods_images SET file_id = '1728009844e48bbfffa364362815e02a' WHERE goods_id = 74 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1728009844e48bbfffa364362815e02a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1728009844e48bbfffa364362815e02a"') WHERE goods_id = 74 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '283b524f8ef5a360ccac09829b13a343', seller_id, 'GOODS', 1 FROM goods WHERE id = 75;
UPDATE goods_images SET file_id = '283b524f8ef5a360ccac09829b13a343' WHERE goods_id = 75 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '283b524f8ef5a360ccac09829b13a343', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"283b524f8ef5a360ccac09829b13a343"') WHERE goods_id = 75 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6adcd37da0483cdd8c602b3bfa59a602', seller_id, 'GOODS', 1 FROM goods WHERE id = 76;
UPDATE goods_images SET file_id = '6adcd37da0483cdd8c602b3bfa59a602' WHERE goods_id = 76 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6adcd37da0483cdd8c602b3bfa59a602', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6adcd37da0483cdd8c602b3bfa59a602"') WHERE goods_id = 76 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2a66fc13de89166a629b3d3977c9c9b5', seller_id, 'GOODS', 1 FROM goods WHERE id = 77;
UPDATE goods_images SET file_id = '2a66fc13de89166a629b3d3977c9c9b5' WHERE goods_id = 77 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2a66fc13de89166a629b3d3977c9c9b5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2a66fc13de89166a629b3d3977c9c9b5"') WHERE goods_id = 77 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd30dac4a0b8102841f0f3e42d05454a7', seller_id, 'GOODS', 1 FROM goods WHERE id = 78;
UPDATE goods_images SET file_id = 'd30dac4a0b8102841f0f3e42d05454a7' WHERE goods_id = 78 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd30dac4a0b8102841f0f3e42d05454a7', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d30dac4a0b8102841f0f3e42d05454a7"') WHERE goods_id = 78 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0566a1ff7568ce2d9b0ef6b67b613313', seller_id, 'GOODS', 1 FROM goods WHERE id = 79;
UPDATE goods_images SET file_id = '0566a1ff7568ce2d9b0ef6b67b613313' WHERE goods_id = 79 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0566a1ff7568ce2d9b0ef6b67b613313', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0566a1ff7568ce2d9b0ef6b67b613313"') WHERE goods_id = 79 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b29abc9f649a9fce4af64815ec4607ef', seller_id, 'GOODS', 1 FROM goods WHERE id = 80;
UPDATE goods_images SET file_id = 'b29abc9f649a9fce4af64815ec4607ef' WHERE goods_id = 80 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b29abc9f649a9fce4af64815ec4607ef', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b29abc9f649a9fce4af64815ec4607ef"') WHERE goods_id = 80 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e65850461428da83e258ee056e806dbd', seller_id, 'GOODS', 1 FROM goods WHERE id = 81;
UPDATE goods_images SET file_id = 'e65850461428da83e258ee056e806dbd' WHERE goods_id = 81 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e65850461428da83e258ee056e806dbd', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e65850461428da83e258ee056e806dbd"') WHERE goods_id = 81 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2ab0e0e4cf358c50fc906a95b12b6f24', seller_id, 'GOODS', 1 FROM goods WHERE id = 82;
UPDATE goods_images SET file_id = '2ab0e0e4cf358c50fc906a95b12b6f24' WHERE goods_id = 82 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2ab0e0e4cf358c50fc906a95b12b6f24', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2ab0e0e4cf358c50fc906a95b12b6f24"') WHERE goods_id = 82 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3d5a87ff9554e5409008898fbe461ddc', seller_id, 'GOODS', 1 FROM goods WHERE id = 83;
UPDATE goods_images SET file_id = '3d5a87ff9554e5409008898fbe461ddc' WHERE goods_id = 83 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3d5a87ff9554e5409008898fbe461ddc', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3d5a87ff9554e5409008898fbe461ddc"') WHERE goods_id = 83 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5c0e14f06f2161bcac297d354bac3f78', seller_id, 'GOODS', 1 FROM goods WHERE id = 84;
UPDATE goods_images SET file_id = '5c0e14f06f2161bcac297d354bac3f78' WHERE goods_id = 84 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5c0e14f06f2161bcac297d354bac3f78', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5c0e14f06f2161bcac297d354bac3f78"') WHERE goods_id = 84 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e86a845a8cb0ad5757ed242b02afe861', seller_id, 'GOODS', 1 FROM goods WHERE id = 85;
UPDATE goods_images SET file_id = 'e86a845a8cb0ad5757ed242b02afe861' WHERE goods_id = 85 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e86a845a8cb0ad5757ed242b02afe861', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e86a845a8cb0ad5757ed242b02afe861"') WHERE goods_id = 85 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '47e44a5e73a6489ea518601bb087386c', seller_id, 'GOODS', 1 FROM goods WHERE id = 86;
UPDATE goods_images SET file_id = '47e44a5e73a6489ea518601bb087386c' WHERE goods_id = 86 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '47e44a5e73a6489ea518601bb087386c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"47e44a5e73a6489ea518601bb087386c"') WHERE goods_id = 86 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1093ed60fcb3a23e7f97f680942a7e23', seller_id, 'GOODS', 1 FROM goods WHERE id = 87;
UPDATE goods_images SET file_id = '1093ed60fcb3a23e7f97f680942a7e23' WHERE goods_id = 87 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1093ed60fcb3a23e7f97f680942a7e23', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1093ed60fcb3a23e7f97f680942a7e23"') WHERE goods_id = 87 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b96679d6cd5c63f6251d54fe82f7bafa', seller_id, 'GOODS', 1 FROM goods WHERE id = 88;
UPDATE goods_images SET file_id = 'b96679d6cd5c63f6251d54fe82f7bafa' WHERE goods_id = 88 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b96679d6cd5c63f6251d54fe82f7bafa', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b96679d6cd5c63f6251d54fe82f7bafa"') WHERE goods_id = 88 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ecba045544ceeac252bdc46599d99c09', seller_id, 'GOODS', 1 FROM goods WHERE id = 89;
UPDATE goods_images SET file_id = 'ecba045544ceeac252bdc46599d99c09' WHERE goods_id = 89 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ecba045544ceeac252bdc46599d99c09', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ecba045544ceeac252bdc46599d99c09"') WHERE goods_id = 89 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1f1f08a5ddf77094a527102804db178f', seller_id, 'GOODS', 1 FROM goods WHERE id = 90;
UPDATE goods_images SET file_id = '1f1f08a5ddf77094a527102804db178f' WHERE goods_id = 90 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1f1f08a5ddf77094a527102804db178f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1f1f08a5ddf77094a527102804db178f"') WHERE goods_id = 90 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1c996d7f1b6dc702cb885fa9ca9af7fc', seller_id, 'GOODS', 1 FROM goods WHERE id = 91;
UPDATE goods_images SET file_id = '1c996d7f1b6dc702cb885fa9ca9af7fc' WHERE goods_id = 91 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1c996d7f1b6dc702cb885fa9ca9af7fc', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1c996d7f1b6dc702cb885fa9ca9af7fc"') WHERE goods_id = 91 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'cd018b3cc5bffa7cc90adb4ab2dfaa8f', seller_id, 'GOODS', 1 FROM goods WHERE id = 92;
UPDATE goods_images SET file_id = 'cd018b3cc5bffa7cc90adb4ab2dfaa8f' WHERE goods_id = 92 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'cd018b3cc5bffa7cc90adb4ab2dfaa8f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"cd018b3cc5bffa7cc90adb4ab2dfaa8f"') WHERE goods_id = 92 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '52ff98f09ed589549e43a5168c21f2dc', seller_id, 'GOODS', 1 FROM goods WHERE id = 93;
UPDATE goods_images SET file_id = '52ff98f09ed589549e43a5168c21f2dc' WHERE goods_id = 93 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '52ff98f09ed589549e43a5168c21f2dc', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"52ff98f09ed589549e43a5168c21f2dc"') WHERE goods_id = 93 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '42c52e7619148338bec7ed81862ebeea', seller_id, 'GOODS', 1 FROM goods WHERE id = 94;
UPDATE goods_images SET file_id = '42c52e7619148338bec7ed81862ebeea' WHERE goods_id = 94 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '42c52e7619148338bec7ed81862ebeea', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"42c52e7619148338bec7ed81862ebeea"') WHERE goods_id = 94 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8d2ff05245bc465f95d8d5b9d0a3975f', seller_id, 'GOODS', 1 FROM goods WHERE id = 95;
UPDATE goods_images SET file_id = '8d2ff05245bc465f95d8d5b9d0a3975f' WHERE goods_id = 95 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8d2ff05245bc465f95d8d5b9d0a3975f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8d2ff05245bc465f95d8d5b9d0a3975f"') WHERE goods_id = 95 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3807bd6c8265f22ee2715108ba035ce7', seller_id, 'GOODS', 1 FROM goods WHERE id = 96;
UPDATE goods_images SET file_id = '3807bd6c8265f22ee2715108ba035ce7' WHERE goods_id = 96 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3807bd6c8265f22ee2715108ba035ce7', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3807bd6c8265f22ee2715108ba035ce7"') WHERE goods_id = 96 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '52f4ecf94012623add7ddd2b9be9ada6', seller_id, 'GOODS', 1 FROM goods WHERE id = 97;
UPDATE goods_images SET file_id = '52f4ecf94012623add7ddd2b9be9ada6' WHERE goods_id = 97 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '52f4ecf94012623add7ddd2b9be9ada6', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"52f4ecf94012623add7ddd2b9be9ada6"') WHERE goods_id = 97 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd4493401ecb7e616d157fa6a9dba5748', seller_id, 'GOODS', 1 FROM goods WHERE id = 98;
UPDATE goods_images SET file_id = 'd4493401ecb7e616d157fa6a9dba5748' WHERE goods_id = 98 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd4493401ecb7e616d157fa6a9dba5748', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d4493401ecb7e616d157fa6a9dba5748"') WHERE goods_id = 98 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '790621d046e6fd2bec3e261af2747284', seller_id, 'GOODS', 1 FROM goods WHERE id = 99;
UPDATE goods_images SET file_id = '790621d046e6fd2bec3e261af2747284' WHERE goods_id = 99 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '790621d046e6fd2bec3e261af2747284', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"790621d046e6fd2bec3e261af2747284"') WHERE goods_id = 99 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3f46a42d872afc0bbbab2e21fc293361', seller_id, 'GOODS', 1 FROM goods WHERE id = 100;
UPDATE goods_images SET file_id = '3f46a42d872afc0bbbab2e21fc293361' WHERE goods_id = 100 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3f46a42d872afc0bbbab2e21fc293361', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3f46a42d872afc0bbbab2e21fc293361"') WHERE goods_id = 100 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '94fca2e80d4b57748fa2213b0fd94d33', seller_id, 'GOODS', 1 FROM goods WHERE id = 101;
UPDATE goods_images SET file_id = '94fca2e80d4b57748fa2213b0fd94d33' WHERE goods_id = 101 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '94fca2e80d4b57748fa2213b0fd94d33', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"94fca2e80d4b57748fa2213b0fd94d33"') WHERE goods_id = 101 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '46b4b6ecdbfbbb6cd50fb717b0039bb3', seller_id, 'GOODS', 1 FROM goods WHERE id = 102;
UPDATE goods_images SET file_id = '46b4b6ecdbfbbb6cd50fb717b0039bb3' WHERE goods_id = 102 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '46b4b6ecdbfbbb6cd50fb717b0039bb3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"46b4b6ecdbfbbb6cd50fb717b0039bb3"') WHERE goods_id = 102 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '62c86fcb73970121515dd417f023fc45', seller_id, 'GOODS', 1 FROM goods WHERE id = 103;
UPDATE goods_images SET file_id = '62c86fcb73970121515dd417f023fc45' WHERE goods_id = 103 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '62c86fcb73970121515dd417f023fc45', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"62c86fcb73970121515dd417f023fc45"') WHERE goods_id = 103 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4fa67eec349511834c5bef8271e252a9', seller_id, 'GOODS', 1 FROM goods WHERE id = 104;
UPDATE goods_images SET file_id = '4fa67eec349511834c5bef8271e252a9' WHERE goods_id = 104 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4fa67eec349511834c5bef8271e252a9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4fa67eec349511834c5bef8271e252a9"') WHERE goods_id = 104 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd77f9b0c12358807773c9766d1f9fe50', seller_id, 'GOODS', 1 FROM goods WHERE id = 105;
UPDATE goods_images SET file_id = 'd77f9b0c12358807773c9766d1f9fe50' WHERE goods_id = 105 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd77f9b0c12358807773c9766d1f9fe50', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d77f9b0c12358807773c9766d1f9fe50"') WHERE goods_id = 105 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '50b2bcb3ae2dc03b967272fc30aaf8d2', seller_id, 'GOODS', 1 FROM goods WHERE id = 106;
UPDATE goods_images SET file_id = '50b2bcb3ae2dc03b967272fc30aaf8d2' WHERE goods_id = 106 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '50b2bcb3ae2dc03b967272fc30aaf8d2', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"50b2bcb3ae2dc03b967272fc30aaf8d2"') WHERE goods_id = 106 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3be3cf29eca5efc9560aaab91587bf8c', seller_id, 'GOODS', 1 FROM goods WHERE id = 107;
UPDATE goods_images SET file_id = '3be3cf29eca5efc9560aaab91587bf8c' WHERE goods_id = 107 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3be3cf29eca5efc9560aaab91587bf8c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3be3cf29eca5efc9560aaab91587bf8c"') WHERE goods_id = 107 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '37d3893e57f4066213dc3d03e8b7d92b', seller_id, 'GOODS', 1 FROM goods WHERE id = 108;
UPDATE goods_images SET file_id = '37d3893e57f4066213dc3d03e8b7d92b' WHERE goods_id = 108 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '37d3893e57f4066213dc3d03e8b7d92b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"37d3893e57f4066213dc3d03e8b7d92b"') WHERE goods_id = 108 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '42a1b00a447025d8f6ccc35331ae52cf', seller_id, 'GOODS', 1 FROM goods WHERE id = 109;
UPDATE goods_images SET file_id = '42a1b00a447025d8f6ccc35331ae52cf' WHERE goods_id = 109 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '42a1b00a447025d8f6ccc35331ae52cf', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"42a1b00a447025d8f6ccc35331ae52cf"') WHERE goods_id = 109 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ede3e2fe3a7566f05b5a79b8a0085b43', seller_id, 'GOODS', 1 FROM goods WHERE id = 110;
UPDATE goods_images SET file_id = 'ede3e2fe3a7566f05b5a79b8a0085b43' WHERE goods_id = 110 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ede3e2fe3a7566f05b5a79b8a0085b43', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ede3e2fe3a7566f05b5a79b8a0085b43"') WHERE goods_id = 110 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8319af4d9538d8797eae1d9f1af62414', seller_id, 'GOODS', 1 FROM goods WHERE id = 111;
UPDATE goods_images SET file_id = '8319af4d9538d8797eae1d9f1af62414' WHERE goods_id = 111 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8319af4d9538d8797eae1d9f1af62414', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8319af4d9538d8797eae1d9f1af62414"') WHERE goods_id = 111 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '26b550f36e8bc4ce56c4c9d7d57abf6f', seller_id, 'GOODS', 1 FROM goods WHERE id = 112;
UPDATE goods_images SET file_id = '26b550f36e8bc4ce56c4c9d7d57abf6f' WHERE goods_id = 112 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '26b550f36e8bc4ce56c4c9d7d57abf6f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"26b550f36e8bc4ce56c4c9d7d57abf6f"') WHERE goods_id = 112 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '90a92a7d98f90cac619ee242e37c5e65', seller_id, 'GOODS', 1 FROM goods WHERE id = 113;
UPDATE goods_images SET file_id = '90a92a7d98f90cac619ee242e37c5e65' WHERE goods_id = 113 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '90a92a7d98f90cac619ee242e37c5e65', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"90a92a7d98f90cac619ee242e37c5e65"') WHERE goods_id = 113 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9c81211174cf455c651b82f16163bb51', seller_id, 'GOODS', 1 FROM goods WHERE id = 114;
UPDATE goods_images SET file_id = '9c81211174cf455c651b82f16163bb51' WHERE goods_id = 114 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9c81211174cf455c651b82f16163bb51', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9c81211174cf455c651b82f16163bb51"') WHERE goods_id = 114 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '048fc22406a73577c8835c952bea2216', seller_id, 'GOODS', 1 FROM goods WHERE id = 115;
UPDATE goods_images SET file_id = '048fc22406a73577c8835c952bea2216' WHERE goods_id = 115 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '048fc22406a73577c8835c952bea2216', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"048fc22406a73577c8835c952bea2216"') WHERE goods_id = 115 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4ed5456c6979c0ca3fcb3419c127d702', seller_id, 'GOODS', 1 FROM goods WHERE id = 116;
UPDATE goods_images SET file_id = '4ed5456c6979c0ca3fcb3419c127d702' WHERE goods_id = 116 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4ed5456c6979c0ca3fcb3419c127d702', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4ed5456c6979c0ca3fcb3419c127d702"') WHERE goods_id = 116 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '16d1e5eb21010f034d1a4e313d81e4e7', seller_id, 'GOODS', 1 FROM goods WHERE id = 117;
UPDATE goods_images SET file_id = '16d1e5eb21010f034d1a4e313d81e4e7' WHERE goods_id = 117 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '16d1e5eb21010f034d1a4e313d81e4e7', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"16d1e5eb21010f034d1a4e313d81e4e7"') WHERE goods_id = 117 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'aadb819973c847118d07adf5648b8b78', seller_id, 'GOODS', 1 FROM goods WHERE id = 118;
UPDATE goods_images SET file_id = 'aadb819973c847118d07adf5648b8b78' WHERE goods_id = 118 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'aadb819973c847118d07adf5648b8b78', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"aadb819973c847118d07adf5648b8b78"') WHERE goods_id = 118 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a1157d0bca984c7fd2cdcc48714229a6', seller_id, 'GOODS', 1 FROM goods WHERE id = 119;
UPDATE goods_images SET file_id = 'a1157d0bca984c7fd2cdcc48714229a6' WHERE goods_id = 119 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a1157d0bca984c7fd2cdcc48714229a6', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a1157d0bca984c7fd2cdcc48714229a6"') WHERE goods_id = 119 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c37b84bf71744e033f3f70166a3599ab', seller_id, 'GOODS', 1 FROM goods WHERE id = 120;
UPDATE goods_images SET file_id = 'c37b84bf71744e033f3f70166a3599ab' WHERE goods_id = 120 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c37b84bf71744e033f3f70166a3599ab', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c37b84bf71744e033f3f70166a3599ab"') WHERE goods_id = 120 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd9ea94e1db3683c6f303ba18da60d855', seller_id, 'GOODS', 1 FROM goods WHERE id = 121;
UPDATE goods_images SET file_id = 'd9ea94e1db3683c6f303ba18da60d855' WHERE goods_id = 121 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd9ea94e1db3683c6f303ba18da60d855', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d9ea94e1db3683c6f303ba18da60d855"') WHERE goods_id = 121 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '636841d3a7e1a9c310f55dfb85384e97', seller_id, 'GOODS', 1 FROM goods WHERE id = 122;
UPDATE goods_images SET file_id = '636841d3a7e1a9c310f55dfb85384e97' WHERE goods_id = 122 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '636841d3a7e1a9c310f55dfb85384e97', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"636841d3a7e1a9c310f55dfb85384e97"') WHERE goods_id = 122 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'abfad4b271523265c7392ed8412242d3', seller_id, 'GOODS', 1 FROM goods WHERE id = 123;
UPDATE goods_images SET file_id = 'abfad4b271523265c7392ed8412242d3' WHERE goods_id = 123 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'abfad4b271523265c7392ed8412242d3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"abfad4b271523265c7392ed8412242d3"') WHERE goods_id = 123 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b8fe7236a0e37c1f83407bf227e98fae', seller_id, 'GOODS', 1 FROM goods WHERE id = 124;
UPDATE goods_images SET file_id = 'b8fe7236a0e37c1f83407bf227e98fae' WHERE goods_id = 124 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b8fe7236a0e37c1f83407bf227e98fae', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b8fe7236a0e37c1f83407bf227e98fae"') WHERE goods_id = 124 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ec4aed7905bc736855cadaf2c2b56395', seller_id, 'GOODS', 1 FROM goods WHERE id = 125;
UPDATE goods_images SET file_id = 'ec4aed7905bc736855cadaf2c2b56395' WHERE goods_id = 125 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ec4aed7905bc736855cadaf2c2b56395', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ec4aed7905bc736855cadaf2c2b56395"') WHERE goods_id = 125 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '648a998ffce072626f94f849eaff0cea', seller_id, 'GOODS', 1 FROM goods WHERE id = 126;
UPDATE goods_images SET file_id = '648a998ffce072626f94f849eaff0cea' WHERE goods_id = 126 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '648a998ffce072626f94f849eaff0cea', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"648a998ffce072626f94f849eaff0cea"') WHERE goods_id = 126 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '669fab5de385b8a09384219b35bf7e46', seller_id, 'GOODS', 1 FROM goods WHERE id = 127;
UPDATE goods_images SET file_id = '669fab5de385b8a09384219b35bf7e46' WHERE goods_id = 127 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '669fab5de385b8a09384219b35bf7e46', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"669fab5de385b8a09384219b35bf7e46"') WHERE goods_id = 127 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '315888cc03a4cf8df08748858f53dab6', seller_id, 'GOODS', 1 FROM goods WHERE id = 128;
UPDATE goods_images SET file_id = '315888cc03a4cf8df08748858f53dab6' WHERE goods_id = 128 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '315888cc03a4cf8df08748858f53dab6', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"315888cc03a4cf8df08748858f53dab6"') WHERE goods_id = 128 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9075018b42c6926be27ede0742f4835e', seller_id, 'GOODS', 1 FROM goods WHERE id = 129;
UPDATE goods_images SET file_id = '9075018b42c6926be27ede0742f4835e' WHERE goods_id = 129 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9075018b42c6926be27ede0742f4835e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9075018b42c6926be27ede0742f4835e"') WHERE goods_id = 129 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9fe22ffbe69a9d204a5b7308dae78135', seller_id, 'GOODS', 1 FROM goods WHERE id = 130;
UPDATE goods_images SET file_id = '9fe22ffbe69a9d204a5b7308dae78135' WHERE goods_id = 130 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9fe22ffbe69a9d204a5b7308dae78135', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9fe22ffbe69a9d204a5b7308dae78135"') WHERE goods_id = 130 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fa83948f22436c813a01befa172c2b34', seller_id, 'GOODS', 1 FROM goods WHERE id = 131;
UPDATE goods_images SET file_id = 'fa83948f22436c813a01befa172c2b34' WHERE goods_id = 131 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fa83948f22436c813a01befa172c2b34', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fa83948f22436c813a01befa172c2b34"') WHERE goods_id = 131 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fe491271c0d6517065369c000a116b01', seller_id, 'GOODS', 1 FROM goods WHERE id = 132;
UPDATE goods_images SET file_id = 'fe491271c0d6517065369c000a116b01' WHERE goods_id = 132 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fe491271c0d6517065369c000a116b01', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fe491271c0d6517065369c000a116b01"') WHERE goods_id = 132 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '610250c697b6f7c0dbe76ef8e4130252', seller_id, 'GOODS', 1 FROM goods WHERE id = 133;
UPDATE goods_images SET file_id = '610250c697b6f7c0dbe76ef8e4130252' WHERE goods_id = 133 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '610250c697b6f7c0dbe76ef8e4130252', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"610250c697b6f7c0dbe76ef8e4130252"') WHERE goods_id = 133 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6cce6319f9a9ceea6143c01b7d6a4090', seller_id, 'GOODS', 1 FROM goods WHERE id = 134;
UPDATE goods_images SET file_id = '6cce6319f9a9ceea6143c01b7d6a4090' WHERE goods_id = 134 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6cce6319f9a9ceea6143c01b7d6a4090', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6cce6319f9a9ceea6143c01b7d6a4090"') WHERE goods_id = 134 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'abb24cf4f5fbf43bd6c164cd2c1ac606', seller_id, 'GOODS', 1 FROM goods WHERE id = 135;
UPDATE goods_images SET file_id = 'abb24cf4f5fbf43bd6c164cd2c1ac606' WHERE goods_id = 135 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'abb24cf4f5fbf43bd6c164cd2c1ac606', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"abb24cf4f5fbf43bd6c164cd2c1ac606"') WHERE goods_id = 135 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '656d1280355af97b03623bf1aff62f64', seller_id, 'GOODS', 1 FROM goods WHERE id = 136;
UPDATE goods_images SET file_id = '656d1280355af97b03623bf1aff62f64' WHERE goods_id = 136 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '656d1280355af97b03623bf1aff62f64', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"656d1280355af97b03623bf1aff62f64"') WHERE goods_id = 136 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '943ce3e263c1882f873bdf5a7f753754', seller_id, 'GOODS', 1 FROM goods WHERE id = 137;
UPDATE goods_images SET file_id = '943ce3e263c1882f873bdf5a7f753754' WHERE goods_id = 137 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '943ce3e263c1882f873bdf5a7f753754', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"943ce3e263c1882f873bdf5a7f753754"') WHERE goods_id = 137 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '997e379a3b1e72353e695f684d856e61', seller_id, 'GOODS', 1 FROM goods WHERE id = 138;
UPDATE goods_images SET file_id = '997e379a3b1e72353e695f684d856e61' WHERE goods_id = 138 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '997e379a3b1e72353e695f684d856e61', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"997e379a3b1e72353e695f684d856e61"') WHERE goods_id = 138 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c77166354ca028727e4cbd3b3534949e', seller_id, 'GOODS', 1 FROM goods WHERE id = 139;
UPDATE goods_images SET file_id = 'c77166354ca028727e4cbd3b3534949e' WHERE goods_id = 139 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c77166354ca028727e4cbd3b3534949e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c77166354ca028727e4cbd3b3534949e"') WHERE goods_id = 139 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f7ccc7fb9c0965d559eef09fdb33d4ab', seller_id, 'GOODS', 1 FROM goods WHERE id = 140;
UPDATE goods_images SET file_id = 'f7ccc7fb9c0965d559eef09fdb33d4ab' WHERE goods_id = 140 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f7ccc7fb9c0965d559eef09fdb33d4ab', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f7ccc7fb9c0965d559eef09fdb33d4ab"') WHERE goods_id = 140 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c0bbed0657894d1c75f2aaaa84ee47ec', seller_id, 'GOODS', 1 FROM goods WHERE id = 141;
UPDATE goods_images SET file_id = 'c0bbed0657894d1c75f2aaaa84ee47ec' WHERE goods_id = 141 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c0bbed0657894d1c75f2aaaa84ee47ec', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c0bbed0657894d1c75f2aaaa84ee47ec"') WHERE goods_id = 141 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '49187a27af694dc7591899c81472aafc', seller_id, 'GOODS', 1 FROM goods WHERE id = 142;
UPDATE goods_images SET file_id = '49187a27af694dc7591899c81472aafc' WHERE goods_id = 142 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '49187a27af694dc7591899c81472aafc', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"49187a27af694dc7591899c81472aafc"') WHERE goods_id = 142 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '28a3d8931bf3423a2d9e5e527a194bea', seller_id, 'GOODS', 1 FROM goods WHERE id = 143;
UPDATE goods_images SET file_id = '28a3d8931bf3423a2d9e5e527a194bea' WHERE goods_id = 143 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '28a3d8931bf3423a2d9e5e527a194bea', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"28a3d8931bf3423a2d9e5e527a194bea"') WHERE goods_id = 143 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '676ffb2ad5be83f016957cd3c741ee89', seller_id, 'GOODS', 1 FROM goods WHERE id = 144;
UPDATE goods_images SET file_id = '676ffb2ad5be83f016957cd3c741ee89' WHERE goods_id = 144 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '676ffb2ad5be83f016957cd3c741ee89', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"676ffb2ad5be83f016957cd3c741ee89"') WHERE goods_id = 144 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b05c982078e9fd92406bb1b4880bb566', seller_id, 'GOODS', 1 FROM goods WHERE id = 145;
UPDATE goods_images SET file_id = 'b05c982078e9fd92406bb1b4880bb566' WHERE goods_id = 145 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b05c982078e9fd92406bb1b4880bb566', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b05c982078e9fd92406bb1b4880bb566"') WHERE goods_id = 145 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '436ec48894abf39a6c704974ec046b25', seller_id, 'GOODS', 1 FROM goods WHERE id = 146;
UPDATE goods_images SET file_id = '436ec48894abf39a6c704974ec046b25' WHERE goods_id = 146 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '436ec48894abf39a6c704974ec046b25', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"436ec48894abf39a6c704974ec046b25"') WHERE goods_id = 146 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ce07c70cc77e751e8a903d5a022aca17', seller_id, 'GOODS', 1 FROM goods WHERE id = 147;
UPDATE goods_images SET file_id = 'ce07c70cc77e751e8a903d5a022aca17' WHERE goods_id = 147 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ce07c70cc77e751e8a903d5a022aca17', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ce07c70cc77e751e8a903d5a022aca17"') WHERE goods_id = 147 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '602be51394978789e16abe68e03d1b5c', seller_id, 'GOODS', 1 FROM goods WHERE id = 148;
UPDATE goods_images SET file_id = '602be51394978789e16abe68e03d1b5c' WHERE goods_id = 148 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '602be51394978789e16abe68e03d1b5c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"602be51394978789e16abe68e03d1b5c"') WHERE goods_id = 148 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5b8aa46bf9eff3c78e8b8e7f519ca82b', seller_id, 'GOODS', 1 FROM goods WHERE id = 149;
UPDATE goods_images SET file_id = '5b8aa46bf9eff3c78e8b8e7f519ca82b' WHERE goods_id = 149 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5b8aa46bf9eff3c78e8b8e7f519ca82b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5b8aa46bf9eff3c78e8b8e7f519ca82b"') WHERE goods_id = 149 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f20f1d81733dd118a027b60eb6aa0a22', seller_id, 'GOODS', 1 FROM goods WHERE id = 150;
UPDATE goods_images SET file_id = 'f20f1d81733dd118a027b60eb6aa0a22' WHERE goods_id = 150 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f20f1d81733dd118a027b60eb6aa0a22', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f20f1d81733dd118a027b60eb6aa0a22"') WHERE goods_id = 150 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'aca3af7e9f5a88fe3507d23e3a997d5e', seller_id, 'GOODS', 1 FROM goods WHERE id = 151;
UPDATE goods_images SET file_id = 'aca3af7e9f5a88fe3507d23e3a997d5e' WHERE goods_id = 151 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'aca3af7e9f5a88fe3507d23e3a997d5e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"aca3af7e9f5a88fe3507d23e3a997d5e"') WHERE goods_id = 151 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c878ade2e4035b100aba0e3d5081bd76', seller_id, 'GOODS', 1 FROM goods WHERE id = 152;
UPDATE goods_images SET file_id = 'c878ade2e4035b100aba0e3d5081bd76' WHERE goods_id = 152 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c878ade2e4035b100aba0e3d5081bd76', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c878ade2e4035b100aba0e3d5081bd76"') WHERE goods_id = 152 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '84dcb192fdc7e009b150495bb6b98803', seller_id, 'GOODS', 1 FROM goods WHERE id = 153;
UPDATE goods_images SET file_id = '84dcb192fdc7e009b150495bb6b98803' WHERE goods_id = 153 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '84dcb192fdc7e009b150495bb6b98803', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"84dcb192fdc7e009b150495bb6b98803"') WHERE goods_id = 153 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1e897907be69f3210863a5aeb9fbe693', seller_id, 'GOODS', 1 FROM goods WHERE id = 154;
UPDATE goods_images SET file_id = '1e897907be69f3210863a5aeb9fbe693' WHERE goods_id = 154 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1e897907be69f3210863a5aeb9fbe693', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1e897907be69f3210863a5aeb9fbe693"') WHERE goods_id = 154 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b4e6988b28cc9e23fff2283dee4c00ad', seller_id, 'GOODS', 1 FROM goods WHERE id = 155;
UPDATE goods_images SET file_id = 'b4e6988b28cc9e23fff2283dee4c00ad' WHERE goods_id = 155 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b4e6988b28cc9e23fff2283dee4c00ad', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b4e6988b28cc9e23fff2283dee4c00ad"') WHERE goods_id = 155 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2f6c2c557ce917f548b1ce85a73a83b0', seller_id, 'GOODS', 1 FROM goods WHERE id = 156;
UPDATE goods_images SET file_id = '2f6c2c557ce917f548b1ce85a73a83b0' WHERE goods_id = 156 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2f6c2c557ce917f548b1ce85a73a83b0', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2f6c2c557ce917f548b1ce85a73a83b0"') WHERE goods_id = 156 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a2831b10092467654344f58a1aa7841b', seller_id, 'GOODS', 1 FROM goods WHERE id = 157;
UPDATE goods_images SET file_id = 'a2831b10092467654344f58a1aa7841b' WHERE goods_id = 157 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a2831b10092467654344f58a1aa7841b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a2831b10092467654344f58a1aa7841b"') WHERE goods_id = 157 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '57869acb6198fb8bb0a7c0d7048e5fa4', seller_id, 'GOODS', 1 FROM goods WHERE id = 158;
UPDATE goods_images SET file_id = '57869acb6198fb8bb0a7c0d7048e5fa4' WHERE goods_id = 158 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '57869acb6198fb8bb0a7c0d7048e5fa4', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"57869acb6198fb8bb0a7c0d7048e5fa4"') WHERE goods_id = 158 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fcb0384195670a489b519fd009c9a5ca', seller_id, 'GOODS', 1 FROM goods WHERE id = 159;
UPDATE goods_images SET file_id = 'fcb0384195670a489b519fd009c9a5ca' WHERE goods_id = 159 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fcb0384195670a489b519fd009c9a5ca', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fcb0384195670a489b519fd009c9a5ca"') WHERE goods_id = 159 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b60be4551f56a6075d7a421a1902c36d', seller_id, 'GOODS', 1 FROM goods WHERE id = 160;
UPDATE goods_images SET file_id = 'b60be4551f56a6075d7a421a1902c36d' WHERE goods_id = 160 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b60be4551f56a6075d7a421a1902c36d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b60be4551f56a6075d7a421a1902c36d"') WHERE goods_id = 160 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '7c4f45bee24b5c219f3050a626ef65cb', seller_id, 'GOODS', 1 FROM goods WHERE id = 161;
UPDATE goods_images SET file_id = '7c4f45bee24b5c219f3050a626ef65cb' WHERE goods_id = 161 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '7c4f45bee24b5c219f3050a626ef65cb', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"7c4f45bee24b5c219f3050a626ef65cb"') WHERE goods_id = 161 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'de5111a4b90562da6ee3ffed83915627', seller_id, 'GOODS', 1 FROM goods WHERE id = 162;
UPDATE goods_images SET file_id = 'de5111a4b90562da6ee3ffed83915627' WHERE goods_id = 162 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'de5111a4b90562da6ee3ffed83915627', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"de5111a4b90562da6ee3ffed83915627"') WHERE goods_id = 162 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'bc801ef2d82215dce4e4fc32c6f6e507', seller_id, 'GOODS', 1 FROM goods WHERE id = 163;
UPDATE goods_images SET file_id = 'bc801ef2d82215dce4e4fc32c6f6e507' WHERE goods_id = 163 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'bc801ef2d82215dce4e4fc32c6f6e507', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"bc801ef2d82215dce4e4fc32c6f6e507"') WHERE goods_id = 163 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2671cad3781ac3f56e4f3a267c0a3fd0', seller_id, 'GOODS', 1 FROM goods WHERE id = 164;
UPDATE goods_images SET file_id = '2671cad3781ac3f56e4f3a267c0a3fd0' WHERE goods_id = 164 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2671cad3781ac3f56e4f3a267c0a3fd0', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2671cad3781ac3f56e4f3a267c0a3fd0"') WHERE goods_id = 164 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '7b90197e96640c01867dccb6231d98cd', seller_id, 'GOODS', 1 FROM goods WHERE id = 165;
UPDATE goods_images SET file_id = '7b90197e96640c01867dccb6231d98cd' WHERE goods_id = 165 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '7b90197e96640c01867dccb6231d98cd', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"7b90197e96640c01867dccb6231d98cd"') WHERE goods_id = 165 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '36ddd6beb550b67f56756b09cb2308aa', seller_id, 'GOODS', 1 FROM goods WHERE id = 166;
UPDATE goods_images SET file_id = '36ddd6beb550b67f56756b09cb2308aa' WHERE goods_id = 166 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '36ddd6beb550b67f56756b09cb2308aa', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"36ddd6beb550b67f56756b09cb2308aa"') WHERE goods_id = 166 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '21564582e6daa9b090af43f4292f1479', seller_id, 'GOODS', 1 FROM goods WHERE id = 167;
UPDATE goods_images SET file_id = '21564582e6daa9b090af43f4292f1479' WHERE goods_id = 167 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '21564582e6daa9b090af43f4292f1479', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"21564582e6daa9b090af43f4292f1479"') WHERE goods_id = 167 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '11a40ac546fb4cd1a62e00768db7ff7c', seller_id, 'GOODS', 1 FROM goods WHERE id = 168;
UPDATE goods_images SET file_id = '11a40ac546fb4cd1a62e00768db7ff7c' WHERE goods_id = 168 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '11a40ac546fb4cd1a62e00768db7ff7c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"11a40ac546fb4cd1a62e00768db7ff7c"') WHERE goods_id = 168 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ab80058c9e1de41e6f6fcfd26d89556d', seller_id, 'GOODS', 1 FROM goods WHERE id = 169;
UPDATE goods_images SET file_id = 'ab80058c9e1de41e6f6fcfd26d89556d' WHERE goods_id = 169 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ab80058c9e1de41e6f6fcfd26d89556d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ab80058c9e1de41e6f6fcfd26d89556d"') WHERE goods_id = 169 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4c249e6b27fa95b475388f12ded3dfe8', seller_id, 'GOODS', 1 FROM goods WHERE id = 170;
UPDATE goods_images SET file_id = '4c249e6b27fa95b475388f12ded3dfe8' WHERE goods_id = 170 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4c249e6b27fa95b475388f12ded3dfe8', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4c249e6b27fa95b475388f12ded3dfe8"') WHERE goods_id = 170 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '38a4474de1f74fedbb8be318fd9fb5a1', seller_id, 'GOODS', 1 FROM goods WHERE id = 171;
UPDATE goods_images SET file_id = '38a4474de1f74fedbb8be318fd9fb5a1' WHERE goods_id = 171 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '38a4474de1f74fedbb8be318fd9fb5a1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"38a4474de1f74fedbb8be318fd9fb5a1"') WHERE goods_id = 171 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a49702ab98800780f005b7bed0c96048', seller_id, 'GOODS', 1 FROM goods WHERE id = 172;
UPDATE goods_images SET file_id = 'a49702ab98800780f005b7bed0c96048' WHERE goods_id = 172 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a49702ab98800780f005b7bed0c96048', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a49702ab98800780f005b7bed0c96048"') WHERE goods_id = 172 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2b8a6c186d39a8189aed49915aa1144f', seller_id, 'GOODS', 1 FROM goods WHERE id = 173;
UPDATE goods_images SET file_id = '2b8a6c186d39a8189aed49915aa1144f' WHERE goods_id = 173 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2b8a6c186d39a8189aed49915aa1144f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2b8a6c186d39a8189aed49915aa1144f"') WHERE goods_id = 173 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ec6898e6442b23d3a5e8d125684cc4c2', seller_id, 'GOODS', 1 FROM goods WHERE id = 174;
UPDATE goods_images SET file_id = 'ec6898e6442b23d3a5e8d125684cc4c2' WHERE goods_id = 174 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ec6898e6442b23d3a5e8d125684cc4c2', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ec6898e6442b23d3a5e8d125684cc4c2"') WHERE goods_id = 174 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2f1c2d08d9eb750160743ce93d1d33c9', seller_id, 'GOODS', 1 FROM goods WHERE id = 175;
UPDATE goods_images SET file_id = '2f1c2d08d9eb750160743ce93d1d33c9' WHERE goods_id = 175 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2f1c2d08d9eb750160743ce93d1d33c9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2f1c2d08d9eb750160743ce93d1d33c9"') WHERE goods_id = 175 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b74af861ea186654725a72c0c8e1f1dc', seller_id, 'GOODS', 1 FROM goods WHERE id = 176;
UPDATE goods_images SET file_id = 'b74af861ea186654725a72c0c8e1f1dc' WHERE goods_id = 176 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b74af861ea186654725a72c0c8e1f1dc', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b74af861ea186654725a72c0c8e1f1dc"') WHERE goods_id = 176 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '103c4ee3b81a37e89864949b97c8a777', seller_id, 'GOODS', 1 FROM goods WHERE id = 177;
UPDATE goods_images SET file_id = '103c4ee3b81a37e89864949b97c8a777' WHERE goods_id = 177 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '103c4ee3b81a37e89864949b97c8a777', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"103c4ee3b81a37e89864949b97c8a777"') WHERE goods_id = 177 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '21f773f7cbd0758a23339d4d9afb687a', seller_id, 'GOODS', 1 FROM goods WHERE id = 178;
UPDATE goods_images SET file_id = '21f773f7cbd0758a23339d4d9afb687a' WHERE goods_id = 178 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '21f773f7cbd0758a23339d4d9afb687a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"21f773f7cbd0758a23339d4d9afb687a"') WHERE goods_id = 178 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4dddbdba602b05a6005a86a92daba305', seller_id, 'GOODS', 1 FROM goods WHERE id = 179;
UPDATE goods_images SET file_id = '4dddbdba602b05a6005a86a92daba305' WHERE goods_id = 179 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4dddbdba602b05a6005a86a92daba305', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4dddbdba602b05a6005a86a92daba305"') WHERE goods_id = 179 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '319043f3a7bb2d7ced0e4256b6284f5a', seller_id, 'GOODS', 1 FROM goods WHERE id = 180;
UPDATE goods_images SET file_id = '319043f3a7bb2d7ced0e4256b6284f5a' WHERE goods_id = 180 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '319043f3a7bb2d7ced0e4256b6284f5a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"319043f3a7bb2d7ced0e4256b6284f5a"') WHERE goods_id = 180 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6f20516a21cac3cb381090997538bc99', seller_id, 'GOODS', 1 FROM goods WHERE id = 181;
UPDATE goods_images SET file_id = '6f20516a21cac3cb381090997538bc99' WHERE goods_id = 181 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6f20516a21cac3cb381090997538bc99', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6f20516a21cac3cb381090997538bc99"') WHERE goods_id = 181 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fd050f0c661ec9388d2a3325010f490f', seller_id, 'GOODS', 1 FROM goods WHERE id = 182;
UPDATE goods_images SET file_id = 'fd050f0c661ec9388d2a3325010f490f' WHERE goods_id = 182 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fd050f0c661ec9388d2a3325010f490f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fd050f0c661ec9388d2a3325010f490f"') WHERE goods_id = 182 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ed33eacc0829aec3c96b5c5c6500e957', seller_id, 'GOODS', 1 FROM goods WHERE id = 183;
UPDATE goods_images SET file_id = 'ed33eacc0829aec3c96b5c5c6500e957' WHERE goods_id = 183 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ed33eacc0829aec3c96b5c5c6500e957', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ed33eacc0829aec3c96b5c5c6500e957"') WHERE goods_id = 183 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '19ef53e94b1b96c5d18b789b91ec344e', seller_id, 'GOODS', 1 FROM goods WHERE id = 184;
UPDATE goods_images SET file_id = '19ef53e94b1b96c5d18b789b91ec344e' WHERE goods_id = 184 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '19ef53e94b1b96c5d18b789b91ec344e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"19ef53e94b1b96c5d18b789b91ec344e"') WHERE goods_id = 184 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'eba7aa8c5e2d49735009aa61fd1a6ff3', seller_id, 'GOODS', 1 FROM goods WHERE id = 185;
UPDATE goods_images SET file_id = 'eba7aa8c5e2d49735009aa61fd1a6ff3' WHERE goods_id = 185 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'eba7aa8c5e2d49735009aa61fd1a6ff3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"eba7aa8c5e2d49735009aa61fd1a6ff3"') WHERE goods_id = 185 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8b08b2bc6a2daf5be8341be92834e08e', seller_id, 'GOODS', 1 FROM goods WHERE id = 186;
UPDATE goods_images SET file_id = '8b08b2bc6a2daf5be8341be92834e08e' WHERE goods_id = 186 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8b08b2bc6a2daf5be8341be92834e08e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8b08b2bc6a2daf5be8341be92834e08e"') WHERE goods_id = 186 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b2200cfe6c542340915e8c0e5f4e1e36', seller_id, 'GOODS', 1 FROM goods WHERE id = 187;
UPDATE goods_images SET file_id = 'b2200cfe6c542340915e8c0e5f4e1e36' WHERE goods_id = 187 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b2200cfe6c542340915e8c0e5f4e1e36', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b2200cfe6c542340915e8c0e5f4e1e36"') WHERE goods_id = 187 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'cdca57a408d8d2b69eced94afbfaa5e4', seller_id, 'GOODS', 1 FROM goods WHERE id = 188;
UPDATE goods_images SET file_id = 'cdca57a408d8d2b69eced94afbfaa5e4' WHERE goods_id = 188 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'cdca57a408d8d2b69eced94afbfaa5e4', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"cdca57a408d8d2b69eced94afbfaa5e4"') WHERE goods_id = 188 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f536acc96acce465bbde674adc133e67', seller_id, 'GOODS', 1 FROM goods WHERE id = 189;
UPDATE goods_images SET file_id = 'f536acc96acce465bbde674adc133e67' WHERE goods_id = 189 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f536acc96acce465bbde674adc133e67', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f536acc96acce465bbde674adc133e67"') WHERE goods_id = 189 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '707ea4a521019bec5c18422b0f7627ae', seller_id, 'GOODS', 1 FROM goods WHERE id = 190;
UPDATE goods_images SET file_id = '707ea4a521019bec5c18422b0f7627ae' WHERE goods_id = 190 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '707ea4a521019bec5c18422b0f7627ae', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"707ea4a521019bec5c18422b0f7627ae"') WHERE goods_id = 190 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1323899b06d31219eaf69cd199f932f1', seller_id, 'GOODS', 1 FROM goods WHERE id = 191;
UPDATE goods_images SET file_id = '1323899b06d31219eaf69cd199f932f1' WHERE goods_id = 191 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1323899b06d31219eaf69cd199f932f1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1323899b06d31219eaf69cd199f932f1"') WHERE goods_id = 191 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '7f2fda1f85f76795ab93c78c9ed423dc', seller_id, 'GOODS', 1 FROM goods WHERE id = 192;
UPDATE goods_images SET file_id = '7f2fda1f85f76795ab93c78c9ed423dc' WHERE goods_id = 192 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '7f2fda1f85f76795ab93c78c9ed423dc', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"7f2fda1f85f76795ab93c78c9ed423dc"') WHERE goods_id = 192 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '50fa2f584b256e4144a5c7fc6d981f6a', seller_id, 'GOODS', 1 FROM goods WHERE id = 193;
UPDATE goods_images SET file_id = '50fa2f584b256e4144a5c7fc6d981f6a' WHERE goods_id = 193 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '50fa2f584b256e4144a5c7fc6d981f6a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"50fa2f584b256e4144a5c7fc6d981f6a"') WHERE goods_id = 193 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'def05811af7f0f01b99a9f2e5dd21531', seller_id, 'GOODS', 1 FROM goods WHERE id = 194;
UPDATE goods_images SET file_id = 'def05811af7f0f01b99a9f2e5dd21531' WHERE goods_id = 194 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'def05811af7f0f01b99a9f2e5dd21531', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"def05811af7f0f01b99a9f2e5dd21531"') WHERE goods_id = 194 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '02c9159730ff16518aa550711c327c63', seller_id, 'GOODS', 1 FROM goods WHERE id = 195;
UPDATE goods_images SET file_id = '02c9159730ff16518aa550711c327c63' WHERE goods_id = 195 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '02c9159730ff16518aa550711c327c63', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"02c9159730ff16518aa550711c327c63"') WHERE goods_id = 195 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9b17eb146bcace45ab84c493b1e300be', seller_id, 'GOODS', 1 FROM goods WHERE id = 196;
UPDATE goods_images SET file_id = '9b17eb146bcace45ab84c493b1e300be' WHERE goods_id = 196 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9b17eb146bcace45ab84c493b1e300be', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9b17eb146bcace45ab84c493b1e300be"') WHERE goods_id = 196 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9f6fc469738ad388175e422a6526e868', seller_id, 'GOODS', 1 FROM goods WHERE id = 197;
UPDATE goods_images SET file_id = '9f6fc469738ad388175e422a6526e868' WHERE goods_id = 197 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9f6fc469738ad388175e422a6526e868', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9f6fc469738ad388175e422a6526e868"') WHERE goods_id = 197 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '41ee445bbf2dcf0a8312c35a5ba1cd42', seller_id, 'GOODS', 1 FROM goods WHERE id = 198;
UPDATE goods_images SET file_id = '41ee445bbf2dcf0a8312c35a5ba1cd42' WHERE goods_id = 198 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '41ee445bbf2dcf0a8312c35a5ba1cd42', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"41ee445bbf2dcf0a8312c35a5ba1cd42"') WHERE goods_id = 198 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '17f850ac7b62861a2068129319dce52e', seller_id, 'GOODS', 1 FROM goods WHERE id = 199;
UPDATE goods_images SET file_id = '17f850ac7b62861a2068129319dce52e' WHERE goods_id = 199 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '17f850ac7b62861a2068129319dce52e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"17f850ac7b62861a2068129319dce52e"') WHERE goods_id = 199 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '7ebe34a142be7494b4b80fa380199b7f', seller_id, 'GOODS', 1 FROM goods WHERE id = 200;
UPDATE goods_images SET file_id = '7ebe34a142be7494b4b80fa380199b7f' WHERE goods_id = 200 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '7ebe34a142be7494b4b80fa380199b7f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"7ebe34a142be7494b4b80fa380199b7f"') WHERE goods_id = 200 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5870f015a7acd87548c0cbb26f1279f1', seller_id, 'GOODS', 1 FROM goods WHERE id = 201;
UPDATE goods_images SET file_id = '5870f015a7acd87548c0cbb26f1279f1' WHERE goods_id = 201 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5870f015a7acd87548c0cbb26f1279f1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5870f015a7acd87548c0cbb26f1279f1"') WHERE goods_id = 201 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '07a2de39840d5a26cb86702b5034e191', seller_id, 'GOODS', 1 FROM goods WHERE id = 202;
UPDATE goods_images SET file_id = '07a2de39840d5a26cb86702b5034e191' WHERE goods_id = 202 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '07a2de39840d5a26cb86702b5034e191', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"07a2de39840d5a26cb86702b5034e191"') WHERE goods_id = 202 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '7e332b8fcaecda0a0c9e042e5a83b297', seller_id, 'GOODS', 1 FROM goods WHERE id = 203;
UPDATE goods_images SET file_id = '7e332b8fcaecda0a0c9e042e5a83b297' WHERE goods_id = 203 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '7e332b8fcaecda0a0c9e042e5a83b297', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"7e332b8fcaecda0a0c9e042e5a83b297"') WHERE goods_id = 203 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0b7ad2edb8c18a4d75d640b882830eb0', seller_id, 'GOODS', 1 FROM goods WHERE id = 204;
UPDATE goods_images SET file_id = '0b7ad2edb8c18a4d75d640b882830eb0' WHERE goods_id = 204 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0b7ad2edb8c18a4d75d640b882830eb0', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0b7ad2edb8c18a4d75d640b882830eb0"') WHERE goods_id = 204 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '275a08ad9cbb2f011d3c5214aa8b4a4a', seller_id, 'GOODS', 1 FROM goods WHERE id = 205;
UPDATE goods_images SET file_id = '275a08ad9cbb2f011d3c5214aa8b4a4a' WHERE goods_id = 205 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '275a08ad9cbb2f011d3c5214aa8b4a4a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"275a08ad9cbb2f011d3c5214aa8b4a4a"') WHERE goods_id = 205 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1f80cf9ec1de1c19019cb1724476efc7', seller_id, 'GOODS', 1 FROM goods WHERE id = 206;
UPDATE goods_images SET file_id = '1f80cf9ec1de1c19019cb1724476efc7' WHERE goods_id = 206 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1f80cf9ec1de1c19019cb1724476efc7', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1f80cf9ec1de1c19019cb1724476efc7"') WHERE goods_id = 206 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd8fd1757f20d2c348d6d78af72d4c71c', seller_id, 'GOODS', 1 FROM goods WHERE id = 207;
UPDATE goods_images SET file_id = 'd8fd1757f20d2c348d6d78af72d4c71c' WHERE goods_id = 207 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd8fd1757f20d2c348d6d78af72d4c71c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d8fd1757f20d2c348d6d78af72d4c71c"') WHERE goods_id = 207 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '71e35b76052f8b5ba0c9520301e0a19d', seller_id, 'GOODS', 1 FROM goods WHERE id = 208;
UPDATE goods_images SET file_id = '71e35b76052f8b5ba0c9520301e0a19d' WHERE goods_id = 208 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '71e35b76052f8b5ba0c9520301e0a19d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"71e35b76052f8b5ba0c9520301e0a19d"') WHERE goods_id = 208 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '73316f2fd9763892a1d856f59bd16b39', seller_id, 'GOODS', 1 FROM goods WHERE id = 209;
UPDATE goods_images SET file_id = '73316f2fd9763892a1d856f59bd16b39' WHERE goods_id = 209 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '73316f2fd9763892a1d856f59bd16b39', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"73316f2fd9763892a1d856f59bd16b39"') WHERE goods_id = 209 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'acf63fa268f93e76e22f903b7925e640', seller_id, 'GOODS', 1 FROM goods WHERE id = 210;
UPDATE goods_images SET file_id = 'acf63fa268f93e76e22f903b7925e640' WHERE goods_id = 210 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'acf63fa268f93e76e22f903b7925e640', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"acf63fa268f93e76e22f903b7925e640"') WHERE goods_id = 210 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b558fa6203d1921006ad0554e911605a', seller_id, 'GOODS', 1 FROM goods WHERE id = 211;
UPDATE goods_images SET file_id = 'b558fa6203d1921006ad0554e911605a' WHERE goods_id = 211 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b558fa6203d1921006ad0554e911605a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b558fa6203d1921006ad0554e911605a"') WHERE goods_id = 211 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '82557758c96eef652c19d0dd83cc6756', seller_id, 'GOODS', 1 FROM goods WHERE id = 212;
UPDATE goods_images SET file_id = '82557758c96eef652c19d0dd83cc6756' WHERE goods_id = 212 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '82557758c96eef652c19d0dd83cc6756', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"82557758c96eef652c19d0dd83cc6756"') WHERE goods_id = 212 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1e5cbc5f00681929275b8bad7104915c', seller_id, 'GOODS', 1 FROM goods WHERE id = 213;
UPDATE goods_images SET file_id = '1e5cbc5f00681929275b8bad7104915c' WHERE goods_id = 213 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1e5cbc5f00681929275b8bad7104915c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1e5cbc5f00681929275b8bad7104915c"') WHERE goods_id = 213 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '98ca2f13fc7abbee98339c7eb5a00af3', seller_id, 'GOODS', 1 FROM goods WHERE id = 214;
UPDATE goods_images SET file_id = '98ca2f13fc7abbee98339c7eb5a00af3' WHERE goods_id = 214 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '98ca2f13fc7abbee98339c7eb5a00af3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"98ca2f13fc7abbee98339c7eb5a00af3"') WHERE goods_id = 214 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '83c6e691f2faad949ab79a2748f9caa3', seller_id, 'GOODS', 1 FROM goods WHERE id = 215;
UPDATE goods_images SET file_id = '83c6e691f2faad949ab79a2748f9caa3' WHERE goods_id = 215 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '83c6e691f2faad949ab79a2748f9caa3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"83c6e691f2faad949ab79a2748f9caa3"') WHERE goods_id = 215 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'cb87bfcbe472ae4499d36a998a5b616c', seller_id, 'GOODS', 1 FROM goods WHERE id = 216;
UPDATE goods_images SET file_id = 'cb87bfcbe472ae4499d36a998a5b616c' WHERE goods_id = 216 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'cb87bfcbe472ae4499d36a998a5b616c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"cb87bfcbe472ae4499d36a998a5b616c"') WHERE goods_id = 216 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0c6f5379ded0cb3347213a8697683804', seller_id, 'GOODS', 1 FROM goods WHERE id = 217;
UPDATE goods_images SET file_id = '0c6f5379ded0cb3347213a8697683804' WHERE goods_id = 217 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0c6f5379ded0cb3347213a8697683804', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0c6f5379ded0cb3347213a8697683804"') WHERE goods_id = 217 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e136b76544bef5c4668877dea6b46c93', seller_id, 'GOODS', 1 FROM goods WHERE id = 218;
UPDATE goods_images SET file_id = 'e136b76544bef5c4668877dea6b46c93' WHERE goods_id = 218 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e136b76544bef5c4668877dea6b46c93', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e136b76544bef5c4668877dea6b46c93"') WHERE goods_id = 218 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3840c6d714afe1f38cc9b7195c04264a', seller_id, 'GOODS', 1 FROM goods WHERE id = 219;
UPDATE goods_images SET file_id = '3840c6d714afe1f38cc9b7195c04264a' WHERE goods_id = 219 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3840c6d714afe1f38cc9b7195c04264a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3840c6d714afe1f38cc9b7195c04264a"') WHERE goods_id = 219 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fedce3f7aa95a0e2f8b494137ebc563e', seller_id, 'GOODS', 1 FROM goods WHERE id = 220;
UPDATE goods_images SET file_id = 'fedce3f7aa95a0e2f8b494137ebc563e' WHERE goods_id = 220 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fedce3f7aa95a0e2f8b494137ebc563e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fedce3f7aa95a0e2f8b494137ebc563e"') WHERE goods_id = 220 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4752887c4f7ade36be89c17109033720', seller_id, 'GOODS', 1 FROM goods WHERE id = 221;
UPDATE goods_images SET file_id = '4752887c4f7ade36be89c17109033720' WHERE goods_id = 221 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4752887c4f7ade36be89c17109033720', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4752887c4f7ade36be89c17109033720"') WHERE goods_id = 221 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '88f4d38a697295f0ba87722e59285c92', seller_id, 'GOODS', 1 FROM goods WHERE id = 222;
UPDATE goods_images SET file_id = '88f4d38a697295f0ba87722e59285c92' WHERE goods_id = 222 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '88f4d38a697295f0ba87722e59285c92', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"88f4d38a697295f0ba87722e59285c92"') WHERE goods_id = 222 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c16dc7a06f7e63eb4233582eb130eabb', seller_id, 'GOODS', 1 FROM goods WHERE id = 223;
UPDATE goods_images SET file_id = 'c16dc7a06f7e63eb4233582eb130eabb' WHERE goods_id = 223 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c16dc7a06f7e63eb4233582eb130eabb', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c16dc7a06f7e63eb4233582eb130eabb"') WHERE goods_id = 223 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '701aa25f52d839d86adb38c58b583ce1', seller_id, 'GOODS', 1 FROM goods WHERE id = 224;
UPDATE goods_images SET file_id = '701aa25f52d839d86adb38c58b583ce1' WHERE goods_id = 224 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '701aa25f52d839d86adb38c58b583ce1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"701aa25f52d839d86adb38c58b583ce1"') WHERE goods_id = 224 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '793613b70a3d9f51051e2f48105c3e4b', seller_id, 'GOODS', 1 FROM goods WHERE id = 225;
UPDATE goods_images SET file_id = '793613b70a3d9f51051e2f48105c3e4b' WHERE goods_id = 225 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '793613b70a3d9f51051e2f48105c3e4b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"793613b70a3d9f51051e2f48105c3e4b"') WHERE goods_id = 225 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '7eb18cb2985ab81853e933841e95be63', seller_id, 'GOODS', 1 FROM goods WHERE id = 226;
UPDATE goods_images SET file_id = '7eb18cb2985ab81853e933841e95be63' WHERE goods_id = 226 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '7eb18cb2985ab81853e933841e95be63', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"7eb18cb2985ab81853e933841e95be63"') WHERE goods_id = 226 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '74b2657448a38c39472a09e53db563f2', seller_id, 'GOODS', 1 FROM goods WHERE id = 227;
UPDATE goods_images SET file_id = '74b2657448a38c39472a09e53db563f2' WHERE goods_id = 227 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '74b2657448a38c39472a09e53db563f2', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"74b2657448a38c39472a09e53db563f2"') WHERE goods_id = 227 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '986a1eaea1ad6d7a8dc6f717a9a65048', seller_id, 'GOODS', 1 FROM goods WHERE id = 228;
UPDATE goods_images SET file_id = '986a1eaea1ad6d7a8dc6f717a9a65048' WHERE goods_id = 228 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '986a1eaea1ad6d7a8dc6f717a9a65048', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"986a1eaea1ad6d7a8dc6f717a9a65048"') WHERE goods_id = 228 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6eeba59e0532e8f6bc45067daad1602b', seller_id, 'GOODS', 1 FROM goods WHERE id = 229;
UPDATE goods_images SET file_id = '6eeba59e0532e8f6bc45067daad1602b' WHERE goods_id = 229 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6eeba59e0532e8f6bc45067daad1602b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6eeba59e0532e8f6bc45067daad1602b"') WHERE goods_id = 229 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c3774ba6b43f363d2610f701bf626cc5', seller_id, 'GOODS', 1 FROM goods WHERE id = 230;
UPDATE goods_images SET file_id = 'c3774ba6b43f363d2610f701bf626cc5' WHERE goods_id = 230 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c3774ba6b43f363d2610f701bf626cc5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c3774ba6b43f363d2610f701bf626cc5"') WHERE goods_id = 230 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '934b42556592cbcea924dd62495f1f6a', seller_id, 'GOODS', 1 FROM goods WHERE id = 231;
UPDATE goods_images SET file_id = '934b42556592cbcea924dd62495f1f6a' WHERE goods_id = 231 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '934b42556592cbcea924dd62495f1f6a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"934b42556592cbcea924dd62495f1f6a"') WHERE goods_id = 231 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8d1a9c681bbca7fbb3e3500eed77d227', seller_id, 'GOODS', 1 FROM goods WHERE id = 232;
UPDATE goods_images SET file_id = '8d1a9c681bbca7fbb3e3500eed77d227' WHERE goods_id = 232 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8d1a9c681bbca7fbb3e3500eed77d227', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8d1a9c681bbca7fbb3e3500eed77d227"') WHERE goods_id = 232 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8c65592fa3eec2133864e56c345f6a60', seller_id, 'GOODS', 1 FROM goods WHERE id = 233;
UPDATE goods_images SET file_id = '8c65592fa3eec2133864e56c345f6a60' WHERE goods_id = 233 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8c65592fa3eec2133864e56c345f6a60', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8c65592fa3eec2133864e56c345f6a60"') WHERE goods_id = 233 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e3819d85d2d41c8b2faec8c24030c97d', seller_id, 'GOODS', 1 FROM goods WHERE id = 234;
UPDATE goods_images SET file_id = 'e3819d85d2d41c8b2faec8c24030c97d' WHERE goods_id = 234 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e3819d85d2d41c8b2faec8c24030c97d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e3819d85d2d41c8b2faec8c24030c97d"') WHERE goods_id = 234 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'bf58a8feb407af69f670baafaa8ad22b', seller_id, 'GOODS', 1 FROM goods WHERE id = 235;
UPDATE goods_images SET file_id = 'bf58a8feb407af69f670baafaa8ad22b' WHERE goods_id = 235 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'bf58a8feb407af69f670baafaa8ad22b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"bf58a8feb407af69f670baafaa8ad22b"') WHERE goods_id = 235 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '187ec997518848015e02ab9b30185352', seller_id, 'GOODS', 1 FROM goods WHERE id = 236;
UPDATE goods_images SET file_id = '187ec997518848015e02ab9b30185352' WHERE goods_id = 236 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '187ec997518848015e02ab9b30185352', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"187ec997518848015e02ab9b30185352"') WHERE goods_id = 236 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '30d1a94345eae000e9fd8841d583173d', seller_id, 'GOODS', 1 FROM goods WHERE id = 237;
UPDATE goods_images SET file_id = '30d1a94345eae000e9fd8841d583173d' WHERE goods_id = 237 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '30d1a94345eae000e9fd8841d583173d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"30d1a94345eae000e9fd8841d583173d"') WHERE goods_id = 237 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '09a11ec769d46a4466ed24fa83fc6036', seller_id, 'GOODS', 1 FROM goods WHERE id = 238;
UPDATE goods_images SET file_id = '09a11ec769d46a4466ed24fa83fc6036' WHERE goods_id = 238 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '09a11ec769d46a4466ed24fa83fc6036', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"09a11ec769d46a4466ed24fa83fc6036"') WHERE goods_id = 238 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '83d634b40ff8932a5e00fad8d0a03bfb', seller_id, 'GOODS', 1 FROM goods WHERE id = 239;
UPDATE goods_images SET file_id = '83d634b40ff8932a5e00fad8d0a03bfb' WHERE goods_id = 239 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '83d634b40ff8932a5e00fad8d0a03bfb', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"83d634b40ff8932a5e00fad8d0a03bfb"') WHERE goods_id = 239 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fef5a60569c85243f8fe605c811db6fc', seller_id, 'GOODS', 1 FROM goods WHERE id = 240;
UPDATE goods_images SET file_id = 'fef5a60569c85243f8fe605c811db6fc' WHERE goods_id = 240 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fef5a60569c85243f8fe605c811db6fc', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fef5a60569c85243f8fe605c811db6fc"') WHERE goods_id = 240 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9e06ffab044a2d152f5718b6a718a72a', seller_id, 'GOODS', 1 FROM goods WHERE id = 241;
UPDATE goods_images SET file_id = '9e06ffab044a2d152f5718b6a718a72a' WHERE goods_id = 241 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9e06ffab044a2d152f5718b6a718a72a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9e06ffab044a2d152f5718b6a718a72a"') WHERE goods_id = 241 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9b63b24c6865ce931bedcc3156ca353b', seller_id, 'GOODS', 1 FROM goods WHERE id = 242;
UPDATE goods_images SET file_id = '9b63b24c6865ce931bedcc3156ca353b' WHERE goods_id = 242 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9b63b24c6865ce931bedcc3156ca353b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9b63b24c6865ce931bedcc3156ca353b"') WHERE goods_id = 242 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5df98c8de3334f30e8bcc4d0b80a9f4a', seller_id, 'GOODS', 1 FROM goods WHERE id = 243;
UPDATE goods_images SET file_id = '5df98c8de3334f30e8bcc4d0b80a9f4a' WHERE goods_id = 243 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5df98c8de3334f30e8bcc4d0b80a9f4a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5df98c8de3334f30e8bcc4d0b80a9f4a"') WHERE goods_id = 243 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b58cfac0dfc3489d09f033514f457551', seller_id, 'GOODS', 1 FROM goods WHERE id = 244;
UPDATE goods_images SET file_id = 'b58cfac0dfc3489d09f033514f457551' WHERE goods_id = 244 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b58cfac0dfc3489d09f033514f457551', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b58cfac0dfc3489d09f033514f457551"') WHERE goods_id = 244 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ef2a5cef8669aa818c2cf7dc8ab1351f', seller_id, 'GOODS', 1 FROM goods WHERE id = 245;
UPDATE goods_images SET file_id = 'ef2a5cef8669aa818c2cf7dc8ab1351f' WHERE goods_id = 245 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ef2a5cef8669aa818c2cf7dc8ab1351f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ef2a5cef8669aa818c2cf7dc8ab1351f"') WHERE goods_id = 245 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c990161960d60535e32e73e15efb6288', seller_id, 'GOODS', 1 FROM goods WHERE id = 246;
UPDATE goods_images SET file_id = 'c990161960d60535e32e73e15efb6288' WHERE goods_id = 246 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c990161960d60535e32e73e15efb6288', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c990161960d60535e32e73e15efb6288"') WHERE goods_id = 246 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd610ee8b890c4090b1d62193410ab937', seller_id, 'GOODS', 1 FROM goods WHERE id = 247;
UPDATE goods_images SET file_id = 'd610ee8b890c4090b1d62193410ab937' WHERE goods_id = 247 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd610ee8b890c4090b1d62193410ab937', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d610ee8b890c4090b1d62193410ab937"') WHERE goods_id = 247 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '046c368c69ce283e9b0f917fe820c763', seller_id, 'GOODS', 1 FROM goods WHERE id = 248;
UPDATE goods_images SET file_id = '046c368c69ce283e9b0f917fe820c763' WHERE goods_id = 248 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '046c368c69ce283e9b0f917fe820c763', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"046c368c69ce283e9b0f917fe820c763"') WHERE goods_id = 248 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b2c953b59d05ae20ac2913eabb22480a', seller_id, 'GOODS', 1 FROM goods WHERE id = 249;
UPDATE goods_images SET file_id = 'b2c953b59d05ae20ac2913eabb22480a' WHERE goods_id = 249 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b2c953b59d05ae20ac2913eabb22480a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b2c953b59d05ae20ac2913eabb22480a"') WHERE goods_id = 249 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c3e5db6568b8d7a5b1c14441b389f8f3', seller_id, 'GOODS', 1 FROM goods WHERE id = 250;
UPDATE goods_images SET file_id = 'c3e5db6568b8d7a5b1c14441b389f8f3' WHERE goods_id = 250 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c3e5db6568b8d7a5b1c14441b389f8f3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c3e5db6568b8d7a5b1c14441b389f8f3"') WHERE goods_id = 250 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '25e4a6059da2828ba66799061a2fd8de', seller_id, 'GOODS', 1 FROM goods WHERE id = 251;
UPDATE goods_images SET file_id = '25e4a6059da2828ba66799061a2fd8de' WHERE goods_id = 251 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '25e4a6059da2828ba66799061a2fd8de', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"25e4a6059da2828ba66799061a2fd8de"') WHERE goods_id = 251 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4ebc30573392c372e9382a5ac255e012', seller_id, 'GOODS', 1 FROM goods WHERE id = 252;
UPDATE goods_images SET file_id = '4ebc30573392c372e9382a5ac255e012' WHERE goods_id = 252 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4ebc30573392c372e9382a5ac255e012', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4ebc30573392c372e9382a5ac255e012"') WHERE goods_id = 252 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3930c0ab17ccafe982d5ebe2c702d90a', seller_id, 'GOODS', 1 FROM goods WHERE id = 253;
UPDATE goods_images SET file_id = '3930c0ab17ccafe982d5ebe2c702d90a' WHERE goods_id = 253 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3930c0ab17ccafe982d5ebe2c702d90a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3930c0ab17ccafe982d5ebe2c702d90a"') WHERE goods_id = 253 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '74187999c21038d9a178159a447e313b', seller_id, 'GOODS', 1 FROM goods WHERE id = 254;
UPDATE goods_images SET file_id = '74187999c21038d9a178159a447e313b' WHERE goods_id = 254 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '74187999c21038d9a178159a447e313b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"74187999c21038d9a178159a447e313b"') WHERE goods_id = 254 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e424fab03c57a752f5dff5924474de8e', seller_id, 'GOODS', 1 FROM goods WHERE id = 255;
UPDATE goods_images SET file_id = 'e424fab03c57a752f5dff5924474de8e' WHERE goods_id = 255 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e424fab03c57a752f5dff5924474de8e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e424fab03c57a752f5dff5924474de8e"') WHERE goods_id = 255 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1257f95da6e0575ffbd51cf9b50dac60', seller_id, 'GOODS', 1 FROM goods WHERE id = 256;
UPDATE goods_images SET file_id = '1257f95da6e0575ffbd51cf9b50dac60' WHERE goods_id = 256 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1257f95da6e0575ffbd51cf9b50dac60', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1257f95da6e0575ffbd51cf9b50dac60"') WHERE goods_id = 256 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '97bd1f1e336220073985875d876e7153', seller_id, 'GOODS', 1 FROM goods WHERE id = 257;
UPDATE goods_images SET file_id = '97bd1f1e336220073985875d876e7153' WHERE goods_id = 257 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '97bd1f1e336220073985875d876e7153', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"97bd1f1e336220073985875d876e7153"') WHERE goods_id = 257 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'af324bb0614f758b298990ef7f580647', seller_id, 'GOODS', 1 FROM goods WHERE id = 258;
UPDATE goods_images SET file_id = 'af324bb0614f758b298990ef7f580647' WHERE goods_id = 258 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'af324bb0614f758b298990ef7f580647', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"af324bb0614f758b298990ef7f580647"') WHERE goods_id = 258 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5733a0623b02660b80c1387195c11078', seller_id, 'GOODS', 1 FROM goods WHERE id = 259;
UPDATE goods_images SET file_id = '5733a0623b02660b80c1387195c11078' WHERE goods_id = 259 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5733a0623b02660b80c1387195c11078', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5733a0623b02660b80c1387195c11078"') WHERE goods_id = 259 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ef744e86ea05b40abb879a0fa24d2c26', seller_id, 'GOODS', 1 FROM goods WHERE id = 260;
UPDATE goods_images SET file_id = 'ef744e86ea05b40abb879a0fa24d2c26' WHERE goods_id = 260 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ef744e86ea05b40abb879a0fa24d2c26', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ef744e86ea05b40abb879a0fa24d2c26"') WHERE goods_id = 260 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '554f21c260b185a9ac0199f1e67b4a98', seller_id, 'GOODS', 1 FROM goods WHERE id = 261;
UPDATE goods_images SET file_id = '554f21c260b185a9ac0199f1e67b4a98' WHERE goods_id = 261 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '554f21c260b185a9ac0199f1e67b4a98', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"554f21c260b185a9ac0199f1e67b4a98"') WHERE goods_id = 261 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '31bf1014b874dd2283ceed77ba7a1fd3', seller_id, 'GOODS', 1 FROM goods WHERE id = 262;
UPDATE goods_images SET file_id = '31bf1014b874dd2283ceed77ba7a1fd3' WHERE goods_id = 262 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '31bf1014b874dd2283ceed77ba7a1fd3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"31bf1014b874dd2283ceed77ba7a1fd3"') WHERE goods_id = 262 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9588045d9c70bd145885f470cfe706f3', seller_id, 'GOODS', 1 FROM goods WHERE id = 263;
UPDATE goods_images SET file_id = '9588045d9c70bd145885f470cfe706f3' WHERE goods_id = 263 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9588045d9c70bd145885f470cfe706f3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9588045d9c70bd145885f470cfe706f3"') WHERE goods_id = 263 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'dfc99e06cf997ee7acae58a3323ceefa', seller_id, 'GOODS', 1 FROM goods WHERE id = 264;
UPDATE goods_images SET file_id = 'dfc99e06cf997ee7acae58a3323ceefa' WHERE goods_id = 264 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'dfc99e06cf997ee7acae58a3323ceefa', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"dfc99e06cf997ee7acae58a3323ceefa"') WHERE goods_id = 264 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '55f1bd5b7e30ba1aa23faadebc759a92', seller_id, 'GOODS', 1 FROM goods WHERE id = 265;
UPDATE goods_images SET file_id = '55f1bd5b7e30ba1aa23faadebc759a92' WHERE goods_id = 265 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '55f1bd5b7e30ba1aa23faadebc759a92', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"55f1bd5b7e30ba1aa23faadebc759a92"') WHERE goods_id = 265 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a63c95ccffb99ef71bd79a886ef4c9b3', seller_id, 'GOODS', 1 FROM goods WHERE id = 266;
UPDATE goods_images SET file_id = 'a63c95ccffb99ef71bd79a886ef4c9b3' WHERE goods_id = 266 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a63c95ccffb99ef71bd79a886ef4c9b3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a63c95ccffb99ef71bd79a886ef4c9b3"') WHERE goods_id = 266 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'be130077a796044fa92cd8ef871c196f', seller_id, 'GOODS', 1 FROM goods WHERE id = 267;
UPDATE goods_images SET file_id = 'be130077a796044fa92cd8ef871c196f' WHERE goods_id = 267 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'be130077a796044fa92cd8ef871c196f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"be130077a796044fa92cd8ef871c196f"') WHERE goods_id = 267 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '98f14ab12626d066b49c2313b7ded264', seller_id, 'GOODS', 1 FROM goods WHERE id = 268;
UPDATE goods_images SET file_id = '98f14ab12626d066b49c2313b7ded264' WHERE goods_id = 268 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '98f14ab12626d066b49c2313b7ded264', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"98f14ab12626d066b49c2313b7ded264"') WHERE goods_id = 268 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6640e5a8cac8023234088657c8e139b5', seller_id, 'GOODS', 1 FROM goods WHERE id = 269;
UPDATE goods_images SET file_id = '6640e5a8cac8023234088657c8e139b5' WHERE goods_id = 269 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6640e5a8cac8023234088657c8e139b5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6640e5a8cac8023234088657c8e139b5"') WHERE goods_id = 269 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '91841cc14ce20e60f50d1fa7085b5d96', seller_id, 'GOODS', 1 FROM goods WHERE id = 270;
UPDATE goods_images SET file_id = '91841cc14ce20e60f50d1fa7085b5d96' WHERE goods_id = 270 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '91841cc14ce20e60f50d1fa7085b5d96', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"91841cc14ce20e60f50d1fa7085b5d96"') WHERE goods_id = 270 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c67111af93991b01a14ee375327fe486', seller_id, 'GOODS', 1 FROM goods WHERE id = 271;
UPDATE goods_images SET file_id = 'c67111af93991b01a14ee375327fe486' WHERE goods_id = 271 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c67111af93991b01a14ee375327fe486', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c67111af93991b01a14ee375327fe486"') WHERE goods_id = 271 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '48a25ea594d0f5ddacf046d6485db022', seller_id, 'GOODS', 1 FROM goods WHERE id = 272;
UPDATE goods_images SET file_id = '48a25ea594d0f5ddacf046d6485db022' WHERE goods_id = 272 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '48a25ea594d0f5ddacf046d6485db022', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"48a25ea594d0f5ddacf046d6485db022"') WHERE goods_id = 272 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b3d9e02b831fc673182b3eea6bf8c199', seller_id, 'GOODS', 1 FROM goods WHERE id = 273;
UPDATE goods_images SET file_id = 'b3d9e02b831fc673182b3eea6bf8c199' WHERE goods_id = 273 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b3d9e02b831fc673182b3eea6bf8c199', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b3d9e02b831fc673182b3eea6bf8c199"') WHERE goods_id = 273 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd0ca42b7746e69e6413975ea1fc9c448', seller_id, 'GOODS', 1 FROM goods WHERE id = 274;
UPDATE goods_images SET file_id = 'd0ca42b7746e69e6413975ea1fc9c448' WHERE goods_id = 274 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd0ca42b7746e69e6413975ea1fc9c448', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d0ca42b7746e69e6413975ea1fc9c448"') WHERE goods_id = 274 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3398cf241f55c91c5bf58e38a1c28da1', seller_id, 'GOODS', 1 FROM goods WHERE id = 275;
UPDATE goods_images SET file_id = '3398cf241f55c91c5bf58e38a1c28da1' WHERE goods_id = 275 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3398cf241f55c91c5bf58e38a1c28da1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3398cf241f55c91c5bf58e38a1c28da1"') WHERE goods_id = 275 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2e6b9f76373931ff32f3ac886c201448', seller_id, 'GOODS', 1 FROM goods WHERE id = 276;
UPDATE goods_images SET file_id = '2e6b9f76373931ff32f3ac886c201448' WHERE goods_id = 276 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2e6b9f76373931ff32f3ac886c201448', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2e6b9f76373931ff32f3ac886c201448"') WHERE goods_id = 276 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'caf46f3a00991265748bd99b18f24c2e', seller_id, 'GOODS', 1 FROM goods WHERE id = 277;
UPDATE goods_images SET file_id = 'caf46f3a00991265748bd99b18f24c2e' WHERE goods_id = 277 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'caf46f3a00991265748bd99b18f24c2e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"caf46f3a00991265748bd99b18f24c2e"') WHERE goods_id = 277 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd2cfc3450a3b11fdd315f0748144d8c5', seller_id, 'GOODS', 1 FROM goods WHERE id = 278;
UPDATE goods_images SET file_id = 'd2cfc3450a3b11fdd315f0748144d8c5' WHERE goods_id = 278 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd2cfc3450a3b11fdd315f0748144d8c5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d2cfc3450a3b11fdd315f0748144d8c5"') WHERE goods_id = 278 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8387143544b67ed93a639c9897e33af4', seller_id, 'GOODS', 1 FROM goods WHERE id = 279;
UPDATE goods_images SET file_id = '8387143544b67ed93a639c9897e33af4' WHERE goods_id = 279 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8387143544b67ed93a639c9897e33af4', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8387143544b67ed93a639c9897e33af4"') WHERE goods_id = 279 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b85e08c3044b9e53ae3adc2ff927d1b1', seller_id, 'GOODS', 1 FROM goods WHERE id = 280;
UPDATE goods_images SET file_id = 'b85e08c3044b9e53ae3adc2ff927d1b1' WHERE goods_id = 280 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b85e08c3044b9e53ae3adc2ff927d1b1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b85e08c3044b9e53ae3adc2ff927d1b1"') WHERE goods_id = 280 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fef4179a1c747666735221c605f411b1', seller_id, 'GOODS', 1 FROM goods WHERE id = 281;
UPDATE goods_images SET file_id = 'fef4179a1c747666735221c605f411b1' WHERE goods_id = 281 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fef4179a1c747666735221c605f411b1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fef4179a1c747666735221c605f411b1"') WHERE goods_id = 281 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fa483da87f7911b6d210e830aaf6e0e2', seller_id, 'GOODS', 1 FROM goods WHERE id = 282;
UPDATE goods_images SET file_id = 'fa483da87f7911b6d210e830aaf6e0e2' WHERE goods_id = 282 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fa483da87f7911b6d210e830aaf6e0e2', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fa483da87f7911b6d210e830aaf6e0e2"') WHERE goods_id = 282 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '45108d6fc6162c065428d61a3c230941', seller_id, 'GOODS', 1 FROM goods WHERE id = 283;
UPDATE goods_images SET file_id = '45108d6fc6162c065428d61a3c230941' WHERE goods_id = 283 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '45108d6fc6162c065428d61a3c230941', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"45108d6fc6162c065428d61a3c230941"') WHERE goods_id = 283 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd7e167872fe97a8ffec9657955dcf92b', seller_id, 'GOODS', 1 FROM goods WHERE id = 284;
UPDATE goods_images SET file_id = 'd7e167872fe97a8ffec9657955dcf92b' WHERE goods_id = 284 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd7e167872fe97a8ffec9657955dcf92b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d7e167872fe97a8ffec9657955dcf92b"') WHERE goods_id = 284 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4d4adab5ba6e0364ccfbd957b1b7cde6', seller_id, 'GOODS', 1 FROM goods WHERE id = 285;
UPDATE goods_images SET file_id = '4d4adab5ba6e0364ccfbd957b1b7cde6' WHERE goods_id = 285 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4d4adab5ba6e0364ccfbd957b1b7cde6', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4d4adab5ba6e0364ccfbd957b1b7cde6"') WHERE goods_id = 285 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b1282e958764862b8a39b50a54d8edad', seller_id, 'GOODS', 1 FROM goods WHERE id = 286;
UPDATE goods_images SET file_id = 'b1282e958764862b8a39b50a54d8edad' WHERE goods_id = 286 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b1282e958764862b8a39b50a54d8edad', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b1282e958764862b8a39b50a54d8edad"') WHERE goods_id = 286 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4e14842a100dcd436d91f4265acc655c', seller_id, 'GOODS', 1 FROM goods WHERE id = 287;
UPDATE goods_images SET file_id = '4e14842a100dcd436d91f4265acc655c' WHERE goods_id = 287 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4e14842a100dcd436d91f4265acc655c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4e14842a100dcd436d91f4265acc655c"') WHERE goods_id = 287 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a13be1b49eed115527bd94976d3032d6', seller_id, 'GOODS', 1 FROM goods WHERE id = 288;
UPDATE goods_images SET file_id = 'a13be1b49eed115527bd94976d3032d6' WHERE goods_id = 288 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a13be1b49eed115527bd94976d3032d6', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a13be1b49eed115527bd94976d3032d6"') WHERE goods_id = 288 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'db66040b94155bf5210bbcb6dd462847', seller_id, 'GOODS', 1 FROM goods WHERE id = 289;
UPDATE goods_images SET file_id = 'db66040b94155bf5210bbcb6dd462847' WHERE goods_id = 289 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'db66040b94155bf5210bbcb6dd462847', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"db66040b94155bf5210bbcb6dd462847"') WHERE goods_id = 289 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5fa97092a5c23bdf84db06cf744ceadd', seller_id, 'GOODS', 1 FROM goods WHERE id = 290;
UPDATE goods_images SET file_id = '5fa97092a5c23bdf84db06cf744ceadd' WHERE goods_id = 290 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5fa97092a5c23bdf84db06cf744ceadd', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5fa97092a5c23bdf84db06cf744ceadd"') WHERE goods_id = 290 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '89fa79385d283440bfaffb1352d571be', seller_id, 'GOODS', 1 FROM goods WHERE id = 291;
UPDATE goods_images SET file_id = '89fa79385d283440bfaffb1352d571be' WHERE goods_id = 291 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '89fa79385d283440bfaffb1352d571be', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"89fa79385d283440bfaffb1352d571be"') WHERE goods_id = 291 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '49453b45e1c559385928f12f21fbc309', seller_id, 'GOODS', 1 FROM goods WHERE id = 292;
UPDATE goods_images SET file_id = '49453b45e1c559385928f12f21fbc309' WHERE goods_id = 292 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '49453b45e1c559385928f12f21fbc309', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"49453b45e1c559385928f12f21fbc309"') WHERE goods_id = 292 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c730546cff4dae3b22eabe88302d3927', seller_id, 'GOODS', 1 FROM goods WHERE id = 293;
UPDATE goods_images SET file_id = 'c730546cff4dae3b22eabe88302d3927' WHERE goods_id = 293 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c730546cff4dae3b22eabe88302d3927', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c730546cff4dae3b22eabe88302d3927"') WHERE goods_id = 293 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '50ad4b9940edad7f4f33b0d947115cad', seller_id, 'GOODS', 1 FROM goods WHERE id = 294;
UPDATE goods_images SET file_id = '50ad4b9940edad7f4f33b0d947115cad' WHERE goods_id = 294 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '50ad4b9940edad7f4f33b0d947115cad', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"50ad4b9940edad7f4f33b0d947115cad"') WHERE goods_id = 294 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'add3c22f139373f2d73acfe4665fab02', seller_id, 'GOODS', 1 FROM goods WHERE id = 295;
UPDATE goods_images SET file_id = 'add3c22f139373f2d73acfe4665fab02' WHERE goods_id = 295 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'add3c22f139373f2d73acfe4665fab02', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"add3c22f139373f2d73acfe4665fab02"') WHERE goods_id = 295 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '37201e75a71918207cf1be1c8998f799', seller_id, 'GOODS', 1 FROM goods WHERE id = 296;
UPDATE goods_images SET file_id = '37201e75a71918207cf1be1c8998f799' WHERE goods_id = 296 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '37201e75a71918207cf1be1c8998f799', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"37201e75a71918207cf1be1c8998f799"') WHERE goods_id = 296 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '66d344bda0d0ed89e9359089ebf51e68', seller_id, 'GOODS', 1 FROM goods WHERE id = 297;
UPDATE goods_images SET file_id = '66d344bda0d0ed89e9359089ebf51e68' WHERE goods_id = 297 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '66d344bda0d0ed89e9359089ebf51e68', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"66d344bda0d0ed89e9359089ebf51e68"') WHERE goods_id = 297 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4eb55fb5335f33847de5ead65e364eb4', seller_id, 'GOODS', 1 FROM goods WHERE id = 298;
UPDATE goods_images SET file_id = '4eb55fb5335f33847de5ead65e364eb4' WHERE goods_id = 298 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4eb55fb5335f33847de5ead65e364eb4', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4eb55fb5335f33847de5ead65e364eb4"') WHERE goods_id = 298 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '603ac3853420130ce1e425b607174b95', seller_id, 'GOODS', 1 FROM goods WHERE id = 299;
UPDATE goods_images SET file_id = '603ac3853420130ce1e425b607174b95' WHERE goods_id = 299 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '603ac3853420130ce1e425b607174b95', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"603ac3853420130ce1e425b607174b95"') WHERE goods_id = 299 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5b65f2a3f3d998328581e0c069f52002', seller_id, 'GOODS', 1 FROM goods WHERE id = 300;
UPDATE goods_images SET file_id = '5b65f2a3f3d998328581e0c069f52002' WHERE goods_id = 300 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5b65f2a3f3d998328581e0c069f52002', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5b65f2a3f3d998328581e0c069f52002"') WHERE goods_id = 300 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'caac6122ebbb1db60d0efda3e9f37741', seller_id, 'GOODS', 1 FROM goods WHERE id = 301;
UPDATE goods_images SET file_id = 'caac6122ebbb1db60d0efda3e9f37741' WHERE goods_id = 301 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'caac6122ebbb1db60d0efda3e9f37741', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"caac6122ebbb1db60d0efda3e9f37741"') WHERE goods_id = 301 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '73e3fadb6498c40bd8c5adb4dfd75d55', seller_id, 'GOODS', 1 FROM goods WHERE id = 302;
UPDATE goods_images SET file_id = '73e3fadb6498c40bd8c5adb4dfd75d55' WHERE goods_id = 302 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '73e3fadb6498c40bd8c5adb4dfd75d55', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"73e3fadb6498c40bd8c5adb4dfd75d55"') WHERE goods_id = 302 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2072523adf1e7685e03317732da03435', seller_id, 'GOODS', 1 FROM goods WHERE id = 303;
UPDATE goods_images SET file_id = '2072523adf1e7685e03317732da03435' WHERE goods_id = 303 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2072523adf1e7685e03317732da03435', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2072523adf1e7685e03317732da03435"') WHERE goods_id = 303 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a14db978564443826b17cfd28243c850', seller_id, 'GOODS', 1 FROM goods WHERE id = 304;
UPDATE goods_images SET file_id = 'a14db978564443826b17cfd28243c850' WHERE goods_id = 304 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a14db978564443826b17cfd28243c850', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a14db978564443826b17cfd28243c850"') WHERE goods_id = 304 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2b04730a9867da20d18b40f29caea942', seller_id, 'GOODS', 1 FROM goods WHERE id = 305;
UPDATE goods_images SET file_id = '2b04730a9867da20d18b40f29caea942' WHERE goods_id = 305 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2b04730a9867da20d18b40f29caea942', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2b04730a9867da20d18b40f29caea942"') WHERE goods_id = 305 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e5645eb6237f10d16328b6d51a034091', seller_id, 'GOODS', 1 FROM goods WHERE id = 306;
UPDATE goods_images SET file_id = 'e5645eb6237f10d16328b6d51a034091' WHERE goods_id = 306 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e5645eb6237f10d16328b6d51a034091', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e5645eb6237f10d16328b6d51a034091"') WHERE goods_id = 306 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '59fa89e7ebe5e5efead43a28254e6341', seller_id, 'GOODS', 1 FROM goods WHERE id = 307;
UPDATE goods_images SET file_id = '59fa89e7ebe5e5efead43a28254e6341' WHERE goods_id = 307 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '59fa89e7ebe5e5efead43a28254e6341', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"59fa89e7ebe5e5efead43a28254e6341"') WHERE goods_id = 307 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1a8dd604204aaffb918a645bc6f7352b', seller_id, 'GOODS', 1 FROM goods WHERE id = 308;
UPDATE goods_images SET file_id = '1a8dd604204aaffb918a645bc6f7352b' WHERE goods_id = 308 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1a8dd604204aaffb918a645bc6f7352b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1a8dd604204aaffb918a645bc6f7352b"') WHERE goods_id = 308 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '7d94bb24693ef1c6eeffd5efafbd613b', seller_id, 'GOODS', 1 FROM goods WHERE id = 309;
UPDATE goods_images SET file_id = '7d94bb24693ef1c6eeffd5efafbd613b' WHERE goods_id = 309 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '7d94bb24693ef1c6eeffd5efafbd613b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"7d94bb24693ef1c6eeffd5efafbd613b"') WHERE goods_id = 309 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '98d8117cbb510b23c226726cdf3050d8', seller_id, 'GOODS', 1 FROM goods WHERE id = 310;
UPDATE goods_images SET file_id = '98d8117cbb510b23c226726cdf3050d8' WHERE goods_id = 310 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '98d8117cbb510b23c226726cdf3050d8', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"98d8117cbb510b23c226726cdf3050d8"') WHERE goods_id = 310 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4f311fd7874fecc2dfcecf62dcc8843a', seller_id, 'GOODS', 1 FROM goods WHERE id = 311;
UPDATE goods_images SET file_id = '4f311fd7874fecc2dfcecf62dcc8843a' WHERE goods_id = 311 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4f311fd7874fecc2dfcecf62dcc8843a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4f311fd7874fecc2dfcecf62dcc8843a"') WHERE goods_id = 311 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '76a0d0e925339ea3b2a75c49dad1b19a', seller_id, 'GOODS', 1 FROM goods WHERE id = 312;
UPDATE goods_images SET file_id = '76a0d0e925339ea3b2a75c49dad1b19a' WHERE goods_id = 312 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '76a0d0e925339ea3b2a75c49dad1b19a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"76a0d0e925339ea3b2a75c49dad1b19a"') WHERE goods_id = 312 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'de4c8363aa7c93b403c0653798190134', seller_id, 'GOODS', 1 FROM goods WHERE id = 313;
UPDATE goods_images SET file_id = 'de4c8363aa7c93b403c0653798190134' WHERE goods_id = 313 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'de4c8363aa7c93b403c0653798190134', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"de4c8363aa7c93b403c0653798190134"') WHERE goods_id = 313 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8d76f53bc59525b93197b7a260e01335', seller_id, 'GOODS', 1 FROM goods WHERE id = 314;
UPDATE goods_images SET file_id = '8d76f53bc59525b93197b7a260e01335' WHERE goods_id = 314 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8d76f53bc59525b93197b7a260e01335', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8d76f53bc59525b93197b7a260e01335"') WHERE goods_id = 314 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6fe5c1c3ca4d5ad80c754a2350a04922', seller_id, 'GOODS', 1 FROM goods WHERE id = 315;
UPDATE goods_images SET file_id = '6fe5c1c3ca4d5ad80c754a2350a04922' WHERE goods_id = 315 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6fe5c1c3ca4d5ad80c754a2350a04922', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6fe5c1c3ca4d5ad80c754a2350a04922"') WHERE goods_id = 315 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '13e9131f9ea26ee4479497b4ab22f66f', seller_id, 'GOODS', 1 FROM goods WHERE id = 316;
UPDATE goods_images SET file_id = '13e9131f9ea26ee4479497b4ab22f66f' WHERE goods_id = 316 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '13e9131f9ea26ee4479497b4ab22f66f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"13e9131f9ea26ee4479497b4ab22f66f"') WHERE goods_id = 316 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c7a5d7d7a0731646ef9f90db7342508e', seller_id, 'GOODS', 1 FROM goods WHERE id = 317;
UPDATE goods_images SET file_id = 'c7a5d7d7a0731646ef9f90db7342508e' WHERE goods_id = 317 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c7a5d7d7a0731646ef9f90db7342508e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c7a5d7d7a0731646ef9f90db7342508e"') WHERE goods_id = 317 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fab1167e9e30c1c145a0e5544696c384', seller_id, 'GOODS', 1 FROM goods WHERE id = 318;
UPDATE goods_images SET file_id = 'fab1167e9e30c1c145a0e5544696c384' WHERE goods_id = 318 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fab1167e9e30c1c145a0e5544696c384', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fab1167e9e30c1c145a0e5544696c384"') WHERE goods_id = 318 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1e85d977e4dc60f577291623db0cfbb5', seller_id, 'GOODS', 1 FROM goods WHERE id = 319;
UPDATE goods_images SET file_id = '1e85d977e4dc60f577291623db0cfbb5' WHERE goods_id = 319 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1e85d977e4dc60f577291623db0cfbb5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1e85d977e4dc60f577291623db0cfbb5"') WHERE goods_id = 319 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '795bfb5c64746ef7750e947c0d7fd1ed', seller_id, 'GOODS', 1 FROM goods WHERE id = 320;
UPDATE goods_images SET file_id = '795bfb5c64746ef7750e947c0d7fd1ed' WHERE goods_id = 320 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '795bfb5c64746ef7750e947c0d7fd1ed', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"795bfb5c64746ef7750e947c0d7fd1ed"') WHERE goods_id = 320 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f870439b98c6e4941fb7fc55ab4e0f1c', seller_id, 'GOODS', 1 FROM goods WHERE id = 321;
UPDATE goods_images SET file_id = 'f870439b98c6e4941fb7fc55ab4e0f1c' WHERE goods_id = 321 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f870439b98c6e4941fb7fc55ab4e0f1c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f870439b98c6e4941fb7fc55ab4e0f1c"') WHERE goods_id = 321 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '275b197b14243d613d24b88b9621563a', seller_id, 'GOODS', 1 FROM goods WHERE id = 322;
UPDATE goods_images SET file_id = '275b197b14243d613d24b88b9621563a' WHERE goods_id = 322 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '275b197b14243d613d24b88b9621563a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"275b197b14243d613d24b88b9621563a"') WHERE goods_id = 322 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '7168180de2f4f713504e284f34863ccd', seller_id, 'GOODS', 1 FROM goods WHERE id = 323;
UPDATE goods_images SET file_id = '7168180de2f4f713504e284f34863ccd' WHERE goods_id = 323 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '7168180de2f4f713504e284f34863ccd', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"7168180de2f4f713504e284f34863ccd"') WHERE goods_id = 323 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '639c5ca241689aee12a12b701411a615', seller_id, 'GOODS', 1 FROM goods WHERE id = 324;
UPDATE goods_images SET file_id = '639c5ca241689aee12a12b701411a615' WHERE goods_id = 324 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '639c5ca241689aee12a12b701411a615', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"639c5ca241689aee12a12b701411a615"') WHERE goods_id = 324 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fd4f5e8662adfd491e4a29210d9e520a', seller_id, 'GOODS', 1 FROM goods WHERE id = 325;
UPDATE goods_images SET file_id = 'fd4f5e8662adfd491e4a29210d9e520a' WHERE goods_id = 325 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fd4f5e8662adfd491e4a29210d9e520a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fd4f5e8662adfd491e4a29210d9e520a"') WHERE goods_id = 325 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2a3cc5370dae4e42a8a5e1cbb6f34bbe', seller_id, 'GOODS', 1 FROM goods WHERE id = 326;
UPDATE goods_images SET file_id = '2a3cc5370dae4e42a8a5e1cbb6f34bbe' WHERE goods_id = 326 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2a3cc5370dae4e42a8a5e1cbb6f34bbe', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2a3cc5370dae4e42a8a5e1cbb6f34bbe"') WHERE goods_id = 326 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '7ff477bc77b6bf528ef6a8764d23c534', seller_id, 'GOODS', 1 FROM goods WHERE id = 327;
UPDATE goods_images SET file_id = '7ff477bc77b6bf528ef6a8764d23c534' WHERE goods_id = 327 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '7ff477bc77b6bf528ef6a8764d23c534', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"7ff477bc77b6bf528ef6a8764d23c534"') WHERE goods_id = 327 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '59992ad7b28f8f1ca20c0c8a3a3f4e66', seller_id, 'GOODS', 1 FROM goods WHERE id = 328;
UPDATE goods_images SET file_id = '59992ad7b28f8f1ca20c0c8a3a3f4e66' WHERE goods_id = 328 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '59992ad7b28f8f1ca20c0c8a3a3f4e66', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"59992ad7b28f8f1ca20c0c8a3a3f4e66"') WHERE goods_id = 328 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0d55754d5d4ccac250788264816f30e1', seller_id, 'GOODS', 1 FROM goods WHERE id = 329;
UPDATE goods_images SET file_id = '0d55754d5d4ccac250788264816f30e1' WHERE goods_id = 329 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0d55754d5d4ccac250788264816f30e1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0d55754d5d4ccac250788264816f30e1"') WHERE goods_id = 329 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4d6a3df8eacfa4ea8ced3388313d3048', seller_id, 'GOODS', 1 FROM goods WHERE id = 330;
UPDATE goods_images SET file_id = '4d6a3df8eacfa4ea8ced3388313d3048' WHERE goods_id = 330 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4d6a3df8eacfa4ea8ced3388313d3048', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4d6a3df8eacfa4ea8ced3388313d3048"') WHERE goods_id = 330 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0b2c060e80c3da1b0578fba00f6282ee', seller_id, 'GOODS', 1 FROM goods WHERE id = 331;
UPDATE goods_images SET file_id = '0b2c060e80c3da1b0578fba00f6282ee' WHERE goods_id = 331 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0b2c060e80c3da1b0578fba00f6282ee', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0b2c060e80c3da1b0578fba00f6282ee"') WHERE goods_id = 331 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1b8957d533985f01ca7e3e82cfc8ed61', seller_id, 'GOODS', 1 FROM goods WHERE id = 332;
UPDATE goods_images SET file_id = '1b8957d533985f01ca7e3e82cfc8ed61' WHERE goods_id = 332 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1b8957d533985f01ca7e3e82cfc8ed61', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1b8957d533985f01ca7e3e82cfc8ed61"') WHERE goods_id = 332 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b7fc59e7d78ea6599d006e91b0980eec', seller_id, 'GOODS', 1 FROM goods WHERE id = 333;
UPDATE goods_images SET file_id = 'b7fc59e7d78ea6599d006e91b0980eec' WHERE goods_id = 333 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b7fc59e7d78ea6599d006e91b0980eec', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b7fc59e7d78ea6599d006e91b0980eec"') WHERE goods_id = 333 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '035c1f2a225d08284488a1f8deba1fc9', seller_id, 'GOODS', 1 FROM goods WHERE id = 334;
UPDATE goods_images SET file_id = '035c1f2a225d08284488a1f8deba1fc9' WHERE goods_id = 334 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '035c1f2a225d08284488a1f8deba1fc9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"035c1f2a225d08284488a1f8deba1fc9"') WHERE goods_id = 334 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6937e06c8b820596a1278ef8a418a274', seller_id, 'GOODS', 1 FROM goods WHERE id = 335;
UPDATE goods_images SET file_id = '6937e06c8b820596a1278ef8a418a274' WHERE goods_id = 335 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6937e06c8b820596a1278ef8a418a274', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6937e06c8b820596a1278ef8a418a274"') WHERE goods_id = 335 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4912f44b9a546b9d7e9d43c954e4b0d8', seller_id, 'GOODS', 1 FROM goods WHERE id = 336;
UPDATE goods_images SET file_id = '4912f44b9a546b9d7e9d43c954e4b0d8' WHERE goods_id = 336 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4912f44b9a546b9d7e9d43c954e4b0d8', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4912f44b9a546b9d7e9d43c954e4b0d8"') WHERE goods_id = 336 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1e4e1864659fab993a71bc690ddcbee9', seller_id, 'GOODS', 1 FROM goods WHERE id = 337;
UPDATE goods_images SET file_id = '1e4e1864659fab993a71bc690ddcbee9' WHERE goods_id = 337 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1e4e1864659fab993a71bc690ddcbee9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1e4e1864659fab993a71bc690ddcbee9"') WHERE goods_id = 337 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0410f63519ededa3a3323bbd6a243bd5', seller_id, 'GOODS', 1 FROM goods WHERE id = 338;
UPDATE goods_images SET file_id = '0410f63519ededa3a3323bbd6a243bd5' WHERE goods_id = 338 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0410f63519ededa3a3323bbd6a243bd5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0410f63519ededa3a3323bbd6a243bd5"') WHERE goods_id = 338 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4b5e7705a66dac89ddf2c05378d9bf4b', seller_id, 'GOODS', 1 FROM goods WHERE id = 339;
UPDATE goods_images SET file_id = '4b5e7705a66dac89ddf2c05378d9bf4b' WHERE goods_id = 339 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4b5e7705a66dac89ddf2c05378d9bf4b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4b5e7705a66dac89ddf2c05378d9bf4b"') WHERE goods_id = 339 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5062896cf97654e24dc8ac165f943935', seller_id, 'GOODS', 1 FROM goods WHERE id = 340;
UPDATE goods_images SET file_id = '5062896cf97654e24dc8ac165f943935' WHERE goods_id = 340 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5062896cf97654e24dc8ac165f943935', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5062896cf97654e24dc8ac165f943935"') WHERE goods_id = 340 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e2fe4227044a42e823c7bd601005e98d', seller_id, 'GOODS', 1 FROM goods WHERE id = 341;
UPDATE goods_images SET file_id = 'e2fe4227044a42e823c7bd601005e98d' WHERE goods_id = 341 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e2fe4227044a42e823c7bd601005e98d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e2fe4227044a42e823c7bd601005e98d"') WHERE goods_id = 341 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd03a9ca8c74e3bd105498df969ded451', seller_id, 'GOODS', 1 FROM goods WHERE id = 342;
UPDATE goods_images SET file_id = 'd03a9ca8c74e3bd105498df969ded451' WHERE goods_id = 342 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd03a9ca8c74e3bd105498df969ded451', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d03a9ca8c74e3bd105498df969ded451"') WHERE goods_id = 342 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd2a058f03ca91f32e033c9a5f7244657', seller_id, 'GOODS', 1 FROM goods WHERE id = 343;
UPDATE goods_images SET file_id = 'd2a058f03ca91f32e033c9a5f7244657' WHERE goods_id = 343 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd2a058f03ca91f32e033c9a5f7244657', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d2a058f03ca91f32e033c9a5f7244657"') WHERE goods_id = 343 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c5b8efd9d221b1292f6d997c288cb483', seller_id, 'GOODS', 1 FROM goods WHERE id = 344;
UPDATE goods_images SET file_id = 'c5b8efd9d221b1292f6d997c288cb483' WHERE goods_id = 344 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c5b8efd9d221b1292f6d997c288cb483', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c5b8efd9d221b1292f6d997c288cb483"') WHERE goods_id = 344 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1fd827f6051184b6e01aa7be89d407f2', seller_id, 'GOODS', 1 FROM goods WHERE id = 345;
UPDATE goods_images SET file_id = '1fd827f6051184b6e01aa7be89d407f2' WHERE goods_id = 345 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1fd827f6051184b6e01aa7be89d407f2', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1fd827f6051184b6e01aa7be89d407f2"') WHERE goods_id = 345 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f98bd84d2c263670c3b44cfadab0d99b', seller_id, 'GOODS', 1 FROM goods WHERE id = 346;
UPDATE goods_images SET file_id = 'f98bd84d2c263670c3b44cfadab0d99b' WHERE goods_id = 346 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f98bd84d2c263670c3b44cfadab0d99b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f98bd84d2c263670c3b44cfadab0d99b"') WHERE goods_id = 346 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2fc40bd815f5ebda563cc662d1aff93c', seller_id, 'GOODS', 1 FROM goods WHERE id = 347;
UPDATE goods_images SET file_id = '2fc40bd815f5ebda563cc662d1aff93c' WHERE goods_id = 347 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2fc40bd815f5ebda563cc662d1aff93c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2fc40bd815f5ebda563cc662d1aff93c"') WHERE goods_id = 347 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '7f71c55167e912bd75d9b4c50c6116d7', seller_id, 'GOODS', 1 FROM goods WHERE id = 348;
UPDATE goods_images SET file_id = '7f71c55167e912bd75d9b4c50c6116d7' WHERE goods_id = 348 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '7f71c55167e912bd75d9b4c50c6116d7', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"7f71c55167e912bd75d9b4c50c6116d7"') WHERE goods_id = 348 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e947107c748a109684ba8bbe8720e297', seller_id, 'GOODS', 1 FROM goods WHERE id = 349;
UPDATE goods_images SET file_id = 'e947107c748a109684ba8bbe8720e297' WHERE goods_id = 349 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e947107c748a109684ba8bbe8720e297', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e947107c748a109684ba8bbe8720e297"') WHERE goods_id = 349 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ca426ec804f52784eba0b3f1d8484b10', seller_id, 'GOODS', 1 FROM goods WHERE id = 350;
UPDATE goods_images SET file_id = 'ca426ec804f52784eba0b3f1d8484b10' WHERE goods_id = 350 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ca426ec804f52784eba0b3f1d8484b10', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ca426ec804f52784eba0b3f1d8484b10"') WHERE goods_id = 350 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ec9b106cfa8c891c257fa734b9f46576', seller_id, 'GOODS', 1 FROM goods WHERE id = 351;
UPDATE goods_images SET file_id = 'ec9b106cfa8c891c257fa734b9f46576' WHERE goods_id = 351 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ec9b106cfa8c891c257fa734b9f46576', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ec9b106cfa8c891c257fa734b9f46576"') WHERE goods_id = 351 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '098ad53714a9b79e83afd7c0130d54eb', seller_id, 'GOODS', 1 FROM goods WHERE id = 352;
UPDATE goods_images SET file_id = '098ad53714a9b79e83afd7c0130d54eb' WHERE goods_id = 352 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '098ad53714a9b79e83afd7c0130d54eb', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"098ad53714a9b79e83afd7c0130d54eb"') WHERE goods_id = 352 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '43d9b4cf6c2de5638d08c781eef16bbe', seller_id, 'GOODS', 1 FROM goods WHERE id = 353;
UPDATE goods_images SET file_id = '43d9b4cf6c2de5638d08c781eef16bbe' WHERE goods_id = 353 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '43d9b4cf6c2de5638d08c781eef16bbe', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"43d9b4cf6c2de5638d08c781eef16bbe"') WHERE goods_id = 353 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '36a92ab552559dc54e606964b134fa6b', seller_id, 'GOODS', 1 FROM goods WHERE id = 354;
UPDATE goods_images SET file_id = '36a92ab552559dc54e606964b134fa6b' WHERE goods_id = 354 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '36a92ab552559dc54e606964b134fa6b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"36a92ab552559dc54e606964b134fa6b"') WHERE goods_id = 354 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5924afe2169dd0f231b10a9637559b77', seller_id, 'GOODS', 1 FROM goods WHERE id = 355;
UPDATE goods_images SET file_id = '5924afe2169dd0f231b10a9637559b77' WHERE goods_id = 355 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5924afe2169dd0f231b10a9637559b77', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5924afe2169dd0f231b10a9637559b77"') WHERE goods_id = 355 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6053a830a8220ba2666e2244423a90c7', seller_id, 'GOODS', 1 FROM goods WHERE id = 356;
UPDATE goods_images SET file_id = '6053a830a8220ba2666e2244423a90c7' WHERE goods_id = 356 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6053a830a8220ba2666e2244423a90c7', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6053a830a8220ba2666e2244423a90c7"') WHERE goods_id = 356 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '20d23a6de3afe81953f2f5f2b8288f28', seller_id, 'GOODS', 1 FROM goods WHERE id = 357;
UPDATE goods_images SET file_id = '20d23a6de3afe81953f2f5f2b8288f28' WHERE goods_id = 357 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '20d23a6de3afe81953f2f5f2b8288f28', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"20d23a6de3afe81953f2f5f2b8288f28"') WHERE goods_id = 357 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '7a56a6442b192df8fa8c8816375d7c66', seller_id, 'GOODS', 1 FROM goods WHERE id = 358;
UPDATE goods_images SET file_id = '7a56a6442b192df8fa8c8816375d7c66' WHERE goods_id = 358 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '7a56a6442b192df8fa8c8816375d7c66', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"7a56a6442b192df8fa8c8816375d7c66"') WHERE goods_id = 358 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0344a7c04c74c24d7962465d25b7d8ce', seller_id, 'GOODS', 1 FROM goods WHERE id = 359;
UPDATE goods_images SET file_id = '0344a7c04c74c24d7962465d25b7d8ce' WHERE goods_id = 359 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0344a7c04c74c24d7962465d25b7d8ce', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0344a7c04c74c24d7962465d25b7d8ce"') WHERE goods_id = 359 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '414272f98d05eca743e372f01b5994de', seller_id, 'GOODS', 1 FROM goods WHERE id = 360;
UPDATE goods_images SET file_id = '414272f98d05eca743e372f01b5994de' WHERE goods_id = 360 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '414272f98d05eca743e372f01b5994de', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"414272f98d05eca743e372f01b5994de"') WHERE goods_id = 360 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0e77f233bc568bec74bcb62d8789ecd8', seller_id, 'GOODS', 1 FROM goods WHERE id = 361;
UPDATE goods_images SET file_id = '0e77f233bc568bec74bcb62d8789ecd8' WHERE goods_id = 361 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0e77f233bc568bec74bcb62d8789ecd8', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0e77f233bc568bec74bcb62d8789ecd8"') WHERE goods_id = 361 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ff96c12f22061d219f9eeaa865cd3dd0', seller_id, 'GOODS', 1 FROM goods WHERE id = 362;
UPDATE goods_images SET file_id = 'ff96c12f22061d219f9eeaa865cd3dd0' WHERE goods_id = 362 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ff96c12f22061d219f9eeaa865cd3dd0', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ff96c12f22061d219f9eeaa865cd3dd0"') WHERE goods_id = 362 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8a7861714fc24eb615e4c678665c3d26', seller_id, 'GOODS', 1 FROM goods WHERE id = 363;
UPDATE goods_images SET file_id = '8a7861714fc24eb615e4c678665c3d26' WHERE goods_id = 363 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8a7861714fc24eb615e4c678665c3d26', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8a7861714fc24eb615e4c678665c3d26"') WHERE goods_id = 363 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd906f9e5655e05c8cf9524537d43b6a5', seller_id, 'GOODS', 1 FROM goods WHERE id = 364;
UPDATE goods_images SET file_id = 'd906f9e5655e05c8cf9524537d43b6a5' WHERE goods_id = 364 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd906f9e5655e05c8cf9524537d43b6a5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d906f9e5655e05c8cf9524537d43b6a5"') WHERE goods_id = 364 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6ff1ddc9cadbd4d8fa521201da4b7bf0', seller_id, 'GOODS', 1 FROM goods WHERE id = 365;
UPDATE goods_images SET file_id = '6ff1ddc9cadbd4d8fa521201da4b7bf0' WHERE goods_id = 365 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6ff1ddc9cadbd4d8fa521201da4b7bf0', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6ff1ddc9cadbd4d8fa521201da4b7bf0"') WHERE goods_id = 365 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '111d6f8c8e41402e216f0ff2a692257a', seller_id, 'GOODS', 1 FROM goods WHERE id = 366;
UPDATE goods_images SET file_id = '111d6f8c8e41402e216f0ff2a692257a' WHERE goods_id = 366 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '111d6f8c8e41402e216f0ff2a692257a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"111d6f8c8e41402e216f0ff2a692257a"') WHERE goods_id = 366 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e28a8d91d4eba0c124df774a0b37616d', seller_id, 'GOODS', 1 FROM goods WHERE id = 367;
UPDATE goods_images SET file_id = 'e28a8d91d4eba0c124df774a0b37616d' WHERE goods_id = 367 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e28a8d91d4eba0c124df774a0b37616d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e28a8d91d4eba0c124df774a0b37616d"') WHERE goods_id = 367 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c358f83e3f97dce2e49ca27206814edc', seller_id, 'GOODS', 1 FROM goods WHERE id = 368;
UPDATE goods_images SET file_id = 'c358f83e3f97dce2e49ca27206814edc' WHERE goods_id = 368 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c358f83e3f97dce2e49ca27206814edc', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c358f83e3f97dce2e49ca27206814edc"') WHERE goods_id = 368 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f331f533b8ddf8fa7d449f023d382d79', seller_id, 'GOODS', 1 FROM goods WHERE id = 369;
UPDATE goods_images SET file_id = 'f331f533b8ddf8fa7d449f023d382d79' WHERE goods_id = 369 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f331f533b8ddf8fa7d449f023d382d79', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f331f533b8ddf8fa7d449f023d382d79"') WHERE goods_id = 369 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4e59ee1a130038c1bec0e1fa3bca830f', seller_id, 'GOODS', 1 FROM goods WHERE id = 370;
UPDATE goods_images SET file_id = '4e59ee1a130038c1bec0e1fa3bca830f' WHERE goods_id = 370 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4e59ee1a130038c1bec0e1fa3bca830f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4e59ee1a130038c1bec0e1fa3bca830f"') WHERE goods_id = 370 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '02ddf7a17a4e71935f6e4821b9fb56bf', seller_id, 'GOODS', 1 FROM goods WHERE id = 371;
UPDATE goods_images SET file_id = '02ddf7a17a4e71935f6e4821b9fb56bf' WHERE goods_id = 371 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '02ddf7a17a4e71935f6e4821b9fb56bf', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"02ddf7a17a4e71935f6e4821b9fb56bf"') WHERE goods_id = 371 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8f9f33912438d4f7e70bd381e66fc56c', seller_id, 'GOODS', 1 FROM goods WHERE id = 372;
UPDATE goods_images SET file_id = '8f9f33912438d4f7e70bd381e66fc56c' WHERE goods_id = 372 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8f9f33912438d4f7e70bd381e66fc56c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8f9f33912438d4f7e70bd381e66fc56c"') WHERE goods_id = 372 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b885efd86a5ecbf4327b36da622c3801', seller_id, 'GOODS', 1 FROM goods WHERE id = 373;
UPDATE goods_images SET file_id = 'b885efd86a5ecbf4327b36da622c3801' WHERE goods_id = 373 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b885efd86a5ecbf4327b36da622c3801', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b885efd86a5ecbf4327b36da622c3801"') WHERE goods_id = 373 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '08935c983adf1654cdc0495cac707569', seller_id, 'GOODS', 1 FROM goods WHERE id = 374;
UPDATE goods_images SET file_id = '08935c983adf1654cdc0495cac707569' WHERE goods_id = 374 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '08935c983adf1654cdc0495cac707569', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"08935c983adf1654cdc0495cac707569"') WHERE goods_id = 374 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd1422039ad2c8133f8c65494b57e10ae', seller_id, 'GOODS', 1 FROM goods WHERE id = 375;
UPDATE goods_images SET file_id = 'd1422039ad2c8133f8c65494b57e10ae' WHERE goods_id = 375 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd1422039ad2c8133f8c65494b57e10ae', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d1422039ad2c8133f8c65494b57e10ae"') WHERE goods_id = 375 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1e9dbee8b1c0440ede99d9c2ec3d545e', seller_id, 'GOODS', 1 FROM goods WHERE id = 376;
UPDATE goods_images SET file_id = '1e9dbee8b1c0440ede99d9c2ec3d545e' WHERE goods_id = 376 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1e9dbee8b1c0440ede99d9c2ec3d545e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1e9dbee8b1c0440ede99d9c2ec3d545e"') WHERE goods_id = 376 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a699740a302b51204bf6199372d8aec0', seller_id, 'GOODS', 1 FROM goods WHERE id = 377;
UPDATE goods_images SET file_id = 'a699740a302b51204bf6199372d8aec0' WHERE goods_id = 377 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a699740a302b51204bf6199372d8aec0', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a699740a302b51204bf6199372d8aec0"') WHERE goods_id = 377 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4b3f91d1f369176e300f4e5bd0432cb2', seller_id, 'GOODS', 1 FROM goods WHERE id = 378;
UPDATE goods_images SET file_id = '4b3f91d1f369176e300f4e5bd0432cb2' WHERE goods_id = 378 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4b3f91d1f369176e300f4e5bd0432cb2', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4b3f91d1f369176e300f4e5bd0432cb2"') WHERE goods_id = 378 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '794676c154671803b9d5209f6014afd8', seller_id, 'GOODS', 1 FROM goods WHERE id = 379;
UPDATE goods_images SET file_id = '794676c154671803b9d5209f6014afd8' WHERE goods_id = 379 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '794676c154671803b9d5209f6014afd8', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"794676c154671803b9d5209f6014afd8"') WHERE goods_id = 379 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0422df443f79bec66bd5a70031739cc7', seller_id, 'GOODS', 1 FROM goods WHERE id = 380;
UPDATE goods_images SET file_id = '0422df443f79bec66bd5a70031739cc7' WHERE goods_id = 380 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0422df443f79bec66bd5a70031739cc7', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0422df443f79bec66bd5a70031739cc7"') WHERE goods_id = 380 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'faaba7dac581aef81a4dcca423d6de23', seller_id, 'GOODS', 1 FROM goods WHERE id = 381;
UPDATE goods_images SET file_id = 'faaba7dac581aef81a4dcca423d6de23' WHERE goods_id = 381 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'faaba7dac581aef81a4dcca423d6de23', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"faaba7dac581aef81a4dcca423d6de23"') WHERE goods_id = 381 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3cc4c9e862b023d70f0c0f76ee24e092', seller_id, 'GOODS', 1 FROM goods WHERE id = 382;
UPDATE goods_images SET file_id = '3cc4c9e862b023d70f0c0f76ee24e092' WHERE goods_id = 382 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3cc4c9e862b023d70f0c0f76ee24e092', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3cc4c9e862b023d70f0c0f76ee24e092"') WHERE goods_id = 382 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fd69de523c2bf344c53c712053e030a8', seller_id, 'GOODS', 1 FROM goods WHERE id = 383;
UPDATE goods_images SET file_id = 'fd69de523c2bf344c53c712053e030a8' WHERE goods_id = 383 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fd69de523c2bf344c53c712053e030a8', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fd69de523c2bf344c53c712053e030a8"') WHERE goods_id = 383 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f226c724121f43513d07d21c0aee1342', seller_id, 'GOODS', 1 FROM goods WHERE id = 384;
UPDATE goods_images SET file_id = 'f226c724121f43513d07d21c0aee1342' WHERE goods_id = 384 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f226c724121f43513d07d21c0aee1342', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f226c724121f43513d07d21c0aee1342"') WHERE goods_id = 384 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6381d77695265f863c14593ecd561df5', seller_id, 'GOODS', 1 FROM goods WHERE id = 385;
UPDATE goods_images SET file_id = '6381d77695265f863c14593ecd561df5' WHERE goods_id = 385 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6381d77695265f863c14593ecd561df5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6381d77695265f863c14593ecd561df5"') WHERE goods_id = 385 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '023f2b52e6f25199d6bb292dcff23973', seller_id, 'GOODS', 1 FROM goods WHERE id = 386;
UPDATE goods_images SET file_id = '023f2b52e6f25199d6bb292dcff23973' WHERE goods_id = 386 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '023f2b52e6f25199d6bb292dcff23973', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"023f2b52e6f25199d6bb292dcff23973"') WHERE goods_id = 386 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c36e22589b0ab00b8927bb1fe9700832', seller_id, 'GOODS', 1 FROM goods WHERE id = 387;
UPDATE goods_images SET file_id = 'c36e22589b0ab00b8927bb1fe9700832' WHERE goods_id = 387 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c36e22589b0ab00b8927bb1fe9700832', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c36e22589b0ab00b8927bb1fe9700832"') WHERE goods_id = 387 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2065cb704282f349ce94dc439809350f', seller_id, 'GOODS', 1 FROM goods WHERE id = 388;
UPDATE goods_images SET file_id = '2065cb704282f349ce94dc439809350f' WHERE goods_id = 388 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2065cb704282f349ce94dc439809350f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2065cb704282f349ce94dc439809350f"') WHERE goods_id = 388 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ac3bfc8aa6bd3d902f1b59acfbaec884', seller_id, 'GOODS', 1 FROM goods WHERE id = 389;
UPDATE goods_images SET file_id = 'ac3bfc8aa6bd3d902f1b59acfbaec884' WHERE goods_id = 389 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ac3bfc8aa6bd3d902f1b59acfbaec884', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ac3bfc8aa6bd3d902f1b59acfbaec884"') WHERE goods_id = 389 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '767c5cebe740dc61dd73be2a20371621', seller_id, 'GOODS', 1 FROM goods WHERE id = 390;
UPDATE goods_images SET file_id = '767c5cebe740dc61dd73be2a20371621' WHERE goods_id = 390 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '767c5cebe740dc61dd73be2a20371621', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"767c5cebe740dc61dd73be2a20371621"') WHERE goods_id = 390 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0d6b456f9744d99d7a2f462b52d0d6f8', seller_id, 'GOODS', 1 FROM goods WHERE id = 391;
UPDATE goods_images SET file_id = '0d6b456f9744d99d7a2f462b52d0d6f8' WHERE goods_id = 391 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0d6b456f9744d99d7a2f462b52d0d6f8', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0d6b456f9744d99d7a2f462b52d0d6f8"') WHERE goods_id = 391 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3202cb89ac8f79fc0de01756d93a5f4a', seller_id, 'GOODS', 1 FROM goods WHERE id = 392;
UPDATE goods_images SET file_id = '3202cb89ac8f79fc0de01756d93a5f4a' WHERE goods_id = 392 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3202cb89ac8f79fc0de01756d93a5f4a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3202cb89ac8f79fc0de01756d93a5f4a"') WHERE goods_id = 392 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '7394c7870ae418674b273821a93d92d2', seller_id, 'GOODS', 1 FROM goods WHERE id = 393;
UPDATE goods_images SET file_id = '7394c7870ae418674b273821a93d92d2' WHERE goods_id = 393 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '7394c7870ae418674b273821a93d92d2', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"7394c7870ae418674b273821a93d92d2"') WHERE goods_id = 393 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '30315956e19a7663997da1e631beec74', seller_id, 'GOODS', 1 FROM goods WHERE id = 394;
UPDATE goods_images SET file_id = '30315956e19a7663997da1e631beec74' WHERE goods_id = 394 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '30315956e19a7663997da1e631beec74', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"30315956e19a7663997da1e631beec74"') WHERE goods_id = 394 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ce8adf77fabee96ce6eca108e96e81b7', seller_id, 'GOODS', 1 FROM goods WHERE id = 395;
UPDATE goods_images SET file_id = 'ce8adf77fabee96ce6eca108e96e81b7' WHERE goods_id = 395 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ce8adf77fabee96ce6eca108e96e81b7', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ce8adf77fabee96ce6eca108e96e81b7"') WHERE goods_id = 395 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'afe5a6db2206e9364c1431f77c9174e6', seller_id, 'GOODS', 1 FROM goods WHERE id = 396;
UPDATE goods_images SET file_id = 'afe5a6db2206e9364c1431f77c9174e6' WHERE goods_id = 396 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'afe5a6db2206e9364c1431f77c9174e6', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"afe5a6db2206e9364c1431f77c9174e6"') WHERE goods_id = 396 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5b580f8f14fd87397ff51e7e9fbaea0f', seller_id, 'GOODS', 1 FROM goods WHERE id = 397;
UPDATE goods_images SET file_id = '5b580f8f14fd87397ff51e7e9fbaea0f' WHERE goods_id = 397 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5b580f8f14fd87397ff51e7e9fbaea0f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5b580f8f14fd87397ff51e7e9fbaea0f"') WHERE goods_id = 397 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c6e668bbd5e21c5869e788babf28183f', seller_id, 'GOODS', 1 FROM goods WHERE id = 398;
UPDATE goods_images SET file_id = 'c6e668bbd5e21c5869e788babf28183f' WHERE goods_id = 398 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c6e668bbd5e21c5869e788babf28183f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c6e668bbd5e21c5869e788babf28183f"') WHERE goods_id = 398 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a2affdb5a81d87c66434a6ae74bd824e', seller_id, 'GOODS', 1 FROM goods WHERE id = 399;
UPDATE goods_images SET file_id = 'a2affdb5a81d87c66434a6ae74bd824e' WHERE goods_id = 399 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a2affdb5a81d87c66434a6ae74bd824e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a2affdb5a81d87c66434a6ae74bd824e"') WHERE goods_id = 399 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3d1d985ea2c7b1453bddd0ab0f484a56', seller_id, 'GOODS', 1 FROM goods WHERE id = 400;
UPDATE goods_images SET file_id = '3d1d985ea2c7b1453bddd0ab0f484a56' WHERE goods_id = 400 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3d1d985ea2c7b1453bddd0ab0f484a56', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3d1d985ea2c7b1453bddd0ab0f484a56"') WHERE goods_id = 400 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0b2e6bc37e0f457626a9574635597bae', seller_id, 'GOODS', 1 FROM goods WHERE id = 401;
UPDATE goods_images SET file_id = '0b2e6bc37e0f457626a9574635597bae' WHERE goods_id = 401 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0b2e6bc37e0f457626a9574635597bae', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0b2e6bc37e0f457626a9574635597bae"') WHERE goods_id = 401 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '46d00d9aa9da643396a3a4415c252109', seller_id, 'GOODS', 1 FROM goods WHERE id = 402;
UPDATE goods_images SET file_id = '46d00d9aa9da643396a3a4415c252109' WHERE goods_id = 402 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '46d00d9aa9da643396a3a4415c252109', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"46d00d9aa9da643396a3a4415c252109"') WHERE goods_id = 402 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '585c9dd9c1dc740e69e911a6e319936a', seller_id, 'GOODS', 1 FROM goods WHERE id = 403;
UPDATE goods_images SET file_id = '585c9dd9c1dc740e69e911a6e319936a' WHERE goods_id = 403 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '585c9dd9c1dc740e69e911a6e319936a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"585c9dd9c1dc740e69e911a6e319936a"') WHERE goods_id = 403 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0227f924f0e3ce93f383433d842b623e', seller_id, 'GOODS', 1 FROM goods WHERE id = 404;
UPDATE goods_images SET file_id = '0227f924f0e3ce93f383433d842b623e' WHERE goods_id = 404 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0227f924f0e3ce93f383433d842b623e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0227f924f0e3ce93f383433d842b623e"') WHERE goods_id = 404 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '975d3f34ad4f10821c6fe4d6462b2b39', seller_id, 'GOODS', 1 FROM goods WHERE id = 405;
UPDATE goods_images SET file_id = '975d3f34ad4f10821c6fe4d6462b2b39' WHERE goods_id = 405 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '975d3f34ad4f10821c6fe4d6462b2b39', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"975d3f34ad4f10821c6fe4d6462b2b39"') WHERE goods_id = 405 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b223cd0569e3af705c7a40f6d8bf42f7', seller_id, 'GOODS', 1 FROM goods WHERE id = 406;
UPDATE goods_images SET file_id = 'b223cd0569e3af705c7a40f6d8bf42f7' WHERE goods_id = 406 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b223cd0569e3af705c7a40f6d8bf42f7', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b223cd0569e3af705c7a40f6d8bf42f7"') WHERE goods_id = 406 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '377c9d126e738c240ea57265e8cc9708', seller_id, 'GOODS', 1 FROM goods WHERE id = 407;
UPDATE goods_images SET file_id = '377c9d126e738c240ea57265e8cc9708' WHERE goods_id = 407 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '377c9d126e738c240ea57265e8cc9708', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"377c9d126e738c240ea57265e8cc9708"') WHERE goods_id = 407 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '44ada9600547a31fd83889907c41c433', seller_id, 'GOODS', 1 FROM goods WHERE id = 408;
UPDATE goods_images SET file_id = '44ada9600547a31fd83889907c41c433' WHERE goods_id = 408 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '44ada9600547a31fd83889907c41c433', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"44ada9600547a31fd83889907c41c433"') WHERE goods_id = 408 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fa614511664c4388e4fc14285a4548dd', seller_id, 'GOODS', 1 FROM goods WHERE id = 409;
UPDATE goods_images SET file_id = 'fa614511664c4388e4fc14285a4548dd' WHERE goods_id = 409 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fa614511664c4388e4fc14285a4548dd', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fa614511664c4388e4fc14285a4548dd"') WHERE goods_id = 409 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '11450e4c802b3e91205223ed85e926af', seller_id, 'GOODS', 1 FROM goods WHERE id = 410;
UPDATE goods_images SET file_id = '11450e4c802b3e91205223ed85e926af' WHERE goods_id = 410 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '11450e4c802b3e91205223ed85e926af', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"11450e4c802b3e91205223ed85e926af"') WHERE goods_id = 410 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f7e60606da468d2a8d37c8d1b846763d', seller_id, 'GOODS', 1 FROM goods WHERE id = 411;
UPDATE goods_images SET file_id = 'f7e60606da468d2a8d37c8d1b846763d' WHERE goods_id = 411 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f7e60606da468d2a8d37c8d1b846763d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f7e60606da468d2a8d37c8d1b846763d"') WHERE goods_id = 411 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0356ff39b32442354d1df4bede1c2a03', seller_id, 'GOODS', 1 FROM goods WHERE id = 412;
UPDATE goods_images SET file_id = '0356ff39b32442354d1df4bede1c2a03' WHERE goods_id = 412 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0356ff39b32442354d1df4bede1c2a03', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0356ff39b32442354d1df4bede1c2a03"') WHERE goods_id = 412 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f8b7f1c3df144cbe88f45955f7c70df2', seller_id, 'GOODS', 1 FROM goods WHERE id = 413;
UPDATE goods_images SET file_id = 'f8b7f1c3df144cbe88f45955f7c70df2' WHERE goods_id = 413 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f8b7f1c3df144cbe88f45955f7c70df2', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f8b7f1c3df144cbe88f45955f7c70df2"') WHERE goods_id = 413 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b6aba81da718fea9021f40c84e352be6', seller_id, 'GOODS', 1 FROM goods WHERE id = 414;
UPDATE goods_images SET file_id = 'b6aba81da718fea9021f40c84e352be6' WHERE goods_id = 414 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b6aba81da718fea9021f40c84e352be6', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b6aba81da718fea9021f40c84e352be6"') WHERE goods_id = 414 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '07b36b3a26282faabc9bb915f6890d03', seller_id, 'GOODS', 1 FROM goods WHERE id = 415;
UPDATE goods_images SET file_id = '07b36b3a26282faabc9bb915f6890d03' WHERE goods_id = 415 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '07b36b3a26282faabc9bb915f6890d03', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"07b36b3a26282faabc9bb915f6890d03"') WHERE goods_id = 415 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4495b21502b7e769b195c21ffd2a1491', seller_id, 'GOODS', 1 FROM goods WHERE id = 416;
UPDATE goods_images SET file_id = '4495b21502b7e769b195c21ffd2a1491' WHERE goods_id = 416 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4495b21502b7e769b195c21ffd2a1491', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4495b21502b7e769b195c21ffd2a1491"') WHERE goods_id = 416 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '28071e5b7888dd2a7c5d810f5ee2a05f', seller_id, 'GOODS', 1 FROM goods WHERE id = 417;
UPDATE goods_images SET file_id = '28071e5b7888dd2a7c5d810f5ee2a05f' WHERE goods_id = 417 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '28071e5b7888dd2a7c5d810f5ee2a05f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"28071e5b7888dd2a7c5d810f5ee2a05f"') WHERE goods_id = 417 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '32c8241fa0d349a10efed20c074c250f', seller_id, 'GOODS', 1 FROM goods WHERE id = 418;
UPDATE goods_images SET file_id = '32c8241fa0d349a10efed20c074c250f' WHERE goods_id = 418 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '32c8241fa0d349a10efed20c074c250f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"32c8241fa0d349a10efed20c074c250f"') WHERE goods_id = 418 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e3aaa96a1949e06e3666ee0dca7472dc', seller_id, 'GOODS', 1 FROM goods WHERE id = 419;
UPDATE goods_images SET file_id = 'e3aaa96a1949e06e3666ee0dca7472dc' WHERE goods_id = 419 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e3aaa96a1949e06e3666ee0dca7472dc', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e3aaa96a1949e06e3666ee0dca7472dc"') WHERE goods_id = 419 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '21f7f3a63744ac30f83eeda8f2b55dd3', seller_id, 'GOODS', 1 FROM goods WHERE id = 420;
UPDATE goods_images SET file_id = '21f7f3a63744ac30f83eeda8f2b55dd3' WHERE goods_id = 420 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '21f7f3a63744ac30f83eeda8f2b55dd3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"21f7f3a63744ac30f83eeda8f2b55dd3"') WHERE goods_id = 420 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '87218ee496d0da5d2827e70384865bb7', seller_id, 'GOODS', 1 FROM goods WHERE id = 421;
UPDATE goods_images SET file_id = '87218ee496d0da5d2827e70384865bb7' WHERE goods_id = 421 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '87218ee496d0da5d2827e70384865bb7', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"87218ee496d0da5d2827e70384865bb7"') WHERE goods_id = 421 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c99c331f58ad25460d8a49ce6a2001a0', seller_id, 'GOODS', 1 FROM goods WHERE id = 422;
UPDATE goods_images SET file_id = 'c99c331f58ad25460d8a49ce6a2001a0' WHERE goods_id = 422 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c99c331f58ad25460d8a49ce6a2001a0', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c99c331f58ad25460d8a49ce6a2001a0"') WHERE goods_id = 422 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ec4bf635029649a4c090314fcb85ac54', seller_id, 'GOODS', 1 FROM goods WHERE id = 423;
UPDATE goods_images SET file_id = 'ec4bf635029649a4c090314fcb85ac54' WHERE goods_id = 423 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ec4bf635029649a4c090314fcb85ac54', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ec4bf635029649a4c090314fcb85ac54"') WHERE goods_id = 423 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0868204a7d88acf4f57516b9a56377b2', seller_id, 'GOODS', 1 FROM goods WHERE id = 424;
UPDATE goods_images SET file_id = '0868204a7d88acf4f57516b9a56377b2' WHERE goods_id = 424 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0868204a7d88acf4f57516b9a56377b2', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0868204a7d88acf4f57516b9a56377b2"') WHERE goods_id = 424 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd39877509cfd62fcce74992f4b454b48', seller_id, 'GOODS', 1 FROM goods WHERE id = 425;
UPDATE goods_images SET file_id = 'd39877509cfd62fcce74992f4b454b48' WHERE goods_id = 425 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd39877509cfd62fcce74992f4b454b48', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d39877509cfd62fcce74992f4b454b48"') WHERE goods_id = 425 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '47a96078ee5cbaa1850598ee4396f3fc', seller_id, 'GOODS', 1 FROM goods WHERE id = 426;
UPDATE goods_images SET file_id = '47a96078ee5cbaa1850598ee4396f3fc' WHERE goods_id = 426 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '47a96078ee5cbaa1850598ee4396f3fc', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"47a96078ee5cbaa1850598ee4396f3fc"') WHERE goods_id = 426 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '56c335e4bfe0160f618205c92978ef23', seller_id, 'GOODS', 1 FROM goods WHERE id = 427;
UPDATE goods_images SET file_id = '56c335e4bfe0160f618205c92978ef23' WHERE goods_id = 427 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '56c335e4bfe0160f618205c92978ef23', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"56c335e4bfe0160f618205c92978ef23"') WHERE goods_id = 427 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '241537af55747677a85b72c5b533f53e', seller_id, 'GOODS', 1 FROM goods WHERE id = 428;
UPDATE goods_images SET file_id = '241537af55747677a85b72c5b533f53e' WHERE goods_id = 428 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '241537af55747677a85b72c5b533f53e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"241537af55747677a85b72c5b533f53e"') WHERE goods_id = 428 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '22a732a026f7c0f183aaee2a6ba04308', seller_id, 'GOODS', 1 FROM goods WHERE id = 429;
UPDATE goods_images SET file_id = '22a732a026f7c0f183aaee2a6ba04308' WHERE goods_id = 429 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '22a732a026f7c0f183aaee2a6ba04308', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"22a732a026f7c0f183aaee2a6ba04308"') WHERE goods_id = 429 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5c05260bb1e470d8467fcd4690d76b55', seller_id, 'GOODS', 1 FROM goods WHERE id = 430;
UPDATE goods_images SET file_id = '5c05260bb1e470d8467fcd4690d76b55' WHERE goods_id = 430 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5c05260bb1e470d8467fcd4690d76b55', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5c05260bb1e470d8467fcd4690d76b55"') WHERE goods_id = 430 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fac424481a5c70695778e82032ec7427', seller_id, 'GOODS', 1 FROM goods WHERE id = 431;
UPDATE goods_images SET file_id = 'fac424481a5c70695778e82032ec7427' WHERE goods_id = 431 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fac424481a5c70695778e82032ec7427', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fac424481a5c70695778e82032ec7427"') WHERE goods_id = 431 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0608236a90a68685c7921267879a5eeb', seller_id, 'GOODS', 1 FROM goods WHERE id = 432;
UPDATE goods_images SET file_id = '0608236a90a68685c7921267879a5eeb' WHERE goods_id = 432 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0608236a90a68685c7921267879a5eeb', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0608236a90a68685c7921267879a5eeb"') WHERE goods_id = 432 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c81c73bd61b5b8583abcfb7aeb785f09', seller_id, 'GOODS', 1 FROM goods WHERE id = 433;
UPDATE goods_images SET file_id = 'c81c73bd61b5b8583abcfb7aeb785f09' WHERE goods_id = 433 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c81c73bd61b5b8583abcfb7aeb785f09', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c81c73bd61b5b8583abcfb7aeb785f09"') WHERE goods_id = 433 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '203953ae8e27e23fbf9fde08626120e9', seller_id, 'GOODS', 1 FROM goods WHERE id = 434;
UPDATE goods_images SET file_id = '203953ae8e27e23fbf9fde08626120e9' WHERE goods_id = 434 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '203953ae8e27e23fbf9fde08626120e9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"203953ae8e27e23fbf9fde08626120e9"') WHERE goods_id = 434 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'db4e314ea9b1a33dc06bb6bf1d5e37bc', seller_id, 'GOODS', 1 FROM goods WHERE id = 435;
UPDATE goods_images SET file_id = 'db4e314ea9b1a33dc06bb6bf1d5e37bc' WHERE goods_id = 435 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'db4e314ea9b1a33dc06bb6bf1d5e37bc', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"db4e314ea9b1a33dc06bb6bf1d5e37bc"') WHERE goods_id = 435 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b89cbc168f816982e2285590c98e3c89', seller_id, 'GOODS', 1 FROM goods WHERE id = 436;
UPDATE goods_images SET file_id = 'b89cbc168f816982e2285590c98e3c89' WHERE goods_id = 436 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b89cbc168f816982e2285590c98e3c89', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b89cbc168f816982e2285590c98e3c89"') WHERE goods_id = 436 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8afa4c91fd913e8d9507f05dcb71e2b1', seller_id, 'GOODS', 1 FROM goods WHERE id = 437;
UPDATE goods_images SET file_id = '8afa4c91fd913e8d9507f05dcb71e2b1' WHERE goods_id = 437 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8afa4c91fd913e8d9507f05dcb71e2b1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8afa4c91fd913e8d9507f05dcb71e2b1"') WHERE goods_id = 437 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '6c2528a2db77dc4ec2ad3e486a7328a2', seller_id, 'GOODS', 1 FROM goods WHERE id = 438;
UPDATE goods_images SET file_id = '6c2528a2db77dc4ec2ad3e486a7328a2' WHERE goods_id = 438 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '6c2528a2db77dc4ec2ad3e486a7328a2', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"6c2528a2db77dc4ec2ad3e486a7328a2"') WHERE goods_id = 438 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'c0ddd015c8de15c30989b37d43078200', seller_id, 'GOODS', 1 FROM goods WHERE id = 439;
UPDATE goods_images SET file_id = 'c0ddd015c8de15c30989b37d43078200' WHERE goods_id = 439 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'c0ddd015c8de15c30989b37d43078200', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"c0ddd015c8de15c30989b37d43078200"') WHERE goods_id = 439 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b7f6cdb6db22970696daa0e7f269daeb', seller_id, 'GOODS', 1 FROM goods WHERE id = 440;
UPDATE goods_images SET file_id = 'b7f6cdb6db22970696daa0e7f269daeb' WHERE goods_id = 440 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b7f6cdb6db22970696daa0e7f269daeb', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b7f6cdb6db22970696daa0e7f269daeb"') WHERE goods_id = 440 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e36d6cc2141f0befa3a5f666fb16d220', seller_id, 'GOODS', 1 FROM goods WHERE id = 441;
UPDATE goods_images SET file_id = 'e36d6cc2141f0befa3a5f666fb16d220' WHERE goods_id = 441 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e36d6cc2141f0befa3a5f666fb16d220', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e36d6cc2141f0befa3a5f666fb16d220"') WHERE goods_id = 441 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '0067f2e34f00ed8db35f1725dc5f2c84', seller_id, 'GOODS', 1 FROM goods WHERE id = 442;
UPDATE goods_images SET file_id = '0067f2e34f00ed8db35f1725dc5f2c84' WHERE goods_id = 442 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '0067f2e34f00ed8db35f1725dc5f2c84', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"0067f2e34f00ed8db35f1725dc5f2c84"') WHERE goods_id = 442 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '32d6a514e6dae43e213a838ffe5ace40', seller_id, 'GOODS', 1 FROM goods WHERE id = 443;
UPDATE goods_images SET file_id = '32d6a514e6dae43e213a838ffe5ace40' WHERE goods_id = 443 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '32d6a514e6dae43e213a838ffe5ace40', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"32d6a514e6dae43e213a838ffe5ace40"') WHERE goods_id = 443 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fa5bf05cdd9d86ff888e15a3ee9445c1', seller_id, 'GOODS', 1 FROM goods WHERE id = 444;
UPDATE goods_images SET file_id = 'fa5bf05cdd9d86ff888e15a3ee9445c1' WHERE goods_id = 444 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fa5bf05cdd9d86ff888e15a3ee9445c1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fa5bf05cdd9d86ff888e15a3ee9445c1"') WHERE goods_id = 444 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5a0a2ef65b470addcf7264b6e7f86116', seller_id, 'GOODS', 1 FROM goods WHERE id = 445;
UPDATE goods_images SET file_id = '5a0a2ef65b470addcf7264b6e7f86116' WHERE goods_id = 445 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5a0a2ef65b470addcf7264b6e7f86116', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5a0a2ef65b470addcf7264b6e7f86116"') WHERE goods_id = 445 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '95fbde87ff9031ad58c259fd2718b4e3', seller_id, 'GOODS', 1 FROM goods WHERE id = 446;
UPDATE goods_images SET file_id = '95fbde87ff9031ad58c259fd2718b4e3' WHERE goods_id = 446 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '95fbde87ff9031ad58c259fd2718b4e3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"95fbde87ff9031ad58c259fd2718b4e3"') WHERE goods_id = 446 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '303270a52056967a85064c2ff1d0b3f9', seller_id, 'GOODS', 1 FROM goods WHERE id = 447;
UPDATE goods_images SET file_id = '303270a52056967a85064c2ff1d0b3f9' WHERE goods_id = 447 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '303270a52056967a85064c2ff1d0b3f9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"303270a52056967a85064c2ff1d0b3f9"') WHERE goods_id = 447 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b7d5b764d5e5b6a8be2051db12f72ba9', seller_id, 'GOODS', 1 FROM goods WHERE id = 448;
UPDATE goods_images SET file_id = 'b7d5b764d5e5b6a8be2051db12f72ba9' WHERE goods_id = 448 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b7d5b764d5e5b6a8be2051db12f72ba9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b7d5b764d5e5b6a8be2051db12f72ba9"') WHERE goods_id = 448 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '620f6f809990ba0ef802d80d88901701', seller_id, 'GOODS', 1 FROM goods WHERE id = 449;
UPDATE goods_images SET file_id = '620f6f809990ba0ef802d80d88901701' WHERE goods_id = 449 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '620f6f809990ba0ef802d80d88901701', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"620f6f809990ba0ef802d80d88901701"') WHERE goods_id = 449 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3651c460b43cecc4bce5bfcd07ebed14', seller_id, 'GOODS', 1 FROM goods WHERE id = 450;
UPDATE goods_images SET file_id = '3651c460b43cecc4bce5bfcd07ebed14' WHERE goods_id = 450 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3651c460b43cecc4bce5bfcd07ebed14', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3651c460b43cecc4bce5bfcd07ebed14"') WHERE goods_id = 450 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'cc3650a0adc5bf3e8d6eaa51ddc1d4c1', seller_id, 'GOODS', 1 FROM goods WHERE id = 451;
UPDATE goods_images SET file_id = 'cc3650a0adc5bf3e8d6eaa51ddc1d4c1' WHERE goods_id = 451 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'cc3650a0adc5bf3e8d6eaa51ddc1d4c1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"cc3650a0adc5bf3e8d6eaa51ddc1d4c1"') WHERE goods_id = 451 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '51330fb66761189244d30925c2238e8a', seller_id, 'GOODS', 1 FROM goods WHERE id = 452;
UPDATE goods_images SET file_id = '51330fb66761189244d30925c2238e8a' WHERE goods_id = 452 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '51330fb66761189244d30925c2238e8a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"51330fb66761189244d30925c2238e8a"') WHERE goods_id = 452 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'bf93e85cbf43db716ca87c6e09abda8d', seller_id, 'GOODS', 1 FROM goods WHERE id = 453;
UPDATE goods_images SET file_id = 'bf93e85cbf43db716ca87c6e09abda8d' WHERE goods_id = 453 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'bf93e85cbf43db716ca87c6e09abda8d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"bf93e85cbf43db716ca87c6e09abda8d"') WHERE goods_id = 453 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a648918b2076303c45420d6a22a50a66', seller_id, 'GOODS', 1 FROM goods WHERE id = 454;
UPDATE goods_images SET file_id = 'a648918b2076303c45420d6a22a50a66' WHERE goods_id = 454 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a648918b2076303c45420d6a22a50a66', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a648918b2076303c45420d6a22a50a66"') WHERE goods_id = 454 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9615a78c27f8fc51f4833282fe9041d5', seller_id, 'GOODS', 1 FROM goods WHERE id = 455;
UPDATE goods_images SET file_id = '9615a78c27f8fc51f4833282fe9041d5' WHERE goods_id = 455 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9615a78c27f8fc51f4833282fe9041d5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9615a78c27f8fc51f4833282fe9041d5"') WHERE goods_id = 455 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8a69681ed14cdc6c0f4c7e4949525590', seller_id, 'GOODS', 1 FROM goods WHERE id = 456;
UPDATE goods_images SET file_id = '8a69681ed14cdc6c0f4c7e4949525590' WHERE goods_id = 456 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8a69681ed14cdc6c0f4c7e4949525590', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8a69681ed14cdc6c0f4c7e4949525590"') WHERE goods_id = 456 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '21d48a5b0e071ed7740637d695bfe916', seller_id, 'GOODS', 1 FROM goods WHERE id = 457;
UPDATE goods_images SET file_id = '21d48a5b0e071ed7740637d695bfe916' WHERE goods_id = 457 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '21d48a5b0e071ed7740637d695bfe916', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"21d48a5b0e071ed7740637d695bfe916"') WHERE goods_id = 457 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'e07516f0067f0bbe52f81d1685d2c6d9', seller_id, 'GOODS', 1 FROM goods WHERE id = 458;
UPDATE goods_images SET file_id = 'e07516f0067f0bbe52f81d1685d2c6d9' WHERE goods_id = 458 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'e07516f0067f0bbe52f81d1685d2c6d9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"e07516f0067f0bbe52f81d1685d2c6d9"') WHERE goods_id = 458 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3e7e84e89bfee13848538a7950c17d30', seller_id, 'GOODS', 1 FROM goods WHERE id = 459;
UPDATE goods_images SET file_id = '3e7e84e89bfee13848538a7950c17d30' WHERE goods_id = 459 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3e7e84e89bfee13848538a7950c17d30', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3e7e84e89bfee13848538a7950c17d30"') WHERE goods_id = 459 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '609c3fbf37fdb8f18f452123898a00c8', seller_id, 'GOODS', 1 FROM goods WHERE id = 460;
UPDATE goods_images SET file_id = '609c3fbf37fdb8f18f452123898a00c8' WHERE goods_id = 460 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '609c3fbf37fdb8f18f452123898a00c8', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"609c3fbf37fdb8f18f452123898a00c8"') WHERE goods_id = 460 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5d7a9de3baa13328740bb7299de710b2', seller_id, 'GOODS', 1 FROM goods WHERE id = 461;
UPDATE goods_images SET file_id = '5d7a9de3baa13328740bb7299de710b2' WHERE goods_id = 461 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5d7a9de3baa13328740bb7299de710b2', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5d7a9de3baa13328740bb7299de710b2"') WHERE goods_id = 461 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '86b9e17868ab2a9e5b50964b76d0d50e', seller_id, 'GOODS', 1 FROM goods WHERE id = 462;
UPDATE goods_images SET file_id = '86b9e17868ab2a9e5b50964b76d0d50e' WHERE goods_id = 462 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '86b9e17868ab2a9e5b50964b76d0d50e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"86b9e17868ab2a9e5b50964b76d0d50e"') WHERE goods_id = 462 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '86df67d3d17ae055f9f0153eb053b65b', seller_id, 'GOODS', 1 FROM goods WHERE id = 463;
UPDATE goods_images SET file_id = '86df67d3d17ae055f9f0153eb053b65b' WHERE goods_id = 463 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '86df67d3d17ae055f9f0153eb053b65b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"86df67d3d17ae055f9f0153eb053b65b"') WHERE goods_id = 463 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '99dbe170884ba418d535ba7843e3636a', seller_id, 'GOODS', 1 FROM goods WHERE id = 464;
UPDATE goods_images SET file_id = '99dbe170884ba418d535ba7843e3636a' WHERE goods_id = 464 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '99dbe170884ba418d535ba7843e3636a', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"99dbe170884ba418d535ba7843e3636a"') WHERE goods_id = 464 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5febba9f7efd67418347c86e0e4e8a8d', seller_id, 'GOODS', 1 FROM goods WHERE id = 465;
UPDATE goods_images SET file_id = '5febba9f7efd67418347c86e0e4e8a8d' WHERE goods_id = 465 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5febba9f7efd67418347c86e0e4e8a8d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5febba9f7efd67418347c86e0e4e8a8d"') WHERE goods_id = 465 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a60e72ee6be361673a2c6ea4317cff8e', seller_id, 'GOODS', 1 FROM goods WHERE id = 466;
UPDATE goods_images SET file_id = 'a60e72ee6be361673a2c6ea4317cff8e' WHERE goods_id = 466 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a60e72ee6be361673a2c6ea4317cff8e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a60e72ee6be361673a2c6ea4317cff8e"') WHERE goods_id = 466 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ea06d4d59af1a4c3358dc799826dec00', seller_id, 'GOODS', 1 FROM goods WHERE id = 467;
UPDATE goods_images SET file_id = 'ea06d4d59af1a4c3358dc799826dec00' WHERE goods_id = 467 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ea06d4d59af1a4c3358dc799826dec00', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ea06d4d59af1a4c3358dc799826dec00"') WHERE goods_id = 467 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1c0d56cb009f9489edc7c988e56048b1', seller_id, 'GOODS', 1 FROM goods WHERE id = 468;
UPDATE goods_images SET file_id = '1c0d56cb009f9489edc7c988e56048b1' WHERE goods_id = 468 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1c0d56cb009f9489edc7c988e56048b1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1c0d56cb009f9489edc7c988e56048b1"') WHERE goods_id = 468 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4e40522a66d8cef48f7b732cbc0056a5', seller_id, 'GOODS', 1 FROM goods WHERE id = 469;
UPDATE goods_images SET file_id = '4e40522a66d8cef48f7b732cbc0056a5' WHERE goods_id = 469 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4e40522a66d8cef48f7b732cbc0056a5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4e40522a66d8cef48f7b732cbc0056a5"') WHERE goods_id = 469 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3016073e4bcc9197b3555cf253f256dd', seller_id, 'GOODS', 1 FROM goods WHERE id = 470;
UPDATE goods_images SET file_id = '3016073e4bcc9197b3555cf253f256dd' WHERE goods_id = 470 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3016073e4bcc9197b3555cf253f256dd', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3016073e4bcc9197b3555cf253f256dd"') WHERE goods_id = 470 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '98a784cce2e95ab11c7fab79c1d5332b', seller_id, 'GOODS', 1 FROM goods WHERE id = 471;
UPDATE goods_images SET file_id = '98a784cce2e95ab11c7fab79c1d5332b' WHERE goods_id = 471 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '98a784cce2e95ab11c7fab79c1d5332b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"98a784cce2e95ab11c7fab79c1d5332b"') WHERE goods_id = 471 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '1d39eba26db804e852ed8eb676fc2a4d', seller_id, 'GOODS', 1 FROM goods WHERE id = 472;
UPDATE goods_images SET file_id = '1d39eba26db804e852ed8eb676fc2a4d' WHERE goods_id = 472 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '1d39eba26db804e852ed8eb676fc2a4d', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"1d39eba26db804e852ed8eb676fc2a4d"') WHERE goods_id = 472 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8b5a170ff3303f0207dccc8236308083', seller_id, 'GOODS', 1 FROM goods WHERE id = 473;
UPDATE goods_images SET file_id = '8b5a170ff3303f0207dccc8236308083' WHERE goods_id = 473 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8b5a170ff3303f0207dccc8236308083', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8b5a170ff3303f0207dccc8236308083"') WHERE goods_id = 473 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'b60d76c0f54d878051f68b8b5bd7509e', seller_id, 'GOODS', 1 FROM goods WHERE id = 474;
UPDATE goods_images SET file_id = 'b60d76c0f54d878051f68b8b5bd7509e' WHERE goods_id = 474 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'b60d76c0f54d878051f68b8b5bd7509e', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"b60d76c0f54d878051f68b8b5bd7509e"') WHERE goods_id = 474 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '3b613bf2e251828504999132f240a187', seller_id, 'GOODS', 1 FROM goods WHERE id = 475;
UPDATE goods_images SET file_id = '3b613bf2e251828504999132f240a187' WHERE goods_id = 475 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '3b613bf2e251828504999132f240a187', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"3b613bf2e251828504999132f240a187"') WHERE goods_id = 475 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '40db4cd501aa47335c0d44e096f09847', seller_id, 'GOODS', 1 FROM goods WHERE id = 476;
UPDATE goods_images SET file_id = '40db4cd501aa47335c0d44e096f09847' WHERE goods_id = 476 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '40db4cd501aa47335c0d44e096f09847', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"40db4cd501aa47335c0d44e096f09847"') WHERE goods_id = 476 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '9a9fb67045a36cd02f8312892f75abab', seller_id, 'GOODS', 1 FROM goods WHERE id = 477;
UPDATE goods_images SET file_id = '9a9fb67045a36cd02f8312892f75abab' WHERE goods_id = 477 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '9a9fb67045a36cd02f8312892f75abab', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"9a9fb67045a36cd02f8312892f75abab"') WHERE goods_id = 477 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd717ae21038399f7be6b11c8054e2554', seller_id, 'GOODS', 1 FROM goods WHERE id = 478;
UPDATE goods_images SET file_id = 'd717ae21038399f7be6b11c8054e2554' WHERE goods_id = 478 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd717ae21038399f7be6b11c8054e2554', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d717ae21038399f7be6b11c8054e2554"') WHERE goods_id = 478 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '8f57dfb393f0ddfccc50423031e9dae3', seller_id, 'GOODS', 1 FROM goods WHERE id = 479;
UPDATE goods_images SET file_id = '8f57dfb393f0ddfccc50423031e9dae3' WHERE goods_id = 479 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '8f57dfb393f0ddfccc50423031e9dae3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"8f57dfb393f0ddfccc50423031e9dae3"') WHERE goods_id = 479 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '794d8a81390f56503801ccaa43455b9b', seller_id, 'GOODS', 1 FROM goods WHERE id = 480;
UPDATE goods_images SET file_id = '794d8a81390f56503801ccaa43455b9b' WHERE goods_id = 480 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '794d8a81390f56503801ccaa43455b9b', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"794d8a81390f56503801ccaa43455b9b"') WHERE goods_id = 480 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'd146d0c258fc44edbd5c4a0434adcf27', seller_id, 'GOODS', 1 FROM goods WHERE id = 481;
UPDATE goods_images SET file_id = 'd146d0c258fc44edbd5c4a0434adcf27' WHERE goods_id = 481 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'd146d0c258fc44edbd5c4a0434adcf27', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"d146d0c258fc44edbd5c4a0434adcf27"') WHERE goods_id = 481 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ae76db75774f44bd189b7765ec8e36ff', seller_id, 'GOODS', 1 FROM goods WHERE id = 482;
UPDATE goods_images SET file_id = 'ae76db75774f44bd189b7765ec8e36ff' WHERE goods_id = 482 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ae76db75774f44bd189b7765ec8e36ff', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ae76db75774f44bd189b7765ec8e36ff"') WHERE goods_id = 482 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'fea44f5092ee95b545b452e49705bdc9', seller_id, 'GOODS', 1 FROM goods WHERE id = 483;
UPDATE goods_images SET file_id = 'fea44f5092ee95b545b452e49705bdc9' WHERE goods_id = 483 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'fea44f5092ee95b545b452e49705bdc9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"fea44f5092ee95b545b452e49705bdc9"') WHERE goods_id = 483 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '87d0a2171fe4769270a1aefa77acecc9', seller_id, 'GOODS', 1 FROM goods WHERE id = 484;
UPDATE goods_images SET file_id = '87d0a2171fe4769270a1aefa77acecc9' WHERE goods_id = 484 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '87d0a2171fe4769270a1aefa77acecc9', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"87d0a2171fe4769270a1aefa77acecc9"') WHERE goods_id = 484 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '14328a3961184927c4a6380bed4bd3f5', seller_id, 'GOODS', 1 FROM goods WHERE id = 485;
UPDATE goods_images SET file_id = '14328a3961184927c4a6380bed4bd3f5' WHERE goods_id = 485 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '14328a3961184927c4a6380bed4bd3f5', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"14328a3961184927c4a6380bed4bd3f5"') WHERE goods_id = 485 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '5bb7c917e6a657e877a49cf7ee4b53e8', seller_id, 'GOODS', 1 FROM goods WHERE id = 486;
UPDATE goods_images SET file_id = '5bb7c917e6a657e877a49cf7ee4b53e8' WHERE goods_id = 486 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '5bb7c917e6a657e877a49cf7ee4b53e8', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"5bb7c917e6a657e877a49cf7ee4b53e8"') WHERE goods_id = 486 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '2af2e0fcf62a6a7b56385695aaca8f45', seller_id, 'GOODS', 1 FROM goods WHERE id = 487;
UPDATE goods_images SET file_id = '2af2e0fcf62a6a7b56385695aaca8f45' WHERE goods_id = 487 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '2af2e0fcf62a6a7b56385695aaca8f45', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"2af2e0fcf62a6a7b56385695aaca8f45"') WHERE goods_id = 487 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '11d1a90d2eaf2aac201b9f382cc3184f', seller_id, 'GOODS', 1 FROM goods WHERE id = 488;
UPDATE goods_images SET file_id = '11d1a90d2eaf2aac201b9f382cc3184f' WHERE goods_id = 488 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '11d1a90d2eaf2aac201b9f382cc3184f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"11d1a90d2eaf2aac201b9f382cc3184f"') WHERE goods_id = 488 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4188d13a96da0444708fa52fa344e2e1', seller_id, 'GOODS', 1 FROM goods WHERE id = 489;
UPDATE goods_images SET file_id = '4188d13a96da0444708fa52fa344e2e1' WHERE goods_id = 489 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4188d13a96da0444708fa52fa344e2e1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4188d13a96da0444708fa52fa344e2e1"') WHERE goods_id = 489 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a19a5aaeb0128359e3c9506f2bb13f20', seller_id, 'GOODS', 1 FROM goods WHERE id = 490;
UPDATE goods_images SET file_id = 'a19a5aaeb0128359e3c9506f2bb13f20' WHERE goods_id = 490 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a19a5aaeb0128359e3c9506f2bb13f20', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a19a5aaeb0128359e3c9506f2bb13f20"') WHERE goods_id = 490 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '03216d024b7eaf9ce8f9f1ade5ad043c', seller_id, 'GOODS', 1 FROM goods WHERE id = 491;
UPDATE goods_images SET file_id = '03216d024b7eaf9ce8f9f1ade5ad043c' WHERE goods_id = 491 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '03216d024b7eaf9ce8f9f1ade5ad043c', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"03216d024b7eaf9ce8f9f1ade5ad043c"') WHERE goods_id = 491 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '01cd4769080db2019c21853449ce3ab4', seller_id, 'GOODS', 1 FROM goods WHERE id = 492;
UPDATE goods_images SET file_id = '01cd4769080db2019c21853449ce3ab4' WHERE goods_id = 492 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '01cd4769080db2019c21853449ce3ab4', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"01cd4769080db2019c21853449ce3ab4"') WHERE goods_id = 492 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '01391074188ba6fdc8eb8a3ac2603156', seller_id, 'GOODS', 1 FROM goods WHERE id = 493;
UPDATE goods_images SET file_id = '01391074188ba6fdc8eb8a3ac2603156' WHERE goods_id = 493 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '01391074188ba6fdc8eb8a3ac2603156', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"01391074188ba6fdc8eb8a3ac2603156"') WHERE goods_id = 493 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '4a5d119305f07dcb3c6b8a4192d0775f', seller_id, 'GOODS', 1 FROM goods WHERE id = 494;
UPDATE goods_images SET file_id = '4a5d119305f07dcb3c6b8a4192d0775f' WHERE goods_id = 494 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '4a5d119305f07dcb3c6b8a4192d0775f', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"4a5d119305f07dcb3c6b8a4192d0775f"') WHERE goods_id = 494 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'ba232c35d0373b7b0dfaa1652af25e23', seller_id, 'GOODS', 1 FROM goods WHERE id = 495;
UPDATE goods_images SET file_id = 'ba232c35d0373b7b0dfaa1652af25e23' WHERE goods_id = 495 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'ba232c35d0373b7b0dfaa1652af25e23', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"ba232c35d0373b7b0dfaa1652af25e23"') WHERE goods_id = 495 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'a5f891dcfd42006428c617b94621e6f3', seller_id, 'GOODS', 1 FROM goods WHERE id = 496;
UPDATE goods_images SET file_id = 'a5f891dcfd42006428c617b94621e6f3' WHERE goods_id = 496 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'a5f891dcfd42006428c617b94621e6f3', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"a5f891dcfd42006428c617b94621e6f3"') WHERE goods_id = 496 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT 'f1821ea5139354f61d859267919621a1', seller_id, 'GOODS', 1 FROM goods WHERE id = 497;
UPDATE goods_images SET file_id = 'f1821ea5139354f61d859267919621a1' WHERE goods_id = 497 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', 'f1821ea5139354f61d859267919621a1', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"f1821ea5139354f61d859267919621a1"') WHERE goods_id = 497 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '57bb34e2fa53cd256f91803a0caf71e6', seller_id, 'GOODS', 1 FROM goods WHERE id = 498;
UPDATE goods_images SET file_id = '57bb34e2fa53cd256f91803a0caf71e6' WHERE goods_id = 498 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '57bb34e2fa53cd256f91803a0caf71e6', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"57bb34e2fa53cd256f91803a0caf71e6"') WHERE goods_id = 498 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
INSERT INTO stored_files (id, owner_id, purpose, public_access)
SELECT '369a9ebb37146170a02d24c990e5e6ff', seller_id, 'GOODS', 1 FROM goods WHERE id = 499;
UPDATE goods_images SET file_id = '369a9ebb37146170a02d24c990e5e6ff' WHERE goods_id = 499 AND file_id = 'demo-cover';
UPDATE orders SET goods_cover_file_id = IF(goods_cover_file_id = 'demo-cover', '369a9ebb37146170a02d24c990e5e6ff', goods_cover_file_id), goods_images_json = REPLACE(goods_images_json, '"demo-cover"', '"369a9ebb37146170a02d24c990e5e6ff"') WHERE goods_id = 499 AND (goods_cover_file_id = 'demo-cover' OR goods_images_json LIKE '%"demo-cover"%');
COMMIT;

-- 核对：第一行应是 499，第二行里 1 到 499 的占位图应为 0。
SELECT COUNT(*) AS replaced_images FROM goods_images WHERE goods_id BETWEEN 1 AND 499 AND file_id <> 'demo-cover';
SELECT COUNT(*) AS still_placeholder FROM goods_images WHERE goods_id BETWEEN 1 AND 499 AND file_id = 'demo-cover';
