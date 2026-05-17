package org.lupoi.workoutapp.application.strategy;




import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.entity.UserProfile;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;

import java.util.List;

public interface PlanGenerationStrategy {
    WorkoutPlan generate(UserProfile profile, List<Exercise> exercises);
}