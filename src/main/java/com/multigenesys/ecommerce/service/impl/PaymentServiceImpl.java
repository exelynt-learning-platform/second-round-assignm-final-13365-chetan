package com.multigenesys.ecommerce.service.impl;

import com.multigenesys.ecommerce.dto.payment.PaymentRequest;
import com.multigenesys.ecommerce.dto.payment.PaymentResponse;
import com.multigenesys.ecommerce.entity.Order;
import com.multigenesys.ecommerce.entity.Payment;
import com.multigenesys.ecommerce.entity.User;
import com.multigenesys.ecommerce.exception.BadRequestException;
import com.multigenesys.ecommerce.exception.ResourceNotFoundException;
import com.multigenesys.ecommerce.exception.UnauthorizedException;
import com.multigenesys.ecommerce.gateway.PaymentGateway;
import com.multigenesys.ecommerce.repository.OrderRepository;
import com.multigenesys.ecommerce.repository.PaymentRepository;
import com.multigenesys.ecommerce.repository.UserRepository;
import com.multigenesys.ecommerce.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentGateway paymentGateway;

    @Override
    public PaymentResponse processPayment(PaymentRequest request) {
        validate(request);

        Order order = orderRepository.findById(request.orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        User currentUser = currentUser();
        if (order.getUser() == null || !order.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only pay for your own order");
        }

        if (!"STRIPE".equalsIgnoreCase(request.method)) {
            throw new BadRequestException("Only Stripe payments are supported");
        }

        Payment payment = new Payment();
        payment.setOrderId(order.getId());

        try {
            long amountInCents = Math.round(order.getTotalPrice() * 100);
            String transactionId = paymentGateway.charge(amountInCents, request.paymentMethodId);

            payment.setTransactionId(transactionId);
            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);

            order.setStatus("PAID");
            order.setPaymentStatus("SUCCESS");
            orderRepository.save(order);

            PaymentResponse response = new PaymentResponse();
            response.status = "SUCCESS";
            response.message = "Payment completed successfully";
            response.transactionId = transactionId;
            return response;
        } catch (RuntimeException ex) {
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
    }

    private User currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Unauthorized"));
    }

    private void validate(PaymentRequest request) {
        if (request == null) {
            throw new BadRequestException("Payment data is required");
        }
        if (request.orderId == null) {
            throw new BadRequestException("Order id is required");
        }
        if (request.method == null || request.method.isBlank()) {
            throw new BadRequestException("Payment method is required");
        }
        if (request.paymentMethodId == null || request.paymentMethodId.isBlank()) {
            throw new BadRequestException("Payment method id is required");
        }
    }
}







