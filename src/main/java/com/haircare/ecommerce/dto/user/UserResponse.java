package com.haircare.ecommerce.dto.user;

import com.haircare.ecommerce.enums.HairType;
import com.haircare.ecommerce.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {

    private final Long id;
    private final String name;
    private final String email;
    private final Role role;
    private final HairType hairType;
}
