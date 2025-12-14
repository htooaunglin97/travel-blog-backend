package com.hal.travelapp.v1.dto;

import java.time.Instant;
import java.util.Set;

public record BlogDto(
        Long id,
        String title,
        String mainPhotoUrl,
        String paragraph1,
        String paragraph2,
        String paragraph3,
        String midPhoto1Url,
        String midPhoto2Url,
        String midPhoto3Url,
        String sidePhotoUrl,
        Long cityId,
        String cityName,
        Long authorId,
        String authorName,
        String status,
        Long bestTimeStartMonth,
        Long bestTimeEndMonth,
        Set<Long> categoryIds,
        Set<String> categoryNames,
        Instant createdAt,
        Instant updatedAt
) {
}

