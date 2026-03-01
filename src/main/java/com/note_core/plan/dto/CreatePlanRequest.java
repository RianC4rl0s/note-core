package com.note_core.plan.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreatePlanRequest(
        @NotBlank(message = "Name is required")
        String name,

        String description,

        @Min(value = 1, message = "Max projects must be at least 1")
        int maxProjects,

        @Min(value = 1, message = "Max pages per project must be at least 1")
        int maxPagesPerProject
) {
}
