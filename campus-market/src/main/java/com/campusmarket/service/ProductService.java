package com.campusmarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campusmarket.dto.ProductDTO;
import com.campusmarket.entity.Product;
import com.campusmarket.vo.ProductVO;

public interface ProductService {

    /**
     * 发布商品
     */
    Product createProduct(ProductDTO dto, Long sellerId);

    /**
     * 查询商品详情（含卖家信息）
     */
    ProductVO getProductById(Long id);

    /**
     * 更新商品
     */
    Product updateProduct(Long id, ProductDTO dto, Long userId);

    /**
     * 删除商品
     */
    void deleteProduct(Long id, Long userId);

    /**
     * 分页查询商品
     */
    IPage<ProductVO> pageProducts(int pageNum, int pageSize, String category, String keyword);
}
