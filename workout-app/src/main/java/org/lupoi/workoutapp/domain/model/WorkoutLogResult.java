package org.lupoi.workoutapp.domain.model;

import java.util.List;

/*
    @author Andrii
    @project workout
    @class WorkoutLogResult
    @version 1.0.0
    @since 07.05.2026
*/
public record WorkoutLogResult(
        String logId,
        int prCount,
        List<ExercisePrInfo> personalRecords
) {
    public record ExercisePrInfo(
            String exerciseId,
            String exerciseName,
            double newWeight,
            Double previousBest,
            Double delta
    ) {}
}