package com.haircare.ecommerce.dto.product;

import com.haircare.ecommerce.enums.HairType;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ProductResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String imageUrl;
    private final HairType hairType;
    private final Integer stock;
}
