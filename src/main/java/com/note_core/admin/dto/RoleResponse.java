package com.note_core.admin.dto;

import com.note_core.user.Permission;
import com.note_core.user.Role;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

public record RoleResponse(
        Long id,
        String name,
        String description,
        boolean builtIn,
        Set<String> permissions,
        Instant createdAt,
        Instant updatedAt
) {
    public static RoleResponse from(Role role) {
        return new RoleResponse(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.isBuiltIn(),
                role.getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet()),
                role.getCreatedAt(),
                role.getUpdatedAt()
        );
    }
}
