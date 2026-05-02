package com.campusmarket.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品响应
 */
@Data
public class ProductVO {

    private Long id;

    private String title;

    private String description;

    private BigDecimal price;

    private BigDecimal originalPrice;

    private String images;

    private String category;

    /**
     * 状态 0-在售 1-下架 2-已卖出
     */
    private Integer status;

    private Long sellerId;

    /**
     * 卖家昵称
     */
    private String sellerName;

    /**
     * 卖家头像
     */
    private String sellerAvatar;

    /**
     * 浏览次数
     */
    private Integer viewCount;

    private LocalDateTime createTime;
}
