package org.lupoi.workoutapp.domain.model;/*
    @author Andrii
    @project workout
    @class PlanProgressResult
    @version 1.0.0
    @since 07.05.2026 - 19.26
*/

public record PlanProgressResult(
        int totalDays,
        int completedDays,
        int currentStreak
) {}

