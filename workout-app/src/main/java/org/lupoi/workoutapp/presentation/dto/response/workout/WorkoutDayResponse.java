package org.lupoi.workoutapp.presentation.dto.response.workout;

import java.util.List;

public record WorkoutDayResponse(
        int weekNumber,
        int dayNumber,
        String focus,
        String intensityType,
        List<WorkoutExerciseResponse> exercises
) {}
