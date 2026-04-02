package com.multigenesys.ecommerce.service.impl;

import com.multigenesys.ecommerce.dto.order.CreateOrderRequest;
import com.multigenesys.ecommerce.entity.Cart;
import com.multigenesys.ecommerce.entity.CartItem;
import com.multigenesys.ecommerce.entity.Order;
import com.multigenesys.ecommerce.entity.OrderItem;
import com.multigenesys.ecommerce.entity.Product;
import com.multigenesys.ecommerce.entity.User;
import com.multigenesys.ecommerce.exception.BadRequestException;
import com.multigenesys.ecommerce.exception.ResourceNotFoundException;
import com.multigenesys.ecommerce.repository.CartRepository;
import com.multigenesys.ecommerce.repository.OrderRepository;
import com.multigenesys.ecommerce.repository.ProductRepository;
import com.multigenesys.ecommerce.repository.UserRepository;
import com.multigenesys.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Order createOrder(Long userId, CreateOrderRequest request) {
        validate(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Cart is empty"));

        List<CartItem> cartItems = cart.getItems();
        if (cartItems == null || cartItems.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setShippingAddress(request.address);
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");

        List<OrderItem> orderItems = new ArrayList<>();
        double total = 0;

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product == null) {
                throw new ResourceNotFoundException("Product not found");
            }

            Product persistedProduct = product;
            if (productRepository != null && product.getId() != null) {
                persistedProduct = productRepository.findById(product.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
            }

            if (persistedProduct.getStockQuantity() > 0 && cartItem.getQuantity() > persistedProduct.getStockQuantity()) {
                throw new BadRequestException("Insufficient stock");
            }

            OrderItem item = new OrderItem();
            item.setProduct(persistedProduct);
            item.setQuantity(cartItem.getQuantity());
            item.setPrice(persistedProduct.getPrice());
            item.setOrder(order);

            if (productRepository != null) {
                persistedProduct.setStockQuantity(persistedProduct.getStockQuantity() - cartItem.getQuantity());
                productRepository.save(persistedProduct);
            }

            total += cartItem.getQuantity() * persistedProduct.getPrice();
            orderItems.add(item);
        }

        order.setItems(orderItems);
        order.setTotalPrice(total);

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    @Override
    public Order getOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getUser() == null || !order.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found");
        }

        return order;
    }

    private void validate(CreateOrderRequest request) {
        if (request == null) {
            throw new BadRequestException("Order data is required");
        }
        if (request.address == null || request.address.isBlank()) {
            throw new BadRequestException("Shipping address is required");
        }
    }
}








