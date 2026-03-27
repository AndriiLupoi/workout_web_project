package org.lupoi.workoutapp.presentation.dto.response;


public record ProfileResponse(
        String id,
        String userId,
        String goal,
        String level,
        int workoutsPerWeek,
        Double currentWeight,
        Double targetWeight,
        Double height,
        Integer age
) {}