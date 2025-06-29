package com.lumastyle.eshop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private String id;

    private String userId;

    private String userAddress;

    private String phoneNumber;

    private String email;

    private Integer amount;

    private String paymentStatus;

    private String paymentOrderId;

    private String orderStatus;

    private List<OrderItem> orderedItems;
}
