package org.lupoi.workoutapp.presentation.dto.response;


import java.util.List;

public record ProfileResponse(
        String id,
        String userId,
        String goal,
        String level,
        String planType,
        Integer workoutsPerWeek,
        Double currentWeight,
        Double targetWeight,
        Double height,
        Integer age,
        List<String> availableEquipment
) {}