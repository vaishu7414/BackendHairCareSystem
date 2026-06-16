package com.haircare.ecommerce.service;

import com.haircare.ecommerce.dto.payment.PaymentRequest;
import com.haircare.ecommerce.dto.payment.PaymentResponse;
import com.haircare.ecommerce.entity.Order;
import com.haircare.ecommerce.entity.Payment;
import com.haircare.ecommerce.enums.PaymentStatus;
import com.haircare.ecommerce.exception.BadRequestException;
import com.haircare.ecommerce.exception.ResourceNotFoundException;
import com.haircare.ecommerce.repository.OrderRepository;
import com.haircare.ecommerce.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public PaymentResponse processPayment(Long orderId, String email, PaymentRequest request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if (!order.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Order does not belong to the authenticated user");
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElse(Payment.builder().order(order).build());

        payment.setAmount(order.getTotalPrice());
        payment.setMethod(request.getMethod());
        payment.setStatus(request.getStatus() == null ? PaymentStatus.SUCCESS : request.getStatus());

        Payment savedPayment = paymentRepository.save(payment);
        order.setPayment(savedPayment);

        return mapToResponse(savedPayment);
    }

    public PaymentResponse getPaymentResponse(Payment payment) {
        return payment == null ? null : mapToResponse(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .amount(payment.getAmount())
                .status(payment.getStatus())
                .method(payment.getMethod())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
