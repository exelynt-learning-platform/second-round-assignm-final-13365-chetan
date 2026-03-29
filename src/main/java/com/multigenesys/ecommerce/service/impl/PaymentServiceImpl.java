package com.multigenesys.ecommerce.service.impl;

import com.multigenesys.ecommerce.dto.payment.*;
import com.multigenesys.ecommerce.entity.*;
import com.multigenesys.ecommerce.exception.BadRequestException;
import com.multigenesys.ecommerce.exception.ResourceNotFoundException;
import com.multigenesys.ecommerce.exception.UnauthorizedException;
import com.multigenesys.ecommerce.repository.*;
import com.multigenesys.ecommerce.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.stripe.secret-key}")
    private String stripeSecretKey;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {

        Order order = orderRepository.findById(request.orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User currentUser = currentUser();
        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only pay for your own order");
        }

        if (!"STRIPE".equalsIgnoreCase(request.method)) {
            throw new BadRequestException("Only Stripe payments are supported");
        }

        Payment payment = new Payment();
        payment.setOrderId(order.getId());

        try {
            Stripe.apiKey = stripeSecretKey;

            long amountInCents = Math.round(order.getTotalPrice() * 100);
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .setConfirm(true)
                    .setPaymentMethod(request.paymentMethodId)
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);
            payment.setTransactionId(intent.getId());

            if ("succeeded".equalsIgnoreCase(intent.getStatus())) {
                payment.setStatus("SUCCESS");
                paymentRepository.save(payment);

                order.setStatus("PAID");
                order.setPaymentStatus("SUCCESS");
                orderRepository.save(order);

                PaymentResponse response = new PaymentResponse();
                response.status = "SUCCESS";
                response.message = "Payment completed successfully";
                response.transactionId = intent.getId();
                return response;
            }
        } catch (StripeException ex) {
            payment.setStatus("FAILED");
            paymentRepository.save(payment);

            order.setStatus("PAYMENT_FAILED");
            order.setPaymentStatus("FAILED");
            orderRepository.save(order);

            PaymentResponse response = new PaymentResponse();
            response.status = "FAILED";
            response.message = ex.getMessage();
            return response;
        }

        payment.setStatus("FAILED");
        paymentRepository.save(payment);

        PaymentResponse response = new PaymentResponse();
        order.setStatus("PAYMENT_FAILED");
        order.setPaymentStatus("FAILED");
        orderRepository.save(order);

        response.status = "FAILED";
        response.message = "Payment was not completed";
        return response;
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Unauthorized"));
    }
}
