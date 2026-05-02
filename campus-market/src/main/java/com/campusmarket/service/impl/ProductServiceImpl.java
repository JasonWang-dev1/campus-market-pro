package com.campusmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campusmarket.common.BusinessException;
import com.campusmarket.common.ResultCode;
import com.campusmarket.dto.ProductDTO;
import com.campusmarket.entity.Product;
import com.campusmarket.entity.User;
import com.campusmarket.mapper.ProductMapper;
import com.campusmarket.mapper.UserMapper;
import com.campusmarket.service.ProductService;
import com.campusmarket.vo.ProductVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product createProduct(ProductDTO dto, Long sellerId) {
        Product product = new Product();
        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setImages(dto.getImages());
        product.setCategory(dto.getCategory());
        product.setStatus(0);
        product.setSellerId(sellerId);
        product.setViewCount(0);

        productMapper.insert(product);
        return product;
    }

    @Override
    public ProductVO getProductById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }

        // 浏览次数 +1
        product.setViewCount(product.getViewCount() == null ? 1 : product.getViewCount() + 1);
        productMapper.updateById(product);

        return toProductVO(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Product updateProduct(Long id, ProductDTO dto, Long userId) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }
        if (!product.getSellerId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能修改自己的商品");
        }

        product.setTitle(dto.getTitle());
        product.setDescription(dto.getDescription());
        product.setPrice(dto.getPrice());
        product.setOriginalPrice(dto.getOriginalPrice());
        product.setImages(dto.getImages());
        product.setCategory(dto.getCategory());

        productMapper.updateById(product);
        return product;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id, Long userId) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }
        if (!product.getSellerId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能删除自己的商品");
        }
        productMapper.deleteById(id);
    }

    @Override
    public IPage<ProductVO> pageProducts(int pageNum, int pageSize, String category, String keyword) {
        Page<Product> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 0)
                .eq(StringUtils.hasText(category), Product::getCategory, category)
                .and(StringUtils.hasText(keyword), w -> w
                        .like(Product::getTitle, keyword)
                        .or()
                        .like(Product::getDescription, keyword))
                .orderByDesc(Product::getCreateTime);

        IPage<Product> productPage = productMapper.selectPage(page, wrapper);

        // 转为 VO
        return productPage.convert(this::toProductVO);
    }

    /**
     * Product -> ProductVO
     */
    private ProductVO toProductVO(Product product) {
        ProductVO vo = new ProductVO();
        vo.setId(product.getId());
        vo.setTitle(product.getTitle());
        vo.setDescription(product.getDescription());
        vo.setPrice(product.getPrice());
        vo.setOriginalPrice(product.getOriginalPrice());
        vo.setImages(product.getImages());
        vo.setCategory(product.getCategory());
        vo.setStatus(product.getStatus());
        vo.setSellerId(product.getSellerId());
        vo.setViewCount(product.getViewCount());
        vo.setCreateTime(product.getCreateTime());

        // 填充卖家信息
        User seller = userMapper.selectById(product.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname());
            vo.setSellerAvatar(seller.getAvatar());
        }
        return vo;
    }
}
