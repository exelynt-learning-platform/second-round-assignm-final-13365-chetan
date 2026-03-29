package com.multigenesys.ecommerce.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateCartItemRequest {

    @NotNull(message = "Product id is required")
    public Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    public int quantity;
}
