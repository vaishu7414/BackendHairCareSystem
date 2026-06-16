package com.haircare.ecommerce.dto.cart;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CartItemResponse {

    private final Long id;
    private final Long productId;
    private final String productName;
    private final String imageUrl;
    private final Integer quantity;
    private final BigDecimal unitPrice;
    private final BigDecimal subtotal;
}
