package com.haircare.ecommerce.controller;

import com.haircare.ecommerce.dto.payment.PaymentRequest;
import com.haircare.ecommerce.dto.payment.PaymentResponse;
import com.haircare.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/order/{orderId}")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable Long orderId,
            @Valid @RequestBody PaymentRequest request,
            Principal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.processPayment(orderId, principal.getName(), request));
    }
}
