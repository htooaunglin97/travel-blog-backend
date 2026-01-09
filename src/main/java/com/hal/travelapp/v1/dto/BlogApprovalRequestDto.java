package com.hal.travelapp.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BlogApprovalRequestDto(
        @NotNull(message = "Blog ID is required")
        Long blogId,

        @NotBlank(message = "Action is required (APPROVE or REJECT)")
        String action,

        String rejectionReason
) {
}



