package com.note_core.project;

import com.note_core.common.exception.BusinessException;
import com.note_core.common.exception.ResourceNotFoundException;
import com.note_core.plan.Plan;
import com.note_core.project.dto.CreatePageRequest;
import com.note_core.project.dto.PageResponse;
import com.note_core.project.dto.UpdatePageRequest;
import com.note_core.user.User;
import com.note_core.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PageService {

    private static final int DEFAULT_MAX_PAGES_PER_PROJECT = 50;

    private final PageRepository pageRepository;
    private final ProjectService projectService;
    private final UserService userService;

    public PageService(PageRepository pageRepository, ProjectService projectService, UserService userService) {
        this.pageRepository = pageRepository;
        this.projectService = projectService;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<PageResponse> findByProjectId(UUID projectId) {
        User user = userService.getLoggedUser();
        projectService.getProjectForUser(projectId, user.getId());

        return pageRepository.findByProjectIdOrderByPositionAsc(projectId).stream()
                .map(PageResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PageResponse findById(UUID projectId, UUID pageId) {
        User user = userService.getLoggedUser();
        projectService.getProjectForUser(projectId, user.getId());

        return pageRepository.findByIdAndProjectId(pageId, projectId)
                .map(PageResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found"));
    }

    @Transactional
    public PageResponse create(UUID projectId, CreatePageRequest request) {
        User user = userService.getLoggedUser();
        Project project = projectService.getProjectForUser(projectId, user.getId());

        int maxPages = DEFAULT_MAX_PAGES_PER_PROJECT;
        Plan plan = user.getPlan();
        if (plan != null) {
            maxPages = plan.getMaxPagesPerProject();
        }

        long currentCount = pageRepository.countByProjectId(projectId);
        if (currentCount >= maxPages) {
            throw new BusinessException("Page limit reached. Maximum allowed per project: " + maxPages);
        }

        Page page = new Page();
        page.setProject(project);
        page.setTitle(request.title());
        page.setPageData(request.pageData());
        page.setPosition(request.position() != null ? request.position() : (int) currentCount);

        return PageResponse.from(pageRepository.save(page));
    }

    @Transactional
    public PageResponse update(UUID projectId, UUID pageId, UpdatePageRequest request) {
        User user = userService.getLoggedUser();
        projectService.getProjectForUser(projectId, user.getId());

        Page page = pageRepository.findByIdAndProjectId(pageId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found"));

        if (request.title() != null) {
            page.setTitle(request.title());
        }
        if (request.pageData() != null) {
            page.setPageData(request.pageData());
        }
        if (request.position() != null) {
            page.setPosition(request.position());
        }

        return PageResponse.from(pageRepository.save(page));
    }

    @Transactional
    public void delete(UUID projectId, UUID pageId) {
        User user = userService.getLoggedUser();
        projectService.getProjectForUser(projectId, user.getId());

        Page page = pageRepository.findByIdAndProjectId(pageId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Page not found"));

        pageRepository.delete(page);
    }
}
