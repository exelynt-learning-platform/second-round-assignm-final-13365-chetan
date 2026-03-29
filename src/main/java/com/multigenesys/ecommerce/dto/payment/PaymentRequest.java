package com.multigenesys.ecommerce.dto.payment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequest {

    @NotNull(message = "Order id is required")
    public Long orderId;

    @NotBlank(message = "Payment method is required")
    public String method;

    @NotBlank(message = "Payment method id is required")
    public String paymentMethodId;
}
