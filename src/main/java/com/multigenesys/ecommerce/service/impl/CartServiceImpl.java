package com.multigenesys.ecommerce.service.impl;

import com.multigenesys.ecommerce.dto.cart.AddToCartRequest;
import com.multigenesys.ecommerce.dto.cart.UpdateCartItemRequest;
import com.multigenesys.ecommerce.entity.Cart;
import com.multigenesys.ecommerce.entity.CartItem;
import com.multigenesys.ecommerce.entity.Product;
import com.multigenesys.ecommerce.entity.User;
import com.multigenesys.ecommerce.exception.BadRequestException;
import com.multigenesys.ecommerce.exception.ResourceNotFoundException;
import com.multigenesys.ecommerce.repository.CartItemRepository;
import com.multigenesys.ecommerce.repository.CartRepository;
import com.multigenesys.ecommerce.repository.ProductRepository;
import com.multigenesys.ecommerce.repository.UserRepository;
import com.multigenesys.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
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
        validateAddRequest(request);

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
            int newQuantity = item.getQuantity() + request.quantity;
            if (product.getStockQuantity() > 0 && newQuantity > product.getStockQuantity()) {
                throw new BadRequestException("Insufficient stock");
            }
            item.setQuantity(newQuantity);
            item.setCart(cart);
        } else {
            if (product.getStockQuantity() > 0 && request.quantity > product.getStockQuantity()) {
                throw new BadRequestException("Insufficient stock");
            }
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
        validateUpdateRequest(request);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        Product product = productRepository.findById(request.productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        if (product.getStockQuantity() > 0 && request.quantity > product.getStockQuantity()) {
            throw new BadRequestException("Insufficient stock");
        }

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

    private void validateAddRequest(AddToCartRequest request) {
        if (request == null) {
            throw new BadRequestException("Cart item data is required");
        }
        if (request.productId == null) {
            throw new BadRequestException("Product id is required");
        }
        if (request.quantity < 1) {
            throw new BadRequestException("Quantity must be at least 1");
        }
    }

    private void validateUpdateRequest(UpdateCartItemRequest request) {
        if (request == null) {
            throw new BadRequestException("Cart item data is required");
        }
        if (request.productId == null) {
            throw new BadRequestException("Product id is required");
        }
        if (request.quantity < 1) {
            throw new BadRequestException("Quantity must be at least 1");
        }
    }
}







