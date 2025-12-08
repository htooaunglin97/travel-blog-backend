package com.hal.travelapp.v1.dto;

import java.time.Instant;

public record AccessTokenDto(
        String accessToken,
        Instant expiresAt
) {
}
