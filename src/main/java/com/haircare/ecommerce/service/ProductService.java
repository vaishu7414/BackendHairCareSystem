package com.haircare.ecommerce.service;

import com.haircare.ecommerce.dto.common.PagedResponse;
import com.haircare.ecommerce.dto.product.ProductRequest;
import com.haircare.ecommerce.dto.product.ProductResponse;
import com.haircare.ecommerce.entity.Product;
import com.haircare.ecommerce.entity.User;
import com.haircare.ecommerce.exception.BadRequestException;
import com.haircare.ecommerce.exception.ResourceNotFoundException;
import com.haircare.ecommerce.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public PagedResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sortBy));
        Page<Product> productPage = productRepository.findAll(pageable);
        return mapPage(productPage);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return mapToResponse(getProductEntityById(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getRecommendedProducts(String email) {
        User user = userService.getUserEntityByEmail(email);
        if (user.getHairType() == null) {
            throw new BadRequestException("Please select a hair type before requesting recommendations");
        }

        return productRepository.findByHairType(user.getHairType(), PageRequest.of(0, 20))
                .getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productRepository.save(Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .imageUrl(request.getImageUrl())
                .hairType(request.getHairType())
                .stock(request.getStock())
                .build());
        return mapToResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = getProductEntityById(id);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setImageUrl(request.getImageUrl());
        product.setHairType(request.getHairType());
        product.setStock(request.getStock());
        return mapToResponse(productRepository.save(product));
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = getProductEntityById(id);
        productRepository.delete(product);
    }

    @Transactional(readOnly = true)
    public Product getProductEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .hairType(product.getHairType())
                .stock(product.getStock())
                .build();
    }

    private PagedResponse<ProductResponse> mapPage(Page<Product> productPage) {
        return PagedResponse.<ProductResponse>builder()
                .content(productPage.getContent().stream().map(this::mapToResponse).toList())
                .page(productPage.getNumber())
                .size(productPage.getSize())
                .totalElements(productPage.getTotalElements())
                .totalPages(productPage.getTotalPages())
                .last(productPage.isLast())
                .build();
    }
}
