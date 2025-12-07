package com.hal.travelapp.v1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @NotBlank(message = "Please enter email")
        @Email
        String email,

        @NotBlank(message = "Please enter password")
        String password
) {
}

