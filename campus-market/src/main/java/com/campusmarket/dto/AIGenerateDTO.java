package com.campusmarket.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 生成请求
 */
@Data
public class AIGenerateDTO {

    @NotBlank(message = "商品标题不能为空")
    private String title;

    /**
     * 额外要求（可选）
     */
    private String requirements;
}
