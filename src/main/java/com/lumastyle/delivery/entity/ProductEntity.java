package com.lumastyle.delivery.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
public class ProductEntity {
    @Id
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private BigDecimal price;
    private String category;
}
