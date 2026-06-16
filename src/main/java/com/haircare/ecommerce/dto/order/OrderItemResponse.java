package com.haircare.ecommerce.dto.order;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderItemResponse {

    private final Long productId;
    private final String productName;
    private final String imageUrl;
    private final Integer quantity;
    private final BigDecimal price;
}
