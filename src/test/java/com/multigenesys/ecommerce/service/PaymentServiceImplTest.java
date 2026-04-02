package com.multigenesys.ecommerce.service;

import com.multigenesys.ecommerce.dto.payment.PaymentRequest;
import com.multigenesys.ecommerce.dto.payment.PaymentResponse;
import com.multigenesys.ecommerce.entity.Order;
import com.multigenesys.ecommerce.entity.Role;
import com.multigenesys.ecommerce.gateway.PaymentGateway;
import com.multigenesys.ecommerce.repository.OrderRepository;
import com.multigenesys.ecommerce.repository.PaymentRepository;
import com.multigenesys.ecommerce.repository.UserRepository;
import com.multigenesys.ecommerce.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PaymentGateway paymentGateway;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void processPaymentMarksOrderAsPaid() {
        com.multigenesys.ecommerce.entity.User currentUser = new com.multigenesys.ecommerce.entity.User();
        currentUser.setEmail("test@example.com");
        currentUser.setRole(Role.USER);

        Order order = new Order();
        order.setUser(currentUser);
        order.setTotalPrice(50.0);
        order.setStatus("PENDING");
        order.setPaymentStatus("PENDING");

        PaymentRequest request = new PaymentRequest();
        request.orderId = 1L;
        request.method = "STRIPE";
        request.paymentMethodId = "pm_test";

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("test@example.com", null)
        );

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(currentUser));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(paymentGateway.charge(5000L, "pm_test")).thenReturn("pi_test");
        when(paymentRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.processPayment(request);

        assertEquals("SUCCESS", response.status);
        assertEquals("PAID", order.getStatus());
        assertEquals("SUCCESS", order.getPaymentStatus());

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertEquals("PAID", captor.getValue().getStatus());
    }
}
