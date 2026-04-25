package org.lupoi.workoutapp.presentation.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.lupoi.workoutapp.domain.enums.FitnessLevel;
import org.lupoi.workoutapp.domain.enums.PlanType;
import org.lupoi.workoutapp.domain.enums.TrainingGoal;

import java.util.List;

public record SaveProfileRequest(
        @NotNull(message = "Goal is required")
        TrainingGoal goal,

        @NotNull(message = "Level is required")
        FitnessLevel level,

        @NotNull(message = "Plan type is required")
        PlanType planType,

        @Min(value = 1) @Max(value = 7)
        Integer workoutsPerWeek,

        Double currentWeight,
        Double targetWeight,
        Double height,

        @Min(value = 10) @Max(value = 100)
        Integer age,

        List<String> availableEquipment
) {}