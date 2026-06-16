package com.haircare.ecommerce.service;

import com.haircare.ecommerce.dto.user.UserResponse;
import com.haircare.ecommerce.entity.User;
import com.haircare.ecommerce.enums.HairType;
import com.haircare.ecommerce.exception.ResourceNotFoundException;
import com.haircare.ecommerce.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserResponse updateHairType(String email, HairType hairType) {
        User user = getUserEntityByEmail(email);
        user.setHairType(hairType);
        return mapToResponse(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .hairType(user.getHairType())
                .build();
    }
}
