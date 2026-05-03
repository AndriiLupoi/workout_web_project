package org.lupoi.workoutapp.presentation.dto.response;/*
    @author Andrii
    @project workout
    @class ProgressResponse
    @version 1.0.0
    @since 03.05.2026 - 17.00
*/

import java.util.List;

public record ProgressResponse(
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
