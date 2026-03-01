package com.note_core.project;

import com.note_core.project.dto.CreatePageRequest;
import com.note_core.project.dto.PageResponse;
import com.note_core.project.dto.UpdatePageRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/projects/{projectId}/pages")
public class PageController {

    private final PageService pageService;

    public PageController(PageService pageService) {
        this.pageService = pageService;
    }

    @GetMapping
    public ResponseEntity<List<PageResponse>> findByProjectId(@PathVariable UUID projectId) {
        return ResponseEntity.ok(pageService.findByProjectId(projectId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PageResponse> findById(@PathVariable UUID projectId, @PathVariable UUID id) {
        return ResponseEntity.ok(pageService.findById(projectId, id));
    }

    @PostMapping
    public ResponseEntity<PageResponse> create(@PathVariable UUID projectId,
                                               @Valid @RequestBody CreatePageRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pageService.create(projectId, request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PageResponse> update(@PathVariable UUID projectId,
                                               @PathVariable UUID id,
                                               @Valid @RequestBody UpdatePageRequest request) {
        return ResponseEntity.ok(pageService.update(projectId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID projectId, @PathVariable UUID id) {
        pageService.delete(projectId, id);
        return ResponseEntity.noContent().build();
    }
}
