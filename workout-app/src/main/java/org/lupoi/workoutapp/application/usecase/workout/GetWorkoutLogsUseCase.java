package org.lupoi.workoutapp.application.usecase.workout;/*
    @author Andrii
    @project workout
    @class GetWorkoutLogsUseCase
    @version 1.0.0
    @since 27.04.2026 - 20.42
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.WorkoutLog;
import org.lupoi.workoutapp.domain.repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetWorkoutLogsUseCase {

    private final WorkoutLogRepository workoutLogRepository;

    /** All logs for a specific plan */
    public List<WorkoutLog> executeByPlan(String userId, String planId) {
        return workoutLogRepository.findByUserIdAndPlanId(userId, planId);
    }

    /** All logs for the user across all plans */
    public List<WorkoutLog> executeAll(String userId) {
        return workoutLogRepository.findByUserId(userId);
    }

    /** Single log for a specific week+day (to check if already completed) */
    public Optional<WorkoutLog> executeForDay(
            String userId, String planId, int week, int day) {
        return workoutLogRepository.findByUserIdAndPlanIdAndWeekAndDay(
                userId, planId, week, day);
    }
}

