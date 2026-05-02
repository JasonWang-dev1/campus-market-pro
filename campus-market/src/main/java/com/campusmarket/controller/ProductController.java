package com.campusmarket.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campusmarket.common.BusinessException;
import com.campusmarket.common.Result;
import com.campusmarket.common.ResultCode;
import com.campusmarket.dto.ProductDTO;
import com.campusmarket.entity.Product;
import com.campusmarket.service.ProductService;
import com.campusmarket.vo.ProductVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public Result<Product> create(@Valid @RequestBody ProductDTO dto) {
        Long userId = getCurrentUserId();
        Product product = productService.createProduct(dto, userId);
        return Result.success(product);
    }

    @GetMapping("/{id}")
    public Result<ProductVO> detail(@PathVariable Long id) {
        ProductVO vo = productService.getProductById(id);
        return Result.success(vo);
    }

    @PutMapping("/{id}")
    public Result<Product> update(@PathVariable Long id,
                                  @Valid @RequestBody ProductDTO dto) {
        Long userId = getCurrentUserId();
        Product product = productService.updateProduct(id, dto, userId);
        return Result.success(product);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        productService.deleteProduct(id, userId);
        return Result.success();
    }

    @GetMapping
    public Result<IPage<ProductVO>> page(@RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int size,
                                         @RequestParam(required = false) String category,
                                         @RequestParam(required = false) String keyword) {
        IPage<ProductVO> pageResult = productService.pageProducts(page, size, category, keyword);
        return Result.success(pageResult);
    }

    // ========== 私有方法 ==========

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return Long.valueOf(authentication.getPrincipal().toString());
    }
}
