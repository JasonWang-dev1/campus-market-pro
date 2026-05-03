package com.campusmarket.agent.tools;

import com.campusmarket.agent.Tool;
import com.campusmarket.common.BusinessException;
import com.campusmarket.dto.ProductDTO;
import com.campusmarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 商品发布工具
 * <p>
 * 根据用户提供的参数自动发布商品。
 * 发布前校验必要参数，返回发布结果。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductPublishTool implements Tool {

    private final ProductService productService;

    @Override
    public String getName() {
        return "publish_product";
    }

    @Override
    public String getDescription() {
        return "发布一个二手商品到平台，需要提供标题、价格、分类等信息";
    }

    @Override
    public String getParameterDescription() {
        return """
                {
                  "title": "商品标题（必填）",
                  "price": "价格（必填，数字）",
                  "category": "商品分类（必填，如：数码/书籍/生活用品/服饰/体育/其他）",
                  "description": "商品描述（可选）",
                  "images": "图片URL（可选，多个用逗号分隔）",
                  "original_price": "原价（可选）",
                  "seller_id": "卖家用户ID（必填）"
                }""";
    }

    @Override
    public String execute(Map<String, Object> params) {
        String title = paramStr(params, "title", "");
        if (title.isBlank()) {
            throw new BusinessException("商品标题不能为空");
        }

        BigDecimal price = paramDecimal(params, "price");
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("商品价格必须大于0");
        }

        String category = paramStr(params, "category", "");
        if (category.isBlank()) {
            throw new BusinessException("商品分类不能为空");
        }

        Long sellerId = paramLong(params, "seller_id");
        if (sellerId == null) {
            throw new BusinessException("卖家ID不能为空");
        }

        ProductDTO dto = new ProductDTO();
        dto.setTitle(title);
        dto.setPrice(price);
        dto.setCategory(category);
        dto.setDescription(paramStr(params, "description", ""));
        dto.setImages(paramStr(params, "images", null));
        dto.setOriginalPrice(paramDecimal(params, "original_price"));

        log.info("Agent 发布商品: title={}, price={}, category={}, sellerId={}",
                title, price, category, sellerId);

        var product = productService.createProduct(dto, sellerId);

        return String.format("""
                商品发布成功！
                【ID】%d
                【标题】%s
                【价格】¥%.2f
                【分类】%s
                """, product.getId(), product.getTitle(), product.getPrice(), product.getCategory());
    }

    // ========== 参数提取工具 ==========

    private String paramStr(Map<String, Object> params, String key, String defaultValue) {
        Object val = params.get(key);
        return val != null ? val.toString() : defaultValue;
    }

    private BigDecimal paramDecimal(Map<String, Object> params, String key) {
        Object val = params.get(key);
        if (val instanceof Number n) return BigDecimal.valueOf(n.doubleValue());
        if (val instanceof String s) {
            try {
                return new BigDecimal(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Long paramLong(Map<String, Object> params, String key) {
        Object val = params.get(key);
        if (val instanceof Number n) return n.longValue();
        if (val instanceof String s) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
}
