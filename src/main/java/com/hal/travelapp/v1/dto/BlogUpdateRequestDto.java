package com.hal.travelapp.v1.dto;

import jakarta.validation.constraints.Size;

public record BlogUpdateRequestDto(
        @Size(max = 200, message = "Title must not exceed 200 characters")
        String title,

        String mainPhotoUrl,

        @Size(max = 1000, message = "Paragraph must not exceed 1000 characters")
        String paragraph1,

        @Size(max = 1000, message = "Paragraph must not exceed 1000 characters")
        String paragraph2,

        @Size(max = 1000, message = "Paragraph must not exceed 1000 characters")
        String paragraph3,

        String midPhoto1Url,

        String midPhoto2Url,

        String midPhoto3Url,

        String sidePhotoUrl,

        Long cityId,

        Long bestTimeStartMonth,

        Long bestTimeEndMonth

) {
}


