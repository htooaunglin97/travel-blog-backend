package com.hal.travelapp.v1.dto.token;

import java.time.Instant;

public record AccessTokenDto(
        String accessToken,
        Instant expiresAt
) {
}
