package org.lupoi.workoutapp.presentation.dto.request;/*
    @author Andrii
    @project workout
    @class ExerciseRequest
    @version 1.0.0
    @since 06.05.2026 - 11.26
*/

public record ExerciseRequest(
        String name,
        String muscleGroup,
        String difficulty,
        String equipmentType,
        String description,
        String videoUrl
) {}
