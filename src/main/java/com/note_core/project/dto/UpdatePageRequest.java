package com.note_core.project.dto;

import jakarta.validation.constraints.Size;

public record UpdatePageRequest(
        String title,

        @Size(max = 51200, message = "Page data must not exceed 50KB")
        String pageData,

        Integer position
) {
}
