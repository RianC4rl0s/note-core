package com.note_core.user.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateUserRequest(
        @Size(min = 1, message = "Name must not be empty")
        String name,

        String phone,

        LocalDate birthDate,

        String avatarUrl
) {}
