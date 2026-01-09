package com.hal.travelapp.v1.dto.blog;

public record BlogFavoriteResponseDto(
        Long blogId,
        boolean favorited,
        String message
) {
}

