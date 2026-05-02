package com.campusmarket.controller;

import com.campusmarket.common.BusinessException;
import com.campusmarket.common.Result;
import com.campusmarket.common.ResultCode;
import com.campusmarket.dto.OrderDTO;
import com.campusmarket.entity.Order;
import com.campusmarket.service.OrderService;
import com.campusmarket.vo.OrderVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<Order> create(@Valid @RequestBody OrderDTO dto) {
        Long buyerId = getCurrentUserId();
        Order order = orderService.createOrder(dto, buyerId);
        return Result.success(order);
    }

    @GetMapping("/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        OrderVO vo = orderService.getOrderById(id, userId);
        return Result.success(vo);
    }

    @GetMapping("/buyer")
    public Result<List<OrderVO>> buyerOrders() {
        Long buyerId = getCurrentUserId();
        List<OrderVO> list = orderService.listBuyerOrders(buyerId);
        return Result.success(list);
    }

    @GetMapping("/seller")
    public Result<List<OrderVO>> sellerOrders() {
        Long sellerId = getCurrentUserId();
        List<OrderVO> list = orderService.listSellerOrders(sellerId);
        return Result.success(list);
    }

    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        orderService.cancelOrder(id, userId);
        return Result.success();
    }

    @PutMapping("/{id}/confirm")
    public Result<Void> confirm(@PathVariable Long id) {
        Long userId = getCurrentUserId();
        orderService.confirmReceived(id, userId);
        return Result.success();
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
