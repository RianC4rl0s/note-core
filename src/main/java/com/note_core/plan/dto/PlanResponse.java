package com.note_core.plan.dto;

import com.note_core.plan.Plan;

import java.time.Instant;

public record PlanResponse(
        Long id,
        String name,
        String description,
        int maxProjects,
        int maxPagesPerProject,
        boolean builtIn,
        Instant createdAt,
        Instant updatedAt
) {
    public static PlanResponse from(Plan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getMaxProjects(),
                plan.getMaxPagesPerProject(),
                plan.isBuiltIn(),
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }
}
