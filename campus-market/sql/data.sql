-- ========================================
-- Campus Market - Seed Data
-- Password for all users: 123456
-- ========================================

USE campus_market;

-- Clear existing data
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `order`;
TRUNCATE TABLE `product`;
TRUNCATE TABLE `user`;
SET FOREIGN_KEY_CHECKS = 1;

INSERT INTO `user` (`username`, `password`, `nickname`, `email`, `phone`, `avatar`, `role`, `status`, `credit_score`, `product_count`) VALUES
('admin',    '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PsLJfGqKCQ2E2MN.dWOm.', 'admin',    'admin@campus.edu',    '13800000001', 'https://api.dicebear.com/7.x/avataaars/svg?seed=admin',    1, 0, 5.00, 0),
('zhangsan', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PsLJfGqKCQ2E2MN.dWOm.', 'zhangsan', 'zhangsan@campus.edu', '13800000002', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhangsan', 0, 0, 4.80, 5),
('lisi',     '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PsLJfGqKCQ2E2MN.dWOm.', 'lisi',     'lisi@campus.edu',     '13800000003', 'https://api.dicebear.com/7.x/avataaars/svg?seed=lisi',     0, 0, 4.90, 4),
('wangwu',   '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PsLJfGqKCQ2E2MN.dWOm.', 'wangwu',   'wangwu@campus.edu',   '13800000004', 'https://api.dicebear.com/7.x/avataaars/svg?seed=wangwu',   0, 0, 4.50, 4),
('zhaoliu',  '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PsLJfGqKCQ2E2MN.dWOm.', 'zhaoliu',  'zhaoliu@campus.edu',  '13800000005', 'https://api.dicebear.com/7.x/avataaars/svg?seed=zhaoliu',  0, 0, 4.70, 3),
('sunqi',    '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36PsLJfGqKCQ2E2MN.dWOm.', 'sunqi',    'sunqi@campus.edu',    '13800000006', 'https://api.dicebear.com/7.x/avataaars/svg?seed=sunqi',    0, 0, 4.60, 3);

-- Electronics (seller_id=2)
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('iPhone 14 Pro 256G 暗紫色', '自用一年，无磕碰，电池健康 92%，全套配件齐全，可当面验机。', 5200.00, 8999.00, '["https://images.unsplash.com/photo-1663499454471-41d445198a42?w=400"]', '数码', 0, 2, 156);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('iPad Air 5 64G WiFi 星光色', '考研结束用不到了，配 Apple Pencil 二代和键盘，整套出。', 3100.00, 4799.00, '["https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400"]', '数码', 0, 2, 98);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('AirPods Pro 2 代', '买了半年，降噪效果很好，因为换了头戴式耳机所以出。', 950.00, 1899.00, '["https://images.unsplash.com/photo-1606220945770-b5b6c2c55bf1?w=400"]', '数码', 0, 2, 67);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('Sony WH-1000XM5 头戴式耳机', '黑色，成色 95 新，降噪天花板，续航 30 小时。', 1600.00, 2999.00, '["https://images.unsplash.com/photo-1546435770-a3e426bf472b?w=400"]', '数码', 0, 2, 45);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('小米移动电源 20000mAh', '支持 50W 快充，外观有轻微使用痕迹，功能完好。', 85.00, 199.00, '["https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=400"]', '数码', 0, 2, 32);

-- Books (seller_id=3)
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('高等数学（同济第七版）上下册', '只翻过几页，基本全新，重点章节有少量铅笔标注。', 25.00, 68.00, '["https://images.unsplash.com/photo-1544947950-fa07a98d237f?w=400"]', '书籍', 0, 3, 210);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('数据结构与算法分析（C语言版）', '计算机专业经典教材，有课堂笔记和重点标注，考研必备。', 18.00, 45.00, '["https://images.unsplash.com/photo-1532012197267-da84d127e765?w=400"]', '书籍', 0, 3, 134);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('大学英语四级真题集（2020-2024）', '包含 10 套真题 + 详细解析，听力扫码可听，答案有标注。', 15.00, 39.80, '["https://images.unsplash.com/photo-1456513080510-7bf3a84b82f8?w=400"]', '书籍', 0, 3, 88);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('线性代数及其应用（第五版）', '英文原版教材，适合想提升数学英语的同学，内有少量笔记。', 30.00, 79.00, '["https://images.unsplash.com/photo-1497633762265-9d179a990aa6?w=400"]', '书籍', 0, 3, 56);

