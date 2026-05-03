package org.lupoi.workoutapp.presentation.dto.response;/*
    @author Andrii
    @project workout
    @class PlanProgressResponse
    @version 1.0.0
    @since 03.05.2026 - 13.46
*/

public record PlanProgressResponse(
        int totalDays,
        int completedDays,
        int currentStreak
) {}
