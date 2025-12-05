package com.hal.travelapp.v1.dto;

public record LoginResponseDto(
        UserDto userData,
        AccessTokenDto tokenData
) {
}
