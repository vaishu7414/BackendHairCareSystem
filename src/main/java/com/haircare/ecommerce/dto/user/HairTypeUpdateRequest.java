package com.haircare.ecommerce.dto.user;

import com.haircare.ecommerce.enums.HairType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HairTypeUpdateRequest {

    @NotNull(message = "Hair type is required")
    private HairType hairType;
}
