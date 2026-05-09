package org.lupoi.workoutapp.application.usecase.workout.logs;/*
    @author Andrii
    @project workout
    @class LogWorkoutUseCase
    @version 1.0.0
    @since 27.04.2026 - 20.40
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.command.LogWorkoutCommand;
import org.lupoi.workoutapp.application.service.AuditService;
import org.lupoi.workoutapp.domain.entity.logs.LoggedExercise;
import org.lupoi.workoutapp.domain.entity.logs.WorkoutLog;
import org.lupoi.workoutapp.domain.enums.AuditAction;
import org.lupoi.workoutapp.domain.model.WorkoutLogResult;
import org.lupoi.workoutapp.domain.repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogWorkoutUseCase {

    private final WorkoutLogRepository workoutLogRepository;
    private final AuditService auditService;

    public WorkoutLogResult execute(String userId, LogWorkoutCommand command) {
        List<WorkoutLog> previousLogs = workoutLogRepository.findByUserIdAndPlanId(userId, command.planId());

        List<LoggedExercise> exercises = command.exercises().stream()
                .map(e -> LoggedExercise.builder()
                        .exerciseId(e.exerciseId())
                        .exerciseName(e.exerciseName())
                        .plannedSets(e.plannedSets())
                        .plannedReps(e.plannedReps())
                        .plannedWeight(e.plannedWeight())
                        .actualSets(e.actualSets())
                        .actualReps(e.actualReps())
                        .actualWeight(e.actualWeight())
                        .felt_easy(e.feltEasy())
                        .notes(e.notes())
                        .build())
                .toList();

        WorkoutLog log = WorkoutLog.builder()
                .userId(userId)
                .planId(command.planId())
                .weekNumber(command.weekNumber())
                .dayNumber(command.dayNumber())
                .exercises(exercises)
                .notes(command.notes())
                .completedAt(LocalDateTime.now())
                .build();

        WorkoutLog saved = workoutLogRepository.save(log);

        // Визначаємо PR
        List<WorkoutLogResult.ExercisePrInfo> prs = new ArrayList<>();
        for (LoggedExercise ex : exercises) {
            if (ex.getActualWeight() == null || ex.getActualWeight() <= 0) continue;

            Double prevBest = findBestWeight(previousLogs, ex.getExerciseId());
            if (prevBest == null || ex.getActualWeight() > prevBest) {
                Double delta = prevBest != null
                        ? Math.round((ex.getActualWeight() - prevBest) * 10.0) / 10.0
                        : null;
                prs.add(new WorkoutLogResult.ExercisePrInfo(
                        ex.getExerciseId(),
                        ex.getExerciseName(),
                        ex.getActualWeight(),
                        prevBest,
                        delta
                ));
            }
        }

        auditService.log(
                userId,
                null,
                "USER",
                AuditAction.WORKOUT_LOGGED,
                saved.getId(),
                "WorkoutLog",
                String.format("Тиждень %d, День %d, план: %s, PR: %d",
                        command.weekNumber(), command.dayNumber(), command.planId(), prs.size())
        );

        return new WorkoutLogResult(saved.getId(), prs.size(), prs);
    }

    private Double findBestWeight(List<WorkoutLog> logs, String exerciseId) {
        Double best = null;
        for (WorkoutLog log : logs) {
            for (LoggedExercise ex : log.getExercises()) {
                if (ex.getExerciseId().equals(exerciseId) && ex.getActualWeight() != null) {
                    if (best == null || ex.getActualWeight() > best) {
                        best = ex.getActualWeight();
                    }
                }
            }
        }
        return best;
    }
}

