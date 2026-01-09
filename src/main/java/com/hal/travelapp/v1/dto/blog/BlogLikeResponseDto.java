package com.hal.travelapp.v1.dto.blog;

public record BlogLikeResponseDto(
        Long blogId,
        boolean liked,
        long likeCount
) {
}

