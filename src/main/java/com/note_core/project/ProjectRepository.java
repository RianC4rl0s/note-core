package com.note_core.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    Page<Project> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    Optional<Project> findByIdAndUserId(UUID id, UUID userId);

    long countByUserId(UUID userId);
}
