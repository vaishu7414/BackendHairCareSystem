package com.haircare.ecommerce.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OtpResponse {

    private String message;
    private String otp;
}
