package com.note_core.project.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateProjectRequest(
        @NotBlank(message = "Name is required")
        String name,

        String description
) {
}
