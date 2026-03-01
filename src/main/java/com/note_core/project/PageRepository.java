package com.note_core.project;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PageRepository extends JpaRepository<Page, UUID> {

    List<Page> findByProjectIdOrderByPositionAsc(UUID projectId);

    Optional<Page> findByIdAndProjectId(UUID id, UUID projectId);

    long countByProjectId(UUID projectId);
}
