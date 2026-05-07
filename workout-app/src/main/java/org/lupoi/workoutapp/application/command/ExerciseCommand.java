package org.lupoi.workoutapp.application.command;/*
    @author Andrii
    @project workout
    @class ExerciseCommand
    @version 1.0.0
    @since 07.05.2026 - 19.27
*/

public record ExerciseCommand(
        String name,
        String muscleGroup,
        String difficulty,
        String equipmentType,
        String description,
        String videoUrl
) {}

