package com.lumastyle.eshop.dto.cart;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartRequest {

    @NotBlank(message = "ProductId must be provided")
    private String productId;

}
