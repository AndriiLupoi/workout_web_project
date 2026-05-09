package org.lupoi.workoutapp.presentation.dto.response.logs;/*
    @author Andrii
    @project workout
    @class WorkoutLogResultResponse
    @version 1.0.0
    @since 07.05.2026 - 19.53
*/

import java.util.List;

public record WorkoutLogResultResponse(
        String logId,
        int prCount,
        List<ExercisePrResponse> personalRecords
) {
    public record ExercisePrResponse(
            String exerciseId,
            String exerciseName,
            double newWeight,
            Double previousBest,
            Double delta
    ) {}
}

