package org.lupoi.workoutapp.presentation.dto.response;/*
    @author Andrii
    @project workout
    @class StatsResponse
    @version 1.0.0
    @since 03.05.2026 - 12.08
*/

public record StatsResponse(
        long totalUsers,
        long totalPlans,
        long totalAdmins
) {}
