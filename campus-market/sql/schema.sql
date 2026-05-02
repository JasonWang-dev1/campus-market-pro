-- ========================================
-- 校园二手交易平台 - 数据库初始化脚本
-- ========================================

CREATE DATABASE IF NOT EXISTS campus_market
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE campus_market;

-- ========================================
-- 用户表
-- ========================================
CREATE TABLE IF NOT EXISTS `user`
(
    `id`            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`      VARCHAR(50)   NOT NULL COMMENT '用户名',
    `password`      VARCHAR(255)  NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname`      VARCHAR(50)   DEFAULT NULL COMMENT '昵称',
    `email`         VARCHAR(100)  DEFAULT NULL COMMENT '邮箱',
    `phone`         VARCHAR(20)   DEFAULT NULL COMMENT '手机号',
    `avatar`        VARCHAR(500)  DEFAULT NULL COMMENT '头像URL',
    `role`          TINYINT       DEFAULT 0 COMMENT '角色：0-普通用户 1-管理员',
    `status`        TINYINT       DEFAULT 0 COMMENT '状态：0-正常 1-禁用',
    `credit_score`  DECIMAL(3, 2) DEFAULT 5.00 COMMENT '信用评分',
    `product_count` INT           DEFAULT 0 COMMENT '发布商品数量',
    `create_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表';

-- ========================================
-- 商品表
-- ========================================
CREATE TABLE IF NOT EXISTS `product`
(
    `id`             BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `title`          VARCHAR(200)  NOT NULL COMMENT '商品标题',
    `description`    TEXT          DEFAULT NULL COMMENT '商品描述',
    `price`          DECIMAL(10, 2) NOT NULL COMMENT '价格',
    `original_price` DECIMAL(10, 2) DEFAULT NULL COMMENT '原价',
    `images`         VARCHAR(2000) DEFAULT NULL COMMENT '图片（JSON数组）',
    `category`       VARCHAR(50)   DEFAULT NULL COMMENT '分类',
    `status`         TINYINT       DEFAULT 0 COMMENT '状态：0-在售 1-下架 2-已卖出',
    `seller_id`      BIGINT        NOT NULL COMMENT '卖家ID',
    `view_count`     INT           DEFAULT 0 COMMENT '浏览次数',
    `create_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_product_seller` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='商品表';

-- ========================================
-- 订单表（order 为 MySQL 保留字，需加反引号）
-- ========================================
CREATE TABLE IF NOT EXISTS `order`
(
    `id`           BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no`     VARCHAR(32)   NOT NULL COMMENT '订单编号',
    `product_id`   BIGINT        NOT NULL COMMENT '商品ID',
    `buyer_id`     BIGINT        NOT NULL COMMENT '买家ID',
    `seller_id`    BIGINT        NOT NULL COMMENT '卖家ID',
    `total_price`  DECIMAL(10, 2) NOT NULL COMMENT '总价',
    `quantity`     INT           DEFAULT 1 COMMENT '数量',
    `status`       TINYINT       DEFAULT 0 COMMENT '状态：0-待付款 1-待发货 2-待收货 3-已完成 4-已取消',
    `message`      VARCHAR(500)  DEFAULT NULL COMMENT '买家留言',
    `create_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME      DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_buyer_id` (`buyer_id`),
    KEY `idx_seller_id` (`seller_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`),
    CONSTRAINT `fk_order_buyer` FOREIGN KEY (`buyer_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_order_seller` FOREIGN KEY (`seller_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_order_product` FOREIGN KEY (`product_id`) REFERENCES `product` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='订单表';
