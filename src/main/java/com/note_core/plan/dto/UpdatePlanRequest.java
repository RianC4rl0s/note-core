package com.note_core.plan.dto;

import jakarta.validation.constraints.Min;

public record UpdatePlanRequest(
        String name,

        String description,

        @Min(value = 1, message = "Max projects must be at least 1")
        Integer maxProjects,

        @Min(value = 1, message = "Max pages per project must be at least 1")
        Integer maxPagesPerProject
) {
}
