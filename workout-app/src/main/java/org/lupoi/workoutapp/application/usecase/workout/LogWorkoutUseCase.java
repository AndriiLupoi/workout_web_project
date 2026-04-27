package org.lupoi.workoutapp.application.usecase.workout;/*
    @author Andrii
    @project workout
    @class LogWorkoutUseCase
    @version 1.0.0
    @since 27.04.2026 - 20.40
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.command.LogWorkoutCommand;
import org.lupoi.workoutapp.domain.entity.LoggedExercise;
import org.lupoi.workoutapp.domain.entity.WorkoutLog;
import org.lupoi.workoutapp.domain.repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LogWorkoutUseCase {

    private final WorkoutLogRepository workoutLogRepository;

    public WorkoutLog execute(String userId, LogWorkoutCommand command) {
        WorkoutLog log = WorkoutLog.builder()
                .userId(userId)
                .planId(command.planId())
                .weekNumber(command.weekNumber())
                .dayNumber(command.dayNumber())
                .exercises(command.exercises().stream()
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
                        .toList())
                .notes(command.notes())
                .completedAt(LocalDateTime.now())
                .build();

        return workoutLogRepository.save(log);
    }
}