-- Daily Supplies (seller_id=4)
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('宜家 KALLAX 书架 白色', '宿舍用的置物架，毕业搬不走，八成新，自提。', 60.00, 149.00, '["https://images.unsplash.com/photo-1594620302200-9a762244a156?w=400"]', '生活用品', 0, 4, 78);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('美的电热水壶 1.5L', '用了一个学期，内胆无水垢，烧水速度快。', 25.00, 79.00, '["https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=400"]', '生活用品', 0, 4, 42);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('小米台灯 Pro', '护眼台灯，可调亮度和色温，适合学习和阅读。', 75.00, 169.00, '["https://images.unsplash.com/photo-1507473885765-e6ed057ab6fe?w=400"]', '生活用品', 0, 4, 55);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('不锈钢保温杯 500ml', '虎牌保温杯，保温效果 12 小时以上，有使用痕迹。', 45.00, 129.00, '["https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400"]', '生活用品', 0, 4, 38);

-- Clothing (seller_id=5)
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('Nike Air Force 1 白色 42 码', '穿了两次，鞋底无磨损，因为买小了半码所以出。', 450.00, 799.00, '["https://images.unsplash.com/photo-1600269452121-4f2416e55c28?w=400"]', '服饰', 0, 5, 123);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('优衣库轻薄羽绒服 L 码', '黑色，去年冬天买的，洗过一次，保暖性好。', 150.00, 499.00, '["https://images.unsplash.com/photo-1544022613-e87ca75a784a?w=400"]', '服饰', 0, 5, 67);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('Herschel 双肩包 经典款', '深蓝色，容量大，适合上课和短途出行，有轻微磨损。', 120.00, 359.00, '["https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400"]', '服饰', 0, 5, 89);

-- Sports (seller_id=6)
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('李宁羽毛球拍 风刃 900C', '4U 重量，适合进攻型选手，送拍包和一桶球。', 280.00, 599.00, '["https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=400"]', '体育', 0, 6, 76);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('迪卡侬跑步鞋 43 码', '穿了一个月，鞋面干净，适合日常跑步和健身。', 80.00, 249.00, '["https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400"]', '体育', 0, 6, 54);
INSERT INTO `product` (`title`, `description`, `price`, `original_price`, `images`, `category`, `status`, `seller_id`, `view_count`) VALUES ('瑜伽垫 加厚 10mm 紫色', '只用过几次，附带收纳袋和绑带。', 30.00, 89.00, '["https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?w=400"]', '体育', 0, 6, 33);

-- Orders
INSERT INTO `order` (`order_no`, `product_id`, `buyer_id`, `seller_id`, `total_price`, `quantity`, `status`, `message`) VALUES ('ORD202604010001', 1, 3, 2, 5200.00, 1, 3, '希望尽快发货，谢谢');
INSERT INTO `order` (`order_no`, `product_id`, `buyer_id`, `seller_id`, `total_price`, `quantity`, `status`, `message`) VALUES ('ORD202604020002', 6, 4, 3, 25.00, 1, 3, '教材用书，请包好');
INSERT INTO `order` (`order_no`, `product_id`, `buyer_id`, `seller_id`, `total_price`, `quantity`, `status`, `message`) VALUES ('ORD202604030003', 10, 5, 4, 60.00, 1, 2, '周末自提可以吗');
INSERT INTO `order` (`order_no`, `product_id`, `buyer_id`, `seller_id`, `total_price`, `quantity`, `status`, `message`) VALUES ('ORD202604040004', 14, 6, 5, 450.00, 1, 1, '确认是全新吗');
INSERT INTO `order` (`order_no`, `product_id`, `buyer_id`, `seller_id`, `total_price`, `quantity`, `status`, `message`) VALUES ('ORD202604050005', 17, 2, 6, 280.00, 1, 0, '拍下付款了');
INSERT INTO `order` (`order_no`, `product_id`, `buyer_id`, `seller_id`, `total_price`, `quantity`, `status`, `message`) VALUES ('ORD202604060006', 7, 2, 3, 18.00, 1, 4, '已不需要，取消订单');
