package com.haircare.ecommerce.controller;

import com.haircare.ecommerce.dto.user.HairTypeUpdateRequest;
import com.haircare.ecommerce.dto.user.UserResponse;
import com.haircare.ecommerce.service.UserService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/me/hair-type")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<UserResponse> updateHairType(
            @Valid @RequestBody HairTypeUpdateRequest request,
            Principal principal
    ) {
        return ResponseEntity.ok(userService.updateHairType(principal.getName(), request.getHairType()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
