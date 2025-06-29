package com.lumastyle.eshop.entity;

import com.lumastyle.eshop.dto.order.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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

    private List<OrderItem> orderedItems;

    private Integer amount;

    private String paymentStatus;

    private String paymentOrderId;

    private String paymentId;

    private String signature;

    private String orderStatus;
}
