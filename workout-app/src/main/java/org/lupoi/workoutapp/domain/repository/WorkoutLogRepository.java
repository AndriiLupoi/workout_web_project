package org.lupoi.workoutapp.domain.repository;/*
    @author Andrii
    @project workout
    @class WorkoutLogRepository
    @version 1.0.0
    @since 27.04.2026 - 20.39
*/

import org.lupoi.workoutapp.domain.entity.logs.WorkoutLog;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkoutLogRepository {
    WorkoutLog save(WorkoutLog log);
    List<WorkoutLog> findByUserIdAndPlanId(String userId, String planId);
    List<WorkoutLog> findByUserId(String userId);
    Optional<WorkoutLog> findByUserIdAndPlanIdAndWeekAndDay(
            String userId, String planId, int weekNumber, int dayNumber);

    List<WorkoutLog> findByUserIdAndCompletedAtBetween(
            String userId, LocalDateTime from, LocalDateTime to);

}

