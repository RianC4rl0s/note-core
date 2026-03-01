package com.note_core.plan;

import com.note_core.common.exception.BusinessException;
import com.note_core.common.exception.ResourceNotFoundException;
import com.note_core.plan.dto.CreatePlanRequest;
import com.note_core.plan.dto.PlanResponse;
import com.note_core.plan.dto.UpdatePlanRequest;
import com.note_core.user.User;
import com.note_core.user.UserRepository;
import com.note_core.user.dto.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final UserRepository userRepository;

    public PlanService(PlanRepository planRepository, UserRepository userRepository) {
        this.planRepository = planRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<PlanResponse> findAll() {
        return planRepository.findAll().stream()
                .map(PlanResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public PlanResponse findById(Long id) {
        return planRepository.findById(id)
                .map(PlanResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));
    }

    @Transactional
    public PlanResponse create(CreatePlanRequest request) {
        if (planRepository.existsByName(request.name())) {
            throw new BusinessException("Plan name already exists");
        }

        Instant now = Instant.now();
        Plan plan = new Plan();
        plan.setName(request.name());
        plan.setDescription(request.description());
        plan.setMaxProjects(request.maxProjects());
        plan.setMaxPagesPerProject(request.maxPagesPerProject());
        plan.setBuiltIn(false);
        plan.setCreatedAt(now);
        plan.setUpdatedAt(now);

        return PlanResponse.from(planRepository.save(plan));
    }

    @Transactional
    public PlanResponse update(Long id, UpdatePlanRequest request) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        if (plan.isBuiltIn()) {
            throw new BusinessException("Built-in plans cannot be edited");
        }

        if (request.name() != null) {
            if (!request.name().equals(plan.getName()) && planRepository.existsByName(request.name())) {
                throw new BusinessException("Plan name already exists");
            }
            plan.setName(request.name());
        }
        if (request.description() != null) {
            plan.setDescription(request.description());
        }
        if (request.maxProjects() != null) {
            plan.setMaxProjects(request.maxProjects());
        }
        if (request.maxPagesPerProject() != null) {
            plan.setMaxPagesPerProject(request.maxPagesPerProject());
        }
        plan.setUpdatedAt(Instant.now());

        return PlanResponse.from(planRepository.save(plan));
    }

    @Transactional
    public void delete(Long id) {
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        if (plan.isBuiltIn()) {
            throw new BusinessException("Built-in plans cannot be deleted");
        }

        planRepository.delete(plan);
    }

    @Transactional
    public UserResponse assignPlanToUser(UUID userId, Long planId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan not found"));

        user.setPlan(plan);
        return UserResponse.from(userRepository.save(user));
    }
}
