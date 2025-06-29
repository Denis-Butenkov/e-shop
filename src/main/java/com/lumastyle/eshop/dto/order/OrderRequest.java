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
public class OrderRequest {

    private List<OrderItem> orderedItems;
    private String userAddress;
    private Integer amount;
    private String phoneNumber;
    private String email;
}
