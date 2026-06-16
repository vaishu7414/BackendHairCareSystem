package com.haircare.ecommerce.repository;

import com.haircare.ecommerce.entity.Product;
import com.haircare.ecommerce.enums.HairType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByHairType(HairType hairType, Pageable pageable);
}
