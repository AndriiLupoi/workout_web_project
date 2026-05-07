package org.lupoi.workoutapp.domain.model;/*
    @author Andrii
    @project workout
    @class ProgressResult
    @version 1.0.0
    @since 07.05.2026 - 19.27
*/

import java.util.List;

public record ProgressResult(
        List<ExerciseProgressItem> exerciseProgress,
        List<BodyWeightItem> bodyWeightHistory,
        List<PrItem> personalRecords
) {
    public record ExerciseProgressItem(
            String exerciseId,
            String exerciseName,
            List<WeightEntry> entries
    ) {}

    public record WeightEntry(
            String date,
            Double weight,
            int weekNumber,
            int dayNumber
    ) {}

    public record BodyWeightItem(
            String date,
            Double weight
    ) {}

    public record PrItem(
            String exerciseId,
            String exerciseName,
            Double maxWeight,
            String date
    ) {}
}

