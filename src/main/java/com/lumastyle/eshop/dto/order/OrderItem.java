package com.lumastyle.eshop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
    private String productId;
    private Integer quantity;
    private BigDecimal price;
    private String category;
    private String imageUrl;
    private String description;
    private String name;
}
