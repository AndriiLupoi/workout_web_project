package org.lupoi.workoutapp.presentation.dto.response.workout;

public record WorkoutExerciseResponse(
        String exerciseId,
        String exerciseName,
        int sets,
        String reps,
        int restSeconds,
        Double plannedWeight
) {}