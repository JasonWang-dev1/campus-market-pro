package com.campusmarket.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单响应
 */
@Data
public class OrderVO {

    private Long id;

    private String orderNo;

    private Long productId;

    /**
     * 商品标题
     */
    private String productTitle;

    /**
     * 商品图片（首图）
     */
    private String productImage;

    /**
     * 商品价格
     */
    private BigDecimal productPrice;

    private Long buyerId;

    /**
     * 买家昵称
     */
    private String buyerName;

    private Long sellerId;

    /**
     * 卖家昵称
     */
    private String sellerName;

    private BigDecimal totalPrice;

    private Integer quantity;

    /**
     * 状态 0-待付款 1-待发货 2-待收货 3-已完成 4-已取消
     */
    private Integer status;

    private String message;

    private LocalDateTime createTime;
}
