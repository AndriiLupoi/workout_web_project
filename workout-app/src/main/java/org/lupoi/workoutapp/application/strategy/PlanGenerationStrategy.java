package org.lupoi.workoutapp.application.strategy;




import org.lupoi.workoutapp.domain.entity.workout.Exercise;
import org.lupoi.workoutapp.domain.entity.user.UserProfile;
import org.lupoi.workoutapp.domain.entity.workout.WorkoutPlan;

import java.util.List;

public interface PlanGenerationStrategy {
    WorkoutPlan generate(UserProfile profile, List<Exercise> exercises);
}