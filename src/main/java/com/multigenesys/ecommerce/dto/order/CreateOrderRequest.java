package com.multigenesys.ecommerce.dto.order;

import jakarta.validation.constraints.NotBlank;

public class CreateOrderRequest {

    @NotBlank(message = "Shipping address is required")
    public String address;
}
