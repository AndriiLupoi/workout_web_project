package org.lupoi.workoutapp.presentation.dto.response;/*
    @author Andrii
    @project workout
    @class WeightProgressResponse
    @version 1.0.0
    @since 07.05.2026 - 19.54
*/

public record WeightProgressResponse(
        double currentWeight,
        double targetWeight,
        int progressPercent,
        String message
) {}
