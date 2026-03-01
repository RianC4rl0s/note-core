package com.note_core.admin.dto;

import com.note_core.user.Permission;

import java.time.Instant;

public record PermissionResponse(
        Long id,
        String name,
        String description,
        boolean builtIn,
        Instant createdAt,
        Instant updatedAt
) {
    public static PermissionResponse from(Permission permission) {
        return new PermissionResponse(
                permission.getId(),
                permission.getName(),
                permission.getDescription(),
                permission.isBuiltIn(),
                permission.getCreatedAt(),
                permission.getUpdatedAt()
        );
    }
}
