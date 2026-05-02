package com.campusmarket.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 商品发布/编辑请求
 */
@Data
public class ProductDTO {

    @NotBlank(message = "商品标题不能为空")
    private String title;

    private String description;

    @NotNull(message = "价格不能为空")
    @Positive(message = "价格必须大于0")
    private BigDecimal price;

    private BigDecimal originalPrice;

    /**
     * 图片（JSON数组字符串）
     */
    private String images;

    @NotBlank(message = "商品分类不能为空")
    private String category;
}
