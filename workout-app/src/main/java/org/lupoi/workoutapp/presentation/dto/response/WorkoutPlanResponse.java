package org.lupoi.workoutapp.presentation.dto.response;

import java.util.List;

public record WorkoutPlanResponse(
        String id,
        String title,
        String goal,
        String status,
        int durationWeeks,
        List<WorkoutDayResponse> days,
        String createdAt
) {}