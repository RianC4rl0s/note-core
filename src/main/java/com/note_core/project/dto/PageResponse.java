package com.note_core.project.dto;

import com.note_core.project.Page;

import java.time.Instant;
import java.util.UUID;

public record PageResponse(
        UUID id,
        UUID projectId,
        String title,
        String pageData,
        int position,
        Instant createdAt,
        Instant updatedAt
) {
    public static PageResponse from(Page page) {
        return new PageResponse(
                page.getId(),
                page.getProject().getId(),
                page.getTitle(),
                page.getPageData(),
                page.getPosition(),
                page.getCreatedAt(),
                page.getUpdatedAt()
        );
    }
}
