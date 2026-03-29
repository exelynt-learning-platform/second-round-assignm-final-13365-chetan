
package com.multigenesys.ecommerce.controller;

import com.multigenesys.ecommerce.dto.cart.AddToCartRequest;
import com.multigenesys.ecommerce.dto.cart.UpdateCartItemRequest;
import com.multigenesys.ecommerce.entity.Cart;
import com.multigenesys.ecommerce.entity.User;
import com.multigenesys.ecommerce.exception.UnauthorizedException;
import com.multigenesys.ecommerce.repository.UserRepository;
import com.multigenesys.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    public Cart addToCart(Authentication authentication, @Valid @RequestBody AddToCartRequest request) {
        return cartService.addToCart(currentUserId(authentication), request);
    }

    @PutMapping("/update")
    public Cart updateItem(Authentication authentication, @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(currentUserId(authentication), request);
    }

    @GetMapping
    public Cart getCart(Authentication authentication) {
        return cartService.getCart(currentUserId(authentication));
    }

    @DeleteMapping("/remove/{productId}")
    public String removeItem(Authentication authentication, @PathVariable Long productId) {
        cartService.removeItem(currentUserId(authentication), productId);
        return "Item removed";
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
