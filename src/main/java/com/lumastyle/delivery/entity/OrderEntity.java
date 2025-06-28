package com.lumastyle.delivery.entity;

import com.lumastyle.delivery.dto.order.OrderRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
public class OrderEntity {
    @Id
    private String id;
    private String userId;
    private String userAddress;
    private String phoneNumber;
    private String email;
    private List<OrderRequest> orderedItems;
    private BigDecimal amount;
    private String paymentStatus;
    private String paymentOrderId;
    private String signature;
    private String orderStatus;
}
