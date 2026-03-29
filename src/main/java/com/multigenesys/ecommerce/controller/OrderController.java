package com.multigenesys.ecommerce.controller;

import com.multigenesys.ecommerce.dto.order.CreateOrderRequest;
import com.multigenesys.ecommerce.dto.order.OrderResponse;
import com.multigenesys.ecommerce.entity.Order;
import com.multigenesys.ecommerce.entity.User;
import com.multigenesys.ecommerce.exception.UnauthorizedException;
import com.multigenesys.ecommerce.repository.UserRepository;
import com.multigenesys.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/create")
    public OrderResponse createOrder(Authentication authentication, @Valid @RequestBody CreateOrderRequest request) {
        return toResponse(orderService.createOrder(currentUserId(authentication), request));
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(Authentication authentication, @PathVariable Long orderId) {
        return toResponse(orderService.getOrder(orderId, currentUserId(authentication)));
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Unauthorized"));
        return user.getId();
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.orderId = order.getId();
        response.shippingAddress = order.getShippingAddress();
        response.totalPrice = order.getTotalPrice();
        response.status = order.getStatus();
        response.paymentStatus = order.getPaymentStatus();
        response.items = order.getItems() == null ? List.of() : order.getItems().stream()
                .map(item -> item.getProduct().getName() + " x " + item.getQuantity())
                .collect(Collectors.toList());
        return response;
    }
}
