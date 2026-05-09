package org.lupoi.workoutapp.presentation.dto.response.workout;/*
    @author Andrii
    @project workout
    @class WeightRecommendationResponse
    @version 1.0.0
    @since 07.05.2026 - 19.54
*/

public record WeightRecommendationResponse(
        double weight,
        String label,
        String hint
) {}
