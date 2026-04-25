package org.lupoi.workoutapp.domain.entity;/*
    @author Andrii
    @project workout
    @class WorkoutDay
    @version 1.0.0
    @since 24.03.2026 - 14.43
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lupoi.workoutapp.domain.enums.IntensityType;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDay {
    private int dayNumber;
    private int weekNumber;
    private String focus;
    private IntensityType intensityType;
    private List<WorkoutExercise> exercises;
}