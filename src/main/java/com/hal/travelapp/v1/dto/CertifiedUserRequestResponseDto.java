package com.hal.travelapp.v1.dto;

public record CertifiedUserRequestResponseDto(
        Long requestId,
        String status,
        String message
) {
}

