package com.hal.travelapp.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdminApprovalRequestDto(
        @NotNull(message = "Request ID is required")
        Long requestId,

        @NotBlank(message = "Action is required (APPROVE or REJECT)")
        String action,

        String rejectionReason
) {
}



