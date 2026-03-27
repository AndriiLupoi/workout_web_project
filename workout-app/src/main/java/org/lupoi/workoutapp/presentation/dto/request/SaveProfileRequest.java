package org.lupoi.workoutapp.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.lupoi.workoutapp.domain.enums.FitnessLevel;
import org.lupoi.workoutapp.domain.enums.TrainingGoal;

public record SaveProfileRequest(
        @NotNull(message = "Goal is required")
        TrainingGoal goal,

        @NotNull(message = "Level is required")
        FitnessLevel level,

        @Min(value = 1) @Max(value = 7)
        int workoutsPerWeek,

        Double currentWeight,
        Double targetWeight,
        Double height,

        @Min(value = 10) @Max(value = 100)
        Integer age
) {}