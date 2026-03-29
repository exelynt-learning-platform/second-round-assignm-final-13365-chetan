
package com.multigenesys.ecommerce.service.impl;

import com.multigenesys.ecommerce.dto.cart.AddToCartRequest;
import com.multigenesys.ecommerce.dto.cart.UpdateCartItemRequest;
import com.multigenesys.ecommerce.entity.*;
import com.multigenesys.ecommerce.exception.BadRequestException;
import com.multigenesys.ecommerce.exception.ResourceNotFoundException;
import com.multigenesys.ecommerce.repository.*;
import com.multigenesys.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public Cart addToCart(Long userId, AddToCartRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Product product = productRepository.findById(request.productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        List<CartItem> items = cart.getItems();

        if (items == null) {
            items = new ArrayList<>();
        }

        Optional<CartItem> existingItem = items.stream()
                .filter(i -> i.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.quantity);
        } else {
            CartItem item = new CartItem();
            item.setProduct(product);
            item.setQuantity(request.quantity);
            item.setCart(cart);
            items.add(item);
        }

        cart.setItems(items);
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateItem(Long userId, UpdateCartItemRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        List<CartItem> items = cart.getItems();
        if (items == null || items.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        CartItem item = items.stream()
                .filter(i -> i.getProduct().getId().equals(request.productId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Product not found in cart"));

        item.setQuantity(request.quantity);
        cart.setItems(items);
        return cartRepository.save(cart);
    }

    @Override
    public Cart getCart(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));
    }

    @Override
    public void removeItem(Long userId, Long productId) {

        Cart cart = getCart(userId);

        List<CartItem> items = cart.getItems();
        if (items == null || items.isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        boolean removed = items.removeIf(i -> i.getProduct().getId().equals(productId));
        if (!removed) {
            throw new ResourceNotFoundException("Product not found in cart");
        }

        cart.setItems(items);
        cartRepository.save(cart);
    }
}
