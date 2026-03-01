package com.note_core.admin.dto;

import jakarta.validation.constraints.NotNull;

import java.util.Set;

public record AssignPermissionsRequest(
        @NotNull(message = "Permission IDs are required")
        Set<Long> permissionIds
) {
}
