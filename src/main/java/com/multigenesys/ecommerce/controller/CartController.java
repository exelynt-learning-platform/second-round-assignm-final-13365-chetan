package com.multigenesys.ecommerce.controller;

import com.multigenesys.ecommerce.dto.cart.AddToCartRequest;
import com.multigenesys.ecommerce.dto.cart.UpdateCartItemRequest;
import com.multigenesys.ecommerce.entity.Cart;
import com.multigenesys.ecommerce.entity.User;
import com.multigenesys.ecommerce.exception.UnauthorizedException;
import com.multigenesys.ecommerce.repository.UserRepository;
import com.multigenesys.ecommerce.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public ResponseEntity<Cart> addToCart(Authentication authentication, @Valid @RequestBody AddToCartRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(cartService.addToCart(currentUserId(authentication), request));
    }

    @PutMapping("/update")
    public ResponseEntity<Cart> updateItem(Authentication authentication, @Valid @RequestBody UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(currentUserId(authentication), request));
    }

    @GetMapping
    public ResponseEntity<Cart> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getCart(currentUserId(authentication)));
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeItem(Authentication authentication, @PathVariable Long productId) {
        cartService.removeItem(currentUserId(authentication), productId);
        return ResponseEntity.ok("Item removed");
    }

    private Long currentUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Unauthorized"));
        return user.getId();
    }
}
