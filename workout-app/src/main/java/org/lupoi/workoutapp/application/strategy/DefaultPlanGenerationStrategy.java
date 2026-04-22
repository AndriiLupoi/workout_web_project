package org.lupoi.workoutapp.application.strategy;

import org.lupoi.workoutapp.domain.entity.*;
import org.lupoi.workoutapp.domain.enums.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DefaultPlanGenerationStrategy implements PlanGenerationStrategy {

    private static final Map<Integer, List<List<MuscleGroup>>> SPLITS = Map.of(
            3, List.of(
                    List.of(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                    List.of(MuscleGroup.BACK, MuscleGroup.BICEPS),
                    List.of(MuscleGroup.LEGS, MuscleGroup.SHOULDERS)
            ),
            4, List.of(
                    List.of(MuscleGroup.CHEST, MuscleGroup.TRICEPS),
                    List.of(MuscleGroup.BACK, MuscleGroup.BICEPS),
                    List.of(MuscleGroup.LEGS),
                    List.of(MuscleGroup.SHOULDERS, MuscleGroup.ABS)
            ),
            5, List.of(
                    List.of(MuscleGroup.CHEST),
                    List.of(MuscleGroup.BACK),
                    List.of(MuscleGroup.LEGS),
                    List.of(MuscleGroup.SHOULDERS),
                    List.of(MuscleGroup.BICEPS, MuscleGroup.TRICEPS)
            )
    );

    @Override
    public WorkoutPlan generate(UserProfile profile, List<Exercise> exercises) {
        int workoutsPerWeek = profile.getWorkoutsPerWeek();

        int splitKey = SPLITS.keySet().stream()
                .min(Comparator.comparingInt(k -> Math.abs(k - workoutsPerWeek)))
                .orElse(3);

        List<List<MuscleGroup>> split = SPLITS.get(splitKey);
        List<WorkoutDay> days = new ArrayList<>();

        for (int i = 0; i < split.size(); i++) {
            List<MuscleGroup> muscleGroups = split.get(i);
            List<WorkoutExercise> workoutExercises = buildExercises(
                    muscleGroups, exercises, profile
            );

            String focus = muscleGroups.stream()
                    .map(MuscleGroup::name)
                    .collect(Collectors.joining(" + "));

            days.add(WorkoutDay.builder()
                    .dayNumber(i + 1)
                    .focus(focus)
                    .exercises(workoutExercises)
                    .build());
        }

        return WorkoutPlan.builder()
                .userId(profile.getUserId())
                .title(buildTitle(profile.getGoal()))
                .goal(profile.getGoal())
                .durationWeeks(8)
                .status(PlanStatus.ACTIVE)
                .days(days)
                .createdAt(LocalDateTime.now())
                .build();
    }

    private List<WorkoutExercise> buildExercises(List<MuscleGroup> muscleGroups,
                                                 List<Exercise> allExercises,
                                                 UserProfile profile) {
        List<WorkoutExercise> result = new ArrayList<>();

        for (MuscleGroup group : muscleGroups) {
            // фільтруємо вправи по групі м'язів і рівні
            List<Exercise> filtered = allExercises.stream()
                    .filter(e -> e.getMuscleGroup() == group)
                    .filter(e -> isLevelSuitable(e.getDifficulty(), profile.getLevel()))
                    .toList();

            // беремо до 2 вправ на групу м'язів
            filtered.stream().limit(2).forEach(exercise -> {
                int[] setsReps = getSetsAndReps(profile.getGoal());
                int restSeconds = getRestSeconds(profile.getGoal());

                result.add(WorkoutExercise.builder()
                        .exerciseId(String.valueOf(exercise.getId()))
                        .exerciseName(exercise.getName())
                        .sets(setsReps[0])
                        .reps(setsReps[0] + "-" + setsReps[1])
                        .restSeconds(restSeconds)
                        .build());
            });
        }

        return result;
    }

    // початківець може робити beginner і intermediate вправи
    // intermediate може робити всі
    private boolean isLevelSuitable(Difficulty difficulty, FitnessLevel level) {
        if (level == FitnessLevel.BEGINNER) {
            return difficulty == Difficulty.BEGINNER;
        }
        return difficulty == Difficulty.BEGINNER || difficulty == Difficulty.INTERMEDIATE;
    }

    // sets[0] = кількість підходів, sets[1] = верхня межа повторень
    private int[] getSetsAndReps(TrainingGoal goal) {
        return switch (goal) {
            case MASS       -> new int[]{4, 10};  // 4x8-10
            case LOSS       -> new int[]{3, 15};  // 3x12-15
            case ENDURANCE  -> new int[]{3, 20};  // 3x15-20
        };
    }

    private int getRestSeconds(TrainingGoal goal) {
        return switch (goal) {
            case MASS       -> 90;
            case LOSS       -> 45;
            case ENDURANCE  -> 30;
        };
    }

    private String buildTitle(TrainingGoal goal) {
        return switch (goal) {
            case MASS       -> "Mass Building Plan";
            case LOSS       -> "Fat Loss Plan";
            case ENDURANCE  -> "Endurance Plan";
        };
    }
}
