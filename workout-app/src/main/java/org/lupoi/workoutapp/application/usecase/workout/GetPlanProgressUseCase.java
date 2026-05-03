package org.lupoi.workoutapp.application.usecase.workout;/*
    @author Andrii
    @project workout
    @class GetPlanProgressUseCase
    @version 1.0.0
    @since 03.05.2026 - 13.49
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.WorkoutLog;
import org.lupoi.workoutapp.domain.exception.EntityNotFoundException;
import org.lupoi.workoutapp.domain.repository.WorkoutLogRepository;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.lupoi.workoutapp.presentation.dto.response.PlanProgressResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPlanProgressUseCase {

    private final WorkoutLogRepository workoutLogRepository;
    private final WorkoutPlanRepository workoutPlanRepository;

    public PlanProgressResponse execute(String userId, String planId) {
        var plan = workoutPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new EntityNotFoundException("WorkoutPlan", planId));

        var logs = workoutLogRepository.findByUserIdAndPlanId(userId, planId);

        int totalDays     = plan.getDays().size();
        int completedDays = logs.size();
        int streak        = calculateStreak(logs);

        return new PlanProgressResponse(totalDays, completedDays, streak);
    }

    private int calculateStreak(List<WorkoutLog> logs) {
        if (logs.isEmpty()) return 0;

        List<LocalDate> dates = logs.stream()
                .map(l -> l.getCompletedAt().toLocalDate())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        int streak = 1;
        for (int i = 0; i < dates.size() - 1; i++) {
            if (dates.get(i).minusDays(1).equals(dates.get(i + 1))) {
                streak++;
            } else break;
        }
        return streak;
    }
}
