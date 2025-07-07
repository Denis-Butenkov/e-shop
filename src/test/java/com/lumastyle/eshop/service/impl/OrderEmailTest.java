package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.entity.OrderEntity;
import com.lumastyle.eshop.exception.GoPayIntegrationException;
import com.lumastyle.eshop.mapper.OrderMapper;
import com.lumastyle.eshop.repository.CartRepository;
import com.lumastyle.eshop.repository.OrderRepository;
import com.lumastyle.eshop.service.EmailService;
import com.lumastyle.eshop.service.UserService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link OrderServiceImpl#verifyPayment(Map, String)},
 * ensuring emails are sent and cart cleared on successful payment.
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class OrderEmailTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartRepository cartRepository;
    @Mock private OrderMapper mapper;
    @Mock private UserService userService;
    @Mock private JavaMailSender mailSender;
    @Mock private EmailService emailService;
    @Mock private Counter ordersCreatedCounter;
    @Mock private Counter ordersPaymentFailedCounter;
    @Mock private Timer orderProcessingTimer;
    @Mock private Counter emailsSentCounter;

    @InjectMocks
    private OrderServiceImpl service;

    @BeforeEach
    void setUp() {
        // Initialize real EmailServiceImpl with mocked mailSender and metrics
        emailService = new EmailServiceImpl(mailSender, emailsSentCounter);
        service = new OrderServiceImpl(
                orderRepository,
                cartRepository,
                mapper,
                userService,
                emailService,
                ordersCreatedCounter,
                ordersPaymentFailedCounter,
                orderProcessingTimer
        );
    }

    /**
     * Verifies that when payment is marked as Paid, an email is sent and the cart is cleared.
     */
    @Test
    @DisplayName("verifyPayment sends email and clears cart on Paid status")
    @Tag("Unit")
    void verifyPayment_paid_sendsEmailAndClearsCart() {
        OrderEntity order = new OrderEntity();
        order.setId("123");
        order.setUserId("user1");
        order.setEmail("customer@example.com");

        when(orderRepository.findByGopayPaymentId("gp1")).thenReturn(Optional.of(order));

        Map<String, String> data = new HashMap<>();
        data.put("paymentId", "gp1");

        service.verifyPayment(data, "Paid");

        // Verify email sent
        verify(mailSender).send(any(SimpleMailMessage.class));
        // Verify cart cleared
        verify(cartRepository).deleteByUserId("user1");
    }

    /**
     * Verifies that when no order is found, a GoPayIntegrationException is thrown.
     */
    @Test
    @DisplayName("verifyPayment throws when order not found")
    @Tag("Unit")
    void verifyPayment_orderNotFound_throws() {
        when(orderRepository.findByGopayPaymentId(anyString())).thenReturn(Optional.empty());
        Map<String, String> data = new HashMap<>();
        data.put("paymentId", "gpX");

        assertThrows(GoPayIntegrationException.class, () -> service.verifyPayment(data, "Paid"));
    }
}
