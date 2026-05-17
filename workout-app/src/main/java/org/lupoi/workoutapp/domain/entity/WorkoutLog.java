package org.lupoi.workoutapp.domain.entity;/*
    @author Andrii
    @project workout
    @class WorkoutLog
    @version 1.0.0
    @since 27.04.2026 - 20.38
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLog {
    private String id;
    private String userId;
    private String planId;
    private int weekNumber;
    private int dayNumber;
    private List<LoggedExercise> exercises;
    private String notes;
    private LocalDateTime completedAt;
}

