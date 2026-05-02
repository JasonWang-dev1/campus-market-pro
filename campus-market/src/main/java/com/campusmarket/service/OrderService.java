package com.campusmarket.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.campusmarket.dto.OrderDTO;
import com.campusmarket.entity.Order;
import com.campusmarket.vo.OrderVO;

import java.util.List;

public interface OrderService {

    /**
     * 创建订单
     */
    Order createOrder(OrderDTO dto, Long buyerId);

    /**
     * 查询订单详情
     */
    OrderVO getOrderById(Long id, Long userId);

    /**
     * 买家订单列表
     */
    List<OrderVO> listBuyerOrders(Long buyerId);

    /**
     * 卖家订单列表
     */
    List<OrderVO> listSellerOrders(Long sellerId);

    /**
     * 取消订单
     */
    void cancelOrder(Long id, Long userId);

    /**
     * 确认收货
     */
    void confirmReceived(Long id, Long userId);
}
