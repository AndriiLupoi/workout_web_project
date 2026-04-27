package org.lupoi.workoutapp.presentation.dto.request;/*
    @author Andrii
    @project workout
    @class LogWorkoutRequest
    @version 1.0.0
    @since 27.04.2026 - 20.56
*/

import java.util.List;

public record LogWorkoutRequest(
        String planId,
        int weekNumber,
        int dayNumber,
        List<LoggedExerciseRequest> exercises,
        String notes
) {
    public record LoggedExerciseRequest(
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

