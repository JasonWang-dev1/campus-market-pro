package com.campusmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campusmarket.common.BusinessException;
import com.campusmarket.common.ResultCode;
import com.campusmarket.dto.OrderDTO;
import com.campusmarket.entity.Order;
import com.campusmarket.entity.Product;
import com.campusmarket.entity.User;
import com.campusmarket.mapper.OrderMapper;
import com.campusmarket.mapper.ProductMapper;
import com.campusmarket.mapper.UserMapper;
import com.campusmarket.service.OrderService;
import com.campusmarket.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order createOrder(OrderDTO dto, Long buyerId) {
        // 校验商品
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "商品不存在");
        }
        if (product.getStatus() != 0) {
            throw new BusinessException(ResultCode.FAILED, "商品已下架或已售出");
        }
        if (product.getSellerId().equals(buyerId)) {
            throw new BusinessException(ResultCode.FAILED, "不能购买自己的商品");
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setProductId(dto.getProductId());
        order.setBuyerId(buyerId);
        order.setSellerId(product.getSellerId());
        order.setTotalPrice(product.getPrice().multiply(java.math.BigDecimal.valueOf(dto.getQuantity())));
        order.setQuantity(dto.getQuantity());
        order.setStatus(0);
        order.setMessage(dto.getMessage());

        orderMapper.insert(order);

        // 商品标记为已卖出
        product.setStatus(2);
        productMapper.updateById(product);

        return order;
    }

    @Override
    public OrderVO getOrderById(Long id, Long userId) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        // 校验权限：买家或卖家可查看
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权查看该订单");
        }
        return toOrderVO(order);
    }

    @Override
    public List<OrderVO> listBuyerOrders(Long buyerId) {
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getBuyerId, buyerId)
                        .orderByDesc(Order::getCreateTime));
        return orders.stream().map(this::toOrderVO).collect(Collectors.toList());
    }

    @Override
    public List<OrderVO> listSellerOrders(Long sellerId) {
        List<Order> orders = orderMapper.selectList(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getSellerId, sellerId)
                        .orderByDesc(Order::getCreateTime));
        return orders.stream().map(this::toOrderVO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id, Long userId) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能取消自己的订单");
        }
        if (order.getStatus() != 0) {
            throw new BusinessException(ResultCode.FAILED, "当前订单状态不可取消");
        }
        order.setStatus(4);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceived(Long id, Long userId) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "订单不存在");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只能确认自己的订单");
        }
        if (order.getStatus() != 2) {
            throw new BusinessException(ResultCode.FAILED, "当前订单状态不可确认收货");
        }
        order.setStatus(3);
        orderMapper.updateById(order);
    }

    // ========== 私有方法 ==========

    /**
     * Order -> OrderVO
     */
    private OrderVO toOrderVO(Order order) {
        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setProductId(order.getProductId());
        vo.setBuyerId(order.getBuyerId());
        vo.setSellerId(order.getSellerId());
        vo.setTotalPrice(order.getTotalPrice());
        vo.setQuantity(order.getQuantity());
        vo.setStatus(order.getStatus());
        vo.setMessage(order.getMessage());
        vo.setCreateTime(order.getCreateTime());

        // 商品信息
        Product product = productMapper.selectById(order.getProductId());
        if (product != null) {
            vo.setProductTitle(product.getTitle());
            vo.setProductPrice(product.getPrice());
            // 取第一张图作为首图
            String images = product.getImages();
            if (images != null && !images.isBlank()) {
                String firstImage = images.replaceAll("[\\[\\]\"]", "").split(",")[0].trim();
                vo.setProductImage(firstImage);
            }
        }

        // 买家信息
        User buyer = userMapper.selectById(order.getBuyerId());
        if (buyer != null) {
            vo.setBuyerName(buyer.getNickname());
        }

        // 卖家信息
        User seller = userMapper.selectById(order.getSellerId());
        if (seller != null) {
            vo.setSellerName(seller.getNickname());
        }

        return vo;
    }

    /**
     * 生成订单号：yyyyMMdd + 8位随机数字
     */
    private String generateOrderNo() {
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int randomPart = new Random().nextInt(100000000);
        return datePart + String.format("%08d", randomPart);
    }
}
