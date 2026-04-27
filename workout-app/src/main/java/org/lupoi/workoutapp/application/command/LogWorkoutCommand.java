package org.lupoi.workoutapp.application.command;/*
    @author Andrii
    @project workout
    @class LogWorkoutCommand
    @version 1.0.0
    @since 27.04.2026 - 20.40
*/

import java.util.List;

public record LogWorkoutCommand(
        String planId,
        int weekNumber,
        int dayNumber,
        List<LoggedExerciseCommand> exercises,
        String notes
) {
    public record LoggedExerciseCommand(
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

