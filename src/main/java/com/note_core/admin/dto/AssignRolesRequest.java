package com.note_core.admin.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AssignRolesRequest(
        @NotNull(message = "Role IDs are required")
        Set<Long> roleIds
) {
}
