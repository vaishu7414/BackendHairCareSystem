package com.haircare.ecommerce.dto.order;

import com.haircare.ecommerce.dto.payment.PaymentResponse;
import com.haircare.ecommerce.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {

    private final Long id;
    private final Long userId;
    private final String customerName;
    private final List<OrderItemResponse> items;
    private final BigDecimal totalPrice;
    private final OrderStatus status;
    private final LocalDateTime createdAt;
    private final PaymentResponse payment;
}
