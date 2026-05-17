package org.lupoi.workoutapp.application.usecase.workout.plan;/*
    @author Andrii
    @project workout
    @class GetTrainingDaysUseCase
    @version 1.0.0
    @since 10.05.2026 - 15.22
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.WorkoutLog;
import org.lupoi.workoutapp.domain.repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetTrainingDaysUseCase {

    private final WorkoutLogRepository workoutLogRepository;

    public List<Integer> execute(String userId) {
        LocalDate today = LocalDate.now();

        // Початок поточного тижня (Понеділок 00:00)
        LocalDateTime weekStart = today
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay();

        // Кінець поточного тижня (Неділя 23:59:59)
        LocalDateTime weekEnd = today
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .atTime(23, 59, 59);

        List<WorkoutLog> logs = workoutLogRepository
                .findByUserIdAndCompletedAtBetween(userId, weekStart, weekEnd);

        // completedAt.getDayOfWeek().getValue() → 1(Пн)..7(Нд), нам треба 0..6
        return logs.stream()
                .map(log -> log.getCompletedAt().getDayOfWeek().getValue() - 1)
                .distinct()
                .sorted()
                .toList();
    }
}

