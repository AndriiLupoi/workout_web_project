package org.lupoi.workoutapp.application.usecase.workout.plan;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.service.AuditService;
import org.lupoi.workoutapp.application.strategy.PlanGenerationStrategy;
import org.lupoi.workoutapp.domain.entity.user.UserProfile;
import org.lupoi.workoutapp.domain.entity.workout.WorkoutPlan;
import org.lupoi.workoutapp.domain.enums.AuditAction;
import org.lupoi.workoutapp.domain.exception.ProfileNotFoundException;
import org.lupoi.workoutapp.domain.repository.ExerciseRepository;
import org.lupoi.workoutapp.domain.repository.UserProfileRepository;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerateWorkoutPlanUseCase {

    private final UserProfileRepository userProfileRepository;
    private final ExerciseRepository exerciseRepository;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final PlanGenerationStrategy planGenerationStrategy;
    private final AuditService auditService;

    public WorkoutPlan execute(String userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        var exercises = exerciseRepository.findAll();

        WorkoutPlan plan = planGenerationStrategy.generate(profile, exercises);
        WorkoutPlan saved = workoutPlanRepository.save(plan);

        auditService.log(
                userId,
                null,
                "USER",
                AuditAction.PLAN_GENERATED,
                saved.getId(),
                "WorkoutPlan",
                "Згенеровано план: " + saved.getTitle()
        );

        return saved;
    }

}
