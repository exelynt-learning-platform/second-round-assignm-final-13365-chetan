
package com.multigenesys.ecommerce.service;

import com.multigenesys.ecommerce.dto.cart.AddToCartRequest;
import com.multigenesys.ecommerce.dto.cart.UpdateCartItemRequest;
import com.multigenesys.ecommerce.entity.Cart;

public interface CartService {

    Cart addToCart(Long userId, AddToCartRequest request);

    Cart updateItem(Long userId, UpdateCartItemRequest request);

    Cart getCart(Long userId);

    void removeItem(Long userId, Long productId);
}
