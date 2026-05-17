package org.lupoi.workoutapp.application.usecase.workout.progress;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.WorkoutLog;
import org.lupoi.workoutapp.domain.model.ProgressResult;
import org.lupoi.workoutapp.domain.repository.BodyWeightLogRepository;
import org.lupoi.workoutapp.domain.repository.WorkoutLogRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GetProgressUseCase {

    private final WorkoutLogRepository workoutLogRepository;
    private final BodyWeightLogRepository bodyWeightLogRepository;

    public ProgressResult execute(String userId) {
        List<WorkoutLog> logs = workoutLogRepository.findByUserId(userId);

        // --- Прогрес по вправах ---
        Map<String, List<ProgressResult.WeightEntry>> byExercise = new LinkedHashMap<>();

        logs.stream()
                .sorted(Comparator.comparing(WorkoutLog::getCompletedAt))
                .forEach(log -> {
                    String dateStr = log.getCompletedAt().toLocalDate().toString();
                    log.getExercises().forEach(ex -> {
                        if (ex.getActualWeight() != null && ex.getActualWeight() > 0) {
                            String key = ex.getExerciseId() + "|" + ex.getExerciseName();
                            byExercise
                                    .computeIfAbsent(key, k -> new ArrayList<>())
                                    .add(new ProgressResult.WeightEntry(
                                            dateStr,
                                            ex.getActualWeight(),
                                            log.getWeekNumber(),
                                            log.getDayNumber()
                                    ));
                        }
                    });
                });

        List<ProgressResult.ExerciseProgressItem> exerciseProgress = byExercise.entrySet().stream()
                .filter(e -> e.getValue().size() >= 2)
                .map(e -> {
                    String[] parts = e.getKey().split("\\|", 2);
                    return new ProgressResult.ExerciseProgressItem(
                            parts[0], parts[1], e.getValue()
                    );
                })
                .sorted(Comparator.comparingInt(e -> -e.entries().size()))
                .limit(10)
                .collect(Collectors.toList());

        // --- Реальна історія ваги тіла ---
        List<ProgressResult.BodyWeightItem> bodyWeightHistory =
                bodyWeightLogRepository.findByUserId(userId)
                        .stream()
                        .map(log -> new ProgressResult.BodyWeightItem(
                                log.getDate().toString(),
                                log.getWeight()
                        ))
                        .collect(Collectors.toList());

        // --- PR по кожній вправі ---
        List<ProgressResult.PrItem> prs = byExercise.entrySet().stream()
                .map(e -> {
                    String[] parts = e.getKey().split("\\|", 2);
                    return e.getValue().stream()
                            .max(Comparator.comparingDouble(ProgressResult.WeightEntry::weight))
                            .map(entry -> new ProgressResult.PrItem(
                                    parts[0], parts[1], entry.weight(), entry.date()
                            ))
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble(ProgressResult.PrItem::maxWeight).reversed())
                .collect(Collectors.toList());

        return new ProgressResult(exerciseProgress, bodyWeightHistory, prs);
    }
}
