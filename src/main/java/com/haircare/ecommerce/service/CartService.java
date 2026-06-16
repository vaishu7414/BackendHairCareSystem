package com.haircare.ecommerce.service;

import com.haircare.ecommerce.dto.cart.AddCartItemRequest;
import com.haircare.ecommerce.dto.cart.CartItemResponse;
import com.haircare.ecommerce.dto.cart.CartResponse;
import com.haircare.ecommerce.dto.cart.UpdateCartItemRequest;
import com.haircare.ecommerce.entity.Cart;
import com.haircare.ecommerce.entity.CartItem;
import com.haircare.ecommerce.entity.Product;
import com.haircare.ecommerce.entity.User;
import com.haircare.ecommerce.exception.BadRequestException;
import com.haircare.ecommerce.exception.ResourceNotFoundException;
import com.haircare.ecommerce.repository.CartItemRepository;
import com.haircare.ecommerce.repository.CartRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    private final UserService userService;

    public CartResponse getCart(String email) {
        Cart cart = getCartByUserEmail(email);
        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String email, AddCartItemRequest request) {
        Cart cart = getCartByUserEmail(email);
        Product product = productService.getProductEntityById(request.getProductId());
        validateRequestedQuantity(product, request.getQuantity());

        CartItem cartItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(0)
                        .build());

        int updatedQuantity = cartItem.getQuantity() + request.getQuantity();
        validateRequestedQuantity(product, updatedQuantity);
        cartItem.setQuantity(updatedQuantity);
        CartItem savedItem = cartItemRepository.save(cartItem);
        if (cart.getCartItems().stream().noneMatch(item -> item.getId() != null && item.getId().equals(savedItem.getId()))) {
            cart.getCartItems().add(savedItem);
        }

        return getCart(email);
    }

    @Transactional
    public CartResponse updateItem(String email, Long cartItemId, UpdateCartItemRequest request) {
        Cart cart = getCartByUserEmail(email);
        CartItem cartItem = getCartItem(cartItemId);
        ensureOwnership(cart, cartItem);
        validateRequestedQuantity(cartItem.getProduct(), request.getQuantity());
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
        return getCart(email);
    }

    @Transactional
    public CartResponse removeItem(String email, Long cartItemId) {
        Cart cart = getCartByUserEmail(email);
        CartItem cartItem = getCartItem(cartItemId);
        ensureOwnership(cart, cartItem);
        cartItemRepository.delete(cartItem);
        cart.getCartItems().removeIf(item -> item.getId().equals(cartItemId));
        return getCart(email);
    }

    @Transactional
    public void clearCart(Cart cart) {
        List<CartItem> items = List.copyOf(cart.getCartItems());
        items.forEach(cartItemRepository::delete);
        cart.getCartItems().clear();
    }

    @Transactional(readOnly = true)
    public Cart getCartByUserEmail(String email) {
        User user = userService.getUserEntityByEmail(email);
        return cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user"));
    }

    private CartItem getCartItem(Long id) {
        return cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + id));
    }

    private void ensureOwnership(Cart cart, CartItem cartItem) {
        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to the authenticated user");
        }
    }

    private void validateRequestedQuantity(Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new BadRequestException("Insufficient stock for product: " + product.getName());
        }
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> items = cart.getCartItems().stream()
                .map(this::mapItem)
                .toList();

        BigDecimal total = items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .cartId(cart.getId())
                .userId(cart.getUser().getId())
                .items(items)
                .totalAmount(total)
                .build();
    }

    private CartItemResponse mapItem(CartItem item) {
        BigDecimal unitPrice = item.getProduct().getPrice();
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
        return CartItemResponse.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getName())
                .imageUrl(item.getProduct().getImageUrl())
                .quantity(item.getQuantity())
                .unitPrice(unitPrice)
                .subtotal(subtotal)
                .build();
    }
}
