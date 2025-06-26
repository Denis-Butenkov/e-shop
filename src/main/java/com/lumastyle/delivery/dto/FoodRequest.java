package com.lumastyle.delivery.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @DecimalMin("0.0")
    private BigDecimal price;

    @Pattern(regexp="^[A-Za-z0-9\\- ]+$", message="Invalid category")
    private String category;

}
