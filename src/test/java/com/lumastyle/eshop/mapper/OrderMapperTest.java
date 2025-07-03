package com.lumastyle.eshop.mapper;

import com.lumastyle.eshop.dto.order.OrderItem;
import com.lumastyle.eshop.dto.order.OrderRequest;
import com.lumastyle.eshop.dto.order.OrderResponse;
import com.lumastyle.eshop.entity.OrderEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link OrderMapper},
 * verifying mapping between {@link OrderRequest}/{@link OrderResponse} DTO and {@link OrderEntity}.
 */
@ActiveProfiles("test")
class OrderMapperTest {

    private final OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    /**
     * Verifies that toEntity maps fields from {@link OrderRequest} to {@link OrderEntity} correctly.
     */
    @Test
    @DisplayName("toEntity maps all fields correctly")
    @Tag("Unit")
    void toEntity_mapsFields() {
        OrderItem item = OrderItem.builder()
                .productId("p1")
                .quantity(2)
                .price(new BigDecimal("3.00"))
                .category("Cat")
                .imageUrl("img")
                .description("desc")
                .name("name")
                .build();
        OrderRequest request = OrderRequest.builder()
                .orderedItems(List.of(item))
                .userAddress("addr")
                .amount(5)
                .phoneNumber("123")
                .email("e@e.com")
                .build();

        OrderEntity entity = mapper.toEntity(request);

        assertNull(entity.getId());
        assertNull(entity.getUserId());
        assertNull(entity.getPaymentStatus());
        assertNull(entity.getGopayPaymentId());
        assertNull(entity.getGopayTransactionId());
        assertNull(entity.getOrderStatus());
        assertEquals(request.getOrderedItems(), entity.getOrderedItems());
        assertEquals(request.getUserAddress(), entity.getUserAddress());
        assertEquals(request.getPhoneNumber(), entity.getPhoneNumber());
        assertEquals(request.getEmail(), entity.getEmail());
        assertEquals(request.getAmount(), entity.getAmount());
    }

    /**
     * Verifies that toResponse maps fields from {@link OrderEntity} to {@link OrderResponse} correctly.
     */
    @Test
    @DisplayName("toResponse maps all fields correctly")
    @Tag("Unit")
    void toResponse_mapsFields() {
        OrderItem item = OrderItem.builder()
                .productId("p1")
                .quantity(1)
                .build();
        OrderEntity entity = OrderEntity.builder()
                .id("1")
                .userId("u1")
                .userAddress("addr")
                .phoneNumber("123")
                .email("e@e.com")
                .orderedItems(List.of(item))
                .amount(10)
                .paymentStatus("PAID")
                .gopayPaymentId("gp1")
                .gopayTransactionId("t1")
                .orderStatus("CREATED")
                .build();

        OrderResponse response = mapper.toResponse(entity);

        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getUserId(), response.getUserId());
        assertEquals(entity.getUserAddress(), response.getUserAddress());
        assertEquals(entity.getPhoneNumber(), response.getPhoneNumber());
        assertEquals(entity.getEmail(), response.getEmail());
        assertEquals(entity.getAmount(), response.getAmount());
        assertEquals(entity.getPaymentStatus(), response.getPaymentStatus());
        assertEquals(entity.getGopayPaymentId(), response.getPaymentOrderId());
        assertEquals(entity.getOrderStatus(), response.getOrderStatus());
        assertEquals(entity.getOrderedItems(), response.getOrderedItems());
    }
}
