package com.haircare.ecommerce.dto.payment;

import com.haircare.ecommerce.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentRequest {

    @NotBlank(message = "Payment method is required")
    private String method;

    private PaymentStatus status;
}
