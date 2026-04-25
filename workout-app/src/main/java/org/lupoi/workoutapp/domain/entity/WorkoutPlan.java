package org.lupoi.workoutapp.domain.entity;/*
    @author Andrii
    @project workout
    @class WorkoutPlan
    @version 1.0.0
    @since 24.03.2026 - 14.42
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lupoi.workoutapp.domain.enums.PlanStatus;
import org.lupoi.workoutapp.domain.enums.PlanType;
import org.lupoi.workoutapp.domain.enums.TrainingGoal;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlan {
    private String id;
    private String userId;
    private String title;
    private TrainingGoal goal;
    private PlanType planType;
    private int durationWeeks;
    private PlanStatus status;
    private List<WorkoutDay> days;
    private LocalDateTime createdAt;
}