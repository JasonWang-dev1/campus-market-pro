package com.campusmarket.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * 创建订单请求
 */
@Data
public class OrderDTO {

    @NotNull(message = "商品ID不能为空")
    private Long productId;

    @NotNull(message = "数量不能为空")
    @Positive(message = "数量必须大于0")
    private Integer quantity;

    /**
     * 买家留言
     */
    private String message;
}
