package org.lupoi.workoutapp.domain.repository;/*
    @author Andrii
    @project workout
    @class WorkoutPlanRepository
    @version 1.0.0
    @since 24.03.2026 - 14.50
*/

import org.lupoi.workoutapp.domain.entity.WorkoutPlan;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository {
    WorkoutPlan save(WorkoutPlan plan);
    List<WorkoutPlan> findByUserId(String userId);
    Optional<WorkoutPlan> findByIdAndUserId(String id, String userId);

    long countAll();

    void deleteById(String id);
}