package org.lupoi.workoutapp.domain.model;

/*
    @author Andrii
    @project workout
    @class WeightRecommendation
    @version 1.0.0
    @since 07.05.2026
*/
public record WeightRecommendation(
        double weight,
        String label,
        String hint
) {}