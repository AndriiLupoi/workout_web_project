package org.lupoi.workoutapp.application.usecase.workout.plan;/*
    @author Andrii
    @project workout
    @class DeleteWorkoutPlanUseCase
    @version 1.0.0
    @since 03.05.2026 - 13.21
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.service.AuditService;
import org.lupoi.workoutapp.domain.enums.AuditAction;
import org.lupoi.workoutapp.domain.exception.EntityNotFoundException;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteWorkoutPlanUseCase {

    private final WorkoutPlanRepository workoutPlanRepository;
    private final AuditService auditService;

    public void execute(String userId, String planId) {
        var plan = workoutPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new EntityNotFoundException("WorkoutPlan", planId));

        workoutPlanRepository.deleteById(planId);

        auditService.log(
                userId,
                null,
                "USER",
                AuditAction.PLAN_DELETED,
                planId,
                "WorkoutPlan",
                "Видалено план: " + plan.getTitle()
        );
    }
}


