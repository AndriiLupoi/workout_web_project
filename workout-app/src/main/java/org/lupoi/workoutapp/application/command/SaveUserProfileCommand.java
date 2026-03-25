package org.lupoi.workoutapp.application.command;

import org.lupoi.workoutapp.domain.enums.FitnessLevel;
import org.lupoi.workoutapp.domain.enums.TrainingGoal;

public record SaveUserProfileCommand(
        TrainingGoal goal,
        FitnessLevel level,
        int workoutsPerWeek,
        Double currentWeight,
        Double targetWeight,
        Double height,
        Integer age
) {}