package org.lupoi.workoutapp.presentation.dto.response;

public record WorkoutExerciseResponse(
        String exerciseId,
        String exerciseName,
        int sets,
        String reps,
        int restSeconds,
        Double plannedWeight
) {}