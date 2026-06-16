package com.haircare.ecommerce.dto.cart;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CartResponse {

    private final Long cartId;
    private final Long userId;
    private final List<CartItemResponse> items;
    private final BigDecimal totalAmount;
}
