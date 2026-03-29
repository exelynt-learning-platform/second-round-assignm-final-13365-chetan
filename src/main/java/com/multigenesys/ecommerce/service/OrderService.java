package com.multigenesys.ecommerce.service;

import com.multigenesys.ecommerce.dto.order.CreateOrderRequest;
import com.multigenesys.ecommerce.entity.Order;

public interface OrderService {

    Order createOrder(Long userId, CreateOrderRequest request);

    Order getOrder(Long orderId, Long userId);
}
