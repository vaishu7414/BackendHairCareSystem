package com.haircare.ecommerce.service;

import com.haircare.ecommerce.dto.auth.AuthResponse;
import com.haircare.ecommerce.dto.auth.ForgotPasswordRequest;
import com.haircare.ecommerce.dto.auth.LoginRequest;
import com.haircare.ecommerce.dto.auth.OtpResponse;
import com.haircare.ecommerce.dto.auth.RegisterRequest;
import com.haircare.ecommerce.dto.auth.ResetPasswordWithOtpRequest;
import com.haircare.ecommerce.dto.auth.SendOtpRequest;
import com.haircare.ecommerce.dto.auth.VerifyOtpRequest;
import com.haircare.ecommerce.dto.common.ApiResponse;
import com.haircare.ecommerce.entity.Cart;
import com.haircare.ecommerce.entity.User;
import com.haircare.ecommerce.enums.Role;
import com.haircare.ecommerce.exception.BadRequestException;
import com.haircare.ecommerce.repository.CartRepository;
import com.haircare.ecommerce.repository.UserRepository;
import com.haircare.ecommerce.security.JwtService;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${otp.expiry.minutes:5}")
    private Integer otpExpiryMinutes;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email is already registered");
        }

        User user = userRepository.save(User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.CUSTOMER)
                .build());

        cartRepository.save(Cart.builder().user(user).build());

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_CUSTOMER"))
                );

        String token = jwtService.generateToken(principal, Map.of("role", user.getRole().name()));
        return buildAuthResponse(user, token);
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        org.springframework.security.core.userdetails.User principal =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                "ROLE_" + user.getRole().name()
                        ))
                );

        String token = jwtService.generateToken(principal, Map.of("role", user.getRole().name()));
        return buildAuthResponse(user, token);
    }

    @Transactional
    public ApiResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account found with that email"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new ApiResponse("Password updated successfully");
    }

    @Transactional
    public OtpResponse sendOtp(SendOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account found with that email"));

        // Generate 6-digit OTP
        String otp = String.format("%06d", new Random().nextInt(999999));
        
        // Set OTP and expiry
        user.setOtp(otp);
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        user.setOtpVerified(false);
        userRepository.save(user);

        // Return OTP for frontend to send via EmailJS
        return new OtpResponse("OTP generated successfully", otp);
    }

    @Transactional
    public ApiResponse verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account found with that email"));

        if (user.getOtp() == null || user.getOtpExpiry() == null) {
            throw new BadRequestException("No OTP found. Please request a new OTP");
        }

        if (LocalDateTime.now().isAfter(user.getOtpExpiry())) {
            throw new BadRequestException("OTP has expired. Please request a new OTP");
        }

        if (!user.getOtp().equals(request.getOtp())) {
            throw new BadRequestException("Invalid OTP");
        }

        user.setOtpVerified(true);
        userRepository.save(user);

        return new ApiResponse("OTP verified successfully");
    }

    @Transactional
    public ApiResponse resetPasswordWithOtp(ResetPasswordWithOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadRequestException("No account found with that email"));

        if (!Boolean.TRUE.equals(user.getOtpVerified())) {
            throw new BadRequestException("Please verify OTP first");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setOtpVerified(false);
        userRepository.save(user);

        return new ApiResponse("Password reset successfully");
    }

    private AuthResponse buildAuthResponse(User user, String token) {
        return AuthResponse.builder()
                .token(token)
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .hairType(user.getHairType())
                .build();
    }
}
