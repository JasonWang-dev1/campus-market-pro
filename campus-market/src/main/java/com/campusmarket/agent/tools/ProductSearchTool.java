package com.campusmarket.agent.tools;

import com.campusmarket.agent.Tool;
import com.campusmarket.service.ProductService;
import com.campusmarket.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品搜索工具
 * <p>
 * 根据关键词和分类搜索商品，返回格式化的结果列表。
 * </p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductSearchTool implements Tool {

    private final ProductService productService;

    @Override
    public String getName() {
        return "search_products";
    }

    @Override
    public String getDescription() {
        return "根据关键词、分类等条件搜索商品，返回商品列表";
    }

    @Override
    public String getParameterDescription() {
        return """
                {
                  "keyword": "搜索关键词（必填）",
                  "category": "商品分类（可选，如：数码/书籍/生活用品）",
                  "page": "页码（可选，默认1）",
                  "size": "每页数量（可选，默认10）"
                }""";
    }

    @Override
    public String execute(Map<String, Object> params) {
        String keyword = paramStr(params, "keyword", "");
        String category = paramStr(params, "category", null);
        int page = paramInt(params, "page", 1);
        int size = paramInt(params, "size", 10);

        log.debug("商品搜索: keyword={}, category={}, page={}, size={}", keyword, category, page, size);

        var pageResult = productService.pageProducts(page, size, category, keyword);
        var records = pageResult.getRecords();

        if (records.isEmpty()) {
            return "未找到匹配的商品";
        }

        String productList = records.stream()
                .limit(10)
                .map(this::formatProduct)
                .collect(Collectors.joining("\n---\n"));

        return String.format("找到 %d 件商品（显示前 %d 件）:\n\n%s",
                pageResult.getTotal(),
                Math.min(records.size(), 10),
                productList);
    }

    private String formatProduct(ProductVO vo) {
        return String.format("""
                        【ID】%d
                        【标题】%s
                        【价格】¥%.2f
                        【分类】%s
                        【卖家】%s
                        【浏览量】%d
                        """,
                vo.getId(),
                vo.getTitle(),
                vo.getPrice(),
                vo.getCategory() != null ? vo.getCategory() : "未分类",
                vo.getSellerName() != null ? vo.getSellerName() : "未知",
                vo.getViewCount() != null ? vo.getViewCount() : 0);
    }

    // ========== 参数提取工具 ==========

    private String paramStr(Map<String, Object> params, String key, String defaultValue) {
        Object val = params.get(key);
        return val != null ? val.toString() : defaultValue;
    }

    private int paramInt(Map<String, Object> params, String key, int defaultValue) {
        Object val = params.get(key);
        if (val instanceof Number n) return n.intValue();
        if (val instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
