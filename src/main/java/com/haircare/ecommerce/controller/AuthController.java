package com.haircare.ecommerce.controller;

import com.haircare.ecommerce.dto.auth.AuthResponse;
import com.haircare.ecommerce.dto.auth.ForgotPasswordRequest;
import com.haircare.ecommerce.dto.auth.LoginRequest;
import com.haircare.ecommerce.dto.auth.OtpResponse;
import com.haircare.ecommerce.dto.auth.RegisterRequest;
import com.haircare.ecommerce.dto.auth.ResetPasswordWithOtpRequest;
import com.haircare.ecommerce.dto.auth.SendOtpRequest;
import com.haircare.ecommerce.dto.auth.VerifyOtpRequest;
import com.haircare.ecommerce.dto.common.ApiResponse;
import com.haircare.ecommerce.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        return ResponseEntity.ok(authService.forgotPassword(request));
    }

    @PostMapping("/send-otp")
    public ResponseEntity<OtpResponse> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        return ResponseEntity.ok(authService.sendOtp(request));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ResponseEntity.ok(authService.verifyOtp(request));
    }

    @PostMapping("/reset-password-with-otp")
    public ResponseEntity<ApiResponse> resetPasswordWithOtp(@Valid @RequestBody ResetPasswordWithOtpRequest request) {
        return ResponseEntity.ok(authService.resetPasswordWithOtp(request));
    }
}
