package com.haircare.ecommerce.config;

import com.haircare.ecommerce.entity.Cart;
import com.haircare.ecommerce.entity.User;
import com.haircare.ecommerce.enums.Role;
import com.haircare.ecommerce.repository.CartRepository;
import com.haircare.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.name}")
    private String adminName;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner seedAdminUser() {
        return args -> {
            if (userRepository.existsByEmail(adminEmail)) {
                return;
            }

            User admin = userRepository.save(User.builder()
                    .name(adminName)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(Role.ADMIN)
                    .build());

            cartRepository.save(Cart.builder()
                    .user(admin)
                    .build());
        };
    }
}
