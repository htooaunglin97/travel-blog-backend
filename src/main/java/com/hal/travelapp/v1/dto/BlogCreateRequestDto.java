package com.hal.travelapp.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record BlogCreateRequestDto(
        @NotBlank(message = "Title is required")
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        @NotBlank(message = "Main photo URL is required")
        String mainPhotoUrl,

        @NotBlank(message = "First paragraph is required")
        @Size(max = 1000, message = "Paragraph must not exceed 1000 characters")
        String paragraph1,

        @NotBlank(message = "Second paragraph is required")
        @Size(max = 1000, message = "Paragraph must not exceed 1000 characters")
        String paragraph2,

        @NotBlank(message = "Third paragraph is required")
        @Size(max = 1000, message = "Paragraph must not exceed 1000 characters")
        String paragraph3,

        @NotBlank(message = "First mid photo URL is required")
        String midPhoto1Url,

        @NotBlank(message = "Second mid photo URL is required")
        String midPhoto2Url,

        @NotBlank(message = "Third mid photo URL is required")
        String midPhoto3Url,

        @NotBlank(message = "Side photo URL is required")
        String sidePhotoUrl,

        @NotNull(message = "City ID is required")
        Long cityId,

        Long bestTimeStartMonth,

        Long bestTimeEndMonth,

        Set<Long> categoryIds
) {
}


