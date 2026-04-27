package org.lupoi.workoutapp.presentation.dto.response;/*
    @author Andrii
    @project workout
    @class WorkoutLogResponse
    @version 1.0.0
    @since 27.04.2026 - 20.56
*/

import java.time.LocalDateTime;
import java.util.List;

public record WorkoutLogResponse(
        String id,
        String planId,
        int weekNumber,
        int dayNumber,
        List<LoggedExerciseResponse> exercises,
        String notes,
        LocalDateTime completedAt
) {
    public record LoggedExerciseResponse(
            String exerciseId,
            String exerciseName,
            int plannedSets,
            String plannedReps,
            Double plannedWeight,
            int actualSets,
            String actualReps,
            Double actualWeight,
            boolean feltEasy,
            String notes
    ) {}
}

