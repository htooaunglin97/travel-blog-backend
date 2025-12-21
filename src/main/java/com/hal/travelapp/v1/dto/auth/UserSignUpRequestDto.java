package com.hal.travelapp.v1.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserSignUpRequestDto(

        @NotBlank(message = "Please enter name")
        String name,

        @NotBlank(message = "Please enter email")
        @Email
        String email,

        @NotBlank(message = "Please enter password")
        String password
)
{
}
