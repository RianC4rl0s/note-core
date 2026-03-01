package com.note_core.auth.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ImpersonateRequest(
        @NotNull(message = "Target user ID is required")
        UUID targetUserId
) {}
