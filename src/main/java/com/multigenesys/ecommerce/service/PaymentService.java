package com.multigenesys.ecommerce.service;

import com.multigenesys.ecommerce.dto.payment.PaymentRequest;
import com.multigenesys.ecommerce.dto.payment.PaymentResponse;

public interface PaymentService {

    PaymentResponse processPayment(PaymentRequest request);
}
