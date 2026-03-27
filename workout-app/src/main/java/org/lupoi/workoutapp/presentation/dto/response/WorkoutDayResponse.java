package org.lupoi.workoutapp.presentation.dto.response;

import java.util.List;

public record WorkoutDayResponse(
        int dayNumber,
        String focus,
        List<WorkoutExerciseResponse> exercises
) {}
