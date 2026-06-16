package com.haircare.ecommerce.dto.payment;

import com.haircare.ecommerce.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PaymentResponse {

    private final Long id;
    private final BigDecimal amount;
    private final PaymentStatus status;
    private final String method;
    private final LocalDateTime createdAt;
}
