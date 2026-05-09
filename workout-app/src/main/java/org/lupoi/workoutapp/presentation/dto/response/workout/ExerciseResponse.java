package org.lupoi.workoutapp.presentation.dto.response.workout;

public record ExerciseResponse(
        String id,
        String name,
        String muscleGroup,
        String difficulty,
        String equipmentType,
        String description,
        String videoUrl
) {}
