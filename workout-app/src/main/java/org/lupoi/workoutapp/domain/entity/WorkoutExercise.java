package org.lupoi.workoutapp.domain.entity;/*
    @author Andrii
    @project workout
    @class WorkoutExercise
    @version 1.0.0
    @since 24.03.2026 - 14.43
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExercise {
    private String exerciseId;
    private String exerciseName;
    private int sets;
    private String reps;
    private int restSeconds;
}