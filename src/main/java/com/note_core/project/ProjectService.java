package com.note_core.project;

import com.note_core.common.exception.BusinessException;
import com.note_core.common.exception.ResourceNotFoundException;
import com.note_core.plan.Plan;
import com.note_core.project.dto.CreateProjectRequest;
import com.note_core.project.dto.ProjectResponse;
import com.note_core.project.dto.UpdateProjectRequest;
import com.note_core.user.User;
import com.note_core.user.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ProjectService {

    private static final int DEFAULT_MAX_PROJECTS = 10;

    private final ProjectRepository projectRepository;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository, UserService userService) {
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public Page<ProjectResponse> findAll(Pageable pageable) {
        User user = userService.getLoggedUser();
        return projectRepository.findByUserIdOrderByCreatedAtDesc(user.getId(), pageable)
                .map(ProjectResponse::from);
    }

    @Transactional(readOnly = true)
    public ProjectResponse findById(UUID id) {
        User user = userService.getLoggedUser();
        return projectRepository.findByIdAndUserId(id, user.getId())
                .map(ProjectResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }

    @Transactional
    public ProjectResponse create(CreateProjectRequest request) {
        User user = userService.getLoggedUser();

        int maxProjects = DEFAULT_MAX_PROJECTS;
        Plan plan = user.getPlan();
        if (plan != null) {
            maxProjects = plan.getMaxProjects();
        }

        long currentCount = projectRepository.countByUserId(user.getId());
        if (currentCount >= maxProjects) {
            throw new BusinessException("Project limit reached. Maximum allowed: " + maxProjects);
        }

        Project project = new Project();
        project.setUserId(user.getId());
        project.setName(request.name());
        project.setDescription(request.description());

        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public ProjectResponse update(UUID id, UpdateProjectRequest request) {
        User user = userService.getLoggedUser();
        Project project = projectRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        if (request.name() != null) {
            project.setName(request.name());
        }
        if (request.description() != null) {
            project.setDescription(request.description());
        }

        return ProjectResponse.from(projectRepository.save(project));
    }

    @Transactional
    public void delete(UUID id) {
        User user = userService.getLoggedUser();
        Project project = projectRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));

        projectRepository.delete(project);
    }

    @Transactional(readOnly = true)
    public Project getProjectForUser(UUID projectId, UUID userId) {
        return projectRepository.findByIdAndUserId(projectId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
    }
}
