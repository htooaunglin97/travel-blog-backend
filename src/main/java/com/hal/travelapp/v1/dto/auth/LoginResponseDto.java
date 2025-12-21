package com.hal.travelapp.v1.dto.auth;

import com.hal.travelapp.v1.dto.user.UserDto;
import com.hal.travelapp.v1.dto.token.AccessTokenDto;

public record LoginResponseDto(
        UserDto userData,
        AccessTokenDto tokenData
) {
}
