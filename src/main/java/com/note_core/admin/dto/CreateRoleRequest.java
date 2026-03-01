package com.note_core.admin.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateRoleRequest(
        @NotBlank(message = "Name is required")
        String name,

        String description
) {
}
