package org.lupoi.workoutapp.application.usecase.workout;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.strategy.PlanGenerationStrategy;
import org.lupoi.workoutapp.domain.entity.UserProfile;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;
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

    public WorkoutPlan execute(String userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));

        var exercises = exerciseRepository.findAll();

        WorkoutPlan plan = planGenerationStrategy.generate(profile, exercises);

        return workoutPlanRepository.save(plan);
    }
}
