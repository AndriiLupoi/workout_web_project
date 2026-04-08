package org.lupoi.workoutapp.application.command;

import org.lupoi.workoutapp.domain.enums.FitnessLevel;
import org.lupoi.workoutapp.domain.enums.TrainingGoal;

import java.util.List;

public record SaveUserProfileCommand(
        TrainingGoal goal,
        FitnessLevel level,
        Integer workoutsPerWeek,
        Double currentWeight,
        Double targetWeight,
        Double height,
        Integer age,
        List<String> availableEquipment
) {}