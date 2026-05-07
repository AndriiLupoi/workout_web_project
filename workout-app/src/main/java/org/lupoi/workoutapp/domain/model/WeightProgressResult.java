package org.lupoi.workoutapp.domain.model;

/*
    @author Andrii
    @project workout
    @class WeightProgressResult
    @version 1.0.0
    @since 07.05.2026
*/
public record WeightProgressResult(
        double currentWeight,
        double targetWeight,
        int progressPercent,
        String message
) {}