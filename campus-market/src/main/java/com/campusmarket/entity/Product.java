package com.campusmarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体
 */
@Data
@TableName("product")
public class Product {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品描述
     */
    private String description;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 原价
     */
    private BigDecimal originalPrice;

    /**
     * 图片（JSON数组字符串）
     */
    private String images;

    /**
     * 分类
     */
    private String category;

    /**
     * 状态 0-在售 1-下架 2-已卖出
     */
    private Integer status;

    /**
     * 卖家ID
     */
    private Long sellerId;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
