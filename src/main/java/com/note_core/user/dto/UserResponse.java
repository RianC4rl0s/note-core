package com.note_core.user.dto;

import com.note_core.user.User;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record UserResponse(
        UUID id,
        String name,
        String email,
        String phone,
        LocalDate birthDate,
        String avatarUrl,
        boolean enabled,
        boolean active,
        Set<String> roles,
        String planName,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                user.getBirthDate(),
                user.getAvatarUrl(),
                user.isEnabled(),
                user.isActive(),
                user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet()),
                user.getPlan() != null ? user.getPlan().getName() : null,
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}
