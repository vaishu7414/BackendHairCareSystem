package com.haircare.ecommerce.service;

import com.haircare.ecommerce.dto.order.OrderItemResponse;
import com.haircare.ecommerce.dto.order.OrderResponse;
import com.haircare.ecommerce.dto.payment.PaymentResponse;
import com.haircare.ecommerce.entity.Cart;
import com.haircare.ecommerce.entity.CartItem;
import com.haircare.ecommerce.entity.Order;
import com.haircare.ecommerce.entity.OrderItem;
import com.haircare.ecommerce.entity.Product;
import com.haircare.ecommerce.entity.User;
import com.haircare.ecommerce.enums.OrderStatus;
import com.haircare.ecommerce.exception.BadRequestException;
import com.haircare.ecommerce.exception.ResourceNotFoundException;
import com.haircare.ecommerce.repository.OrderRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final UserService userService;

    @Transactional
    public OrderResponse placeOrder(String email) {
        User user = userService.getUserEntityByEmail(email);
        Cart cart = cartService.getCartByUserEmail(email);

        if (cart.getCartItems().isEmpty()) {
            throw new BadRequestException("Cart is empty");
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            if (cartItem.getQuantity() > product.getStock()) {
                throw new BadRequestException("Insufficient stock for product: " + product.getName());
            }

            product.setStock(product.getStock() - cartItem.getQuantity());

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(cartItem.getQuantity())
                    .price(product.getPrice())
                    .build();
            orderItems.add(orderItem);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setOrderItems(orderItems);
        order.setTotalPrice(total);

        Order savedOrder = orderRepository.save(order);
        cartService.clearCart(cart);

        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String email) {
        User user = userService.getUserEntityByEmail(email);
        return orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrderEntityById(orderId);
        order.setStatus(status);
        return mapToResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public Order getOrderEntityById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    public void validateOrderOwnership(Order order, String email) {
        if (!order.getUser().getEmail().equals(email)) {
            throw new BadRequestException("Order does not belong to the authenticated user");
        }
    }

    public OrderResponse mapToResponse(Order order) {
        PaymentResponse paymentResponse = null;
        if (order.getPayment() != null) {
            paymentResponse = PaymentResponse.builder()
                    .id(order.getPayment().getId())
                    .amount(order.getPayment().getAmount())
                    .status(order.getPayment().getStatus())
                    .method(order.getPayment().getMethod())
                    .createdAt(order.getPayment().getCreatedAt())
                    .build();
        }

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .customerName(order.getUser().getName())
                .items(order.getOrderItems().stream().map(this::mapOrderItem).toList())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .payment(paymentResponse)
                .build();
    }

    private OrderItemResponse mapOrderItem(OrderItem orderItem) {
        return OrderItemResponse.builder()
                .productId(orderItem.getProduct().getId())
                .productName(orderItem.getProduct().getName())
                .imageUrl(orderItem.getProduct().getImageUrl())
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
