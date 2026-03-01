package com.note_core.plan.dto;

import jakarta.validation.constraints.NotNull;

public record AssignPlanRequest(
        @NotNull(message = "Plan ID is required")
        Long planId
) {
}
