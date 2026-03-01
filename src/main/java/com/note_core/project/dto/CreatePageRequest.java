package com.note_core.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePageRequest(
        @NotBlank(message = "Title is required")
        String title,

        @Size(max = 51200, message = "Page data must not exceed 50KB")
        String pageData,

        Integer position
) {
}
