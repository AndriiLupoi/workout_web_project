package org.lupoi.workoutapp.domain.entity;/*
    @author Andrii
    @project workout
    @class LoggedExercise
    @version 1.0.0
    @since 27.04.2026 - 20.38
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoggedExercise {
    private String exerciseId;
    private String exerciseName;
    // Planned values (copied from WorkoutExercise at log time)
    private int plannedSets;
    private String plannedReps;
    private Double plannedWeight;
    // Actual values entered by the user
    private int actualSets;
    private String actualReps;    // e.g. "8" or "8,8,7" per set
    private Double actualWeight;  // kg
    private boolean felt_easy;    // hint for future plan adjustment
    private String notes;
}

