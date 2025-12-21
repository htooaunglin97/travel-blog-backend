package com.hal.travelapp.v1.dto;

import java.time.Instant;

public record CertifiedUserRequestDto(
        Long id,
        Long userId,
        String userName,
        String userEmail,
        String status,
        Long reviewedById,
        String reviewedByName,
        String rejectionReason,
        Instant createdAt,
        Instant updatedAt
) {
}

