package com.multigenesys.ecommerce.controller;

import com.multigenesys.ecommerce.dto.order.CreateOrderRequest;
import com.multigenesys.ecommerce.dto.order.OrderResponse;
import com.multigenesys.ecommerce.entity.Order;
import com.multigenesys.ecommerce.entity.User;
import com.multigenesys.ecommerce.exception.UnauthorizedException;
import com.multigenesys.ecommerce.repository.UserRepository;
import com.multigenesys.ecommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<OrderResponse> createOrder(Authentication authentication, @Valid @RequestBody CreateOrderRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(orderService.createOrder(currentUserId(authentication), request)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(Authentication authentication, @PathVariable Long orderId) {
        return ResponseEntity.ok(toResponse(orderService.getOrder(orderId, currentUserId(authentication))));
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
