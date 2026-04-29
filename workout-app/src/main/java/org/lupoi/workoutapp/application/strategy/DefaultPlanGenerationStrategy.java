package org.lupoi.workoutapp.application.strategy;

import org.lupoi.workoutapp.domain.entity.*;
import org.lupoi.workoutapp.domain.enums.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DefaultPlanGenerationStrategy implements PlanGenerationStrategy {

    private final Map<String, Integer> exerciseIndexTracker = new HashMap<>();

    // скільки тижнів для кожного типу плану
    private static final Map<PlanType, Integer> PLAN_DURATION = Map.of(
            PlanType.HYPERTROPHY,             8,
            PlanType.STRENGTH,               10,
            PlanType.STRENGTH_HYPERTROPHY,    9,
            PlanType.FAT_LOSS,                8,
            PlanType.ENDURANCE,               8
    );

    // скільки тижнів Full Body на старті залежно від рівня
    private static final Map<FitnessLevel, Integer> FULL_BODY_WEEKS = Map.of(
            FitnessLevel.BEGINNER,     2,
            FitnessLevel.RETURNING,    1,
            FitnessLevel.INTERMEDIATE, 0,
            FitnessLevel.ADVANCED,     0
    );

    // цикл інтенсивності: HEAVY → MEDIUM → SETS → повторюється
    private static final List<IntensityType> INTENSITY_CYCLE = List.of(
            IntensityType.HEAVY,
            IntensityType.MEDIUM,
            IntensityType.SETS
    );

    // Кількість вправ на групу м'язів залежно від пріоритету
    private static final Map<MuscleGroup, Integer> EXERCISE_COUNT = Map.ofEntries(
            // Великі групи — 4 вправи
            Map.entry(MuscleGroup.CHEST,     4),
            Map.entry(MuscleGroup.BACK,      4),
            Map.entry(MuscleGroup.LEGS,      4),
            // Середні — 3 вправи
            Map.entry(MuscleGroup.SHOULDERS, 3),
            Map.entry(MuscleGroup.BICEPS,    3),
            Map.entry(MuscleGroup.TRICEPS,   3),
            // Малі — 2 вправи
            Map.entry(MuscleGroup.FOREARMS,  1),
            Map.entry(MuscleGroup.CALVES,    1),
            Map.entry(MuscleGroup.TRAPS,     1),
            Map.entry(MuscleGroup.ABS,       1)
    );

    private static final Map<Integer, List<List<MuscleGroup>>> SPLITS = Map.of(
            3, List.of(
                    // Груди + Біцепс + Передпліччя
                    List.of(MuscleGroup.CHEST, MuscleGroup.BICEPS, MuscleGroup.FOREARMS),
                    // Спина + Трицепс + Трапеції
                    List.of(MuscleGroup.BACK, MuscleGroup.TRICEPS, MuscleGroup.TRAPS),
                    // Ноги + Плечі + Ікри
                    List.of(MuscleGroup.LEGS, MuscleGroup.SHOULDERS, MuscleGroup.CALVES)
            ),
            4, List.of(
                    List.of(MuscleGroup.CHEST, MuscleGroup.BICEPS, MuscleGroup.FOREARMS),
                    List.of(MuscleGroup.BACK, MuscleGroup.TRICEPS, MuscleGroup.TRAPS),
                    List.of(MuscleGroup.LEGS, MuscleGroup.CALVES),
                    List.of(MuscleGroup.SHOULDERS, MuscleGroup.ABS)
            ),
            5, List.of(
                    List.of(MuscleGroup.CHEST, MuscleGroup.BICEPS, MuscleGroup.FOREARMS),
                    List.of(MuscleGroup.BACK, MuscleGroup.TRICEPS, MuscleGroup.TRAPS),
                    List.of(MuscleGroup.LEGS, MuscleGroup.CALVES),
                    List.of(MuscleGroup.SHOULDERS, MuscleGroup.ABS),
                    List.of(MuscleGroup.BICEPS, MuscleGroup.TRICEPS, MuscleGroup.FOREARMS)
            )
    );

    @Override
    public WorkoutPlan generate(UserProfile profile, List<Exercise> exercises) {

        exerciseIndexTracker.clear();

        PlanType planType = profile.getPlanType() != null
                ? profile.getPlanType()
                : PlanType.HYPERTROPHY;

        int totalWeeks     = PLAN_DURATION.get(planType);
        int fullBodyWeeks  = FULL_BODY_WEEKS.getOrDefault(profile.getLevel(), 0);
        int splitWeeks     = totalWeeks - fullBodyWeeks;

        List<WorkoutDay> allDays = new ArrayList<>();

        // --- Фаза 1: Full Body (якщо BEGINNER або RETURNING) ---
        for (int week = 1; week <= fullBodyWeeks; week++) {
            allDays.addAll(buildFullBodyWeek(week, exercises, profile));
        }

        // --- Фаза 2: Сплітами з циклом інтенсивності ---
        int splitKey = SPLITS.keySet().stream()
                .min(Comparator.comparingInt(k ->
                        Math.abs(k - profile.getWorkoutsPerWeek())))
                .orElse(3);

        List<List<MuscleGroup>> split = SPLITS.get(splitKey);

        for (int week = 1; week <= splitWeeks; week++) {
            int absoluteWeek = fullBodyWeeks + week;
            // цикл: 1→HEAVY, 2→MEDIUM, 3→SETS, 4→HEAVY, ...
            IntensityType intensity = INTENSITY_CYCLE.get((week - 1) % 3);
            allDays.addAll(buildSplitWeek(absoluteWeek, split, exercises, profile, intensity));
        }

        return WorkoutPlan.builder()
                .userId(profile.getUserId())
                .title(buildTitle(planType))
                .goal(profile.getGoal())
                .planType(planType)
                .durationWeeks(totalWeeks)
                .status(PlanStatus.ACTIVE)
                .days(allDays)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ── Full Body тиждень ──
    private List<WorkoutDay> buildFullBodyWeek(int week,
                                               List<Exercise> exercises,
                                               UserProfile profile) {
        List<MuscleGroup> allGroups = List.of(
                MuscleGroup.CHEST, MuscleGroup.BACK,
                MuscleGroup.LEGS,  MuscleGroup.SHOULDERS
        );

        List<WorkoutDay> days = new ArrayList<>();
        int sessionsPerWeek = Math.min(profile.getWorkoutsPerWeek(), 3);

        // Перемішуємо вправи для кожної групи щоб не повторювались
        Map<MuscleGroup, List<Exercise>> exercisePool = new HashMap<>();
        for (MuscleGroup group : allGroups) {
            List<Exercise> pool = exercises.stream()
                    .filter(e -> e.getMuscleGroup() == group)
                    .filter(e -> e.getDifficulty() == Difficulty.BEGINNER)
                    .collect(Collectors.toCollection(ArrayList::new));
            Collections.shuffle(pool); // випадковий порядок
            exercisePool.put(group, pool);
        }

        // Трекер індексів щоб брати наступну вправу кожного дня
        Map<MuscleGroup, Integer> indexTracker = new HashMap<>();
        for (MuscleGroup group : allGroups) {
            indexTracker.put(group, 0);
        }

        for (int day = 1; day <= sessionsPerWeek; day++) {
            List<WorkoutExercise> exList = new ArrayList<>();
            int[] setsReps  = getSetsAndReps(IntensityType.FULL_BODY);
            int restSeconds = getRestSeconds(IntensityType.FULL_BODY);

            for (MuscleGroup group : allGroups) {
                List<Exercise> pool = exercisePool.get(group);
                int idx = indexTracker.get(group);

                if (pool.isEmpty()) continue;

                // береємо вправу по індексу, якщо закінчились — по колу
                Exercise ex = pool.get(idx % pool.size());
                indexTracker.put(group, idx + 1);

                exList.add(WorkoutExercise.builder()
                        .exerciseId(ex.getId())
                        .exerciseName(ex.getName())
                        .sets(setsReps[0])
                        .reps(setsReps[0] + "-" + setsReps[1])
                        .restSeconds(restSeconds)
                        .plannedWeight(0.0)
                        .build());
            }

            days.add(WorkoutDay.builder()
                    .weekNumber(week)
                    .dayNumber(day)
                    .focus("FULL BODY")
                    .intensityType(IntensityType.FULL_BODY)
                    .exercises(exList)
                    .build());
        }
        return days;
    }

    // ── Сплітовий тиждень ──
    private List<WorkoutDay> buildSplitWeek(int week,
                                            List<List<MuscleGroup>> split,
                                            List<Exercise> exercises,
                                            UserProfile profile,
                                            IntensityType intensity) {
        List<WorkoutDay> days = new ArrayList<>();

        for (int i = 0; i < split.size(); i++) {
            List<MuscleGroup> groups = split.get(i);
            String focus = groups.stream()
                    .map(MuscleGroup::name)
                    .collect(Collectors.joining(" + "));

            days.add(WorkoutDay.builder()
                    .weekNumber(week)
                    .dayNumber(i + 1)
                    .focus(focus)
                    .intensityType(intensity)
                    .exercises(buildExercises(groups, exercises, profile, intensity, week))
                    .build());
        }
        return days;
    }

    // ── Вправи залежно від інтенсивності ──
    private List<WorkoutExercise> buildExercises(List<MuscleGroup> groups,
                                                 List<Exercise> all,
                                                 UserProfile profile,
                                                 IntensityType intensity,
                                                 int weekNumber) {
        List<WorkoutExercise> result = new ArrayList<>();

        for (MuscleGroup group : groups) {
            List<Exercise> filtered = all.stream()
                    .filter(e -> e.getMuscleGroup() == group)
                    .filter(e -> isLevelSuitable(e.getDifficulty(), profile.getLevel()))
                    // Важкий тиждень — пріоритет базовим
                    .sorted((a, b) -> {
                        if (intensity == IntensityType.HEAVY) {
                            return a.getDifficulty().compareTo(b.getDifficulty()); // ADVANCED перші
                        }
                        return 0;
                    })
                    .collect(Collectors.toCollection(ArrayList::new));

            if (filtered.isEmpty()) continue;

            // Перемішуємо але детерміновано по тижню — щоб різні тижні = різні вправи
            Collections.shuffle(filtered, new Random(weekNumber * 31L + group.ordinal()));

            int count       = EXERCISE_COUNT.getOrDefault(group, 2);
            int[] setsReps  = getSetsAndReps(intensity);
            int restSeconds = getRestSeconds(intensity);

            // Беремо зі зміщенням по тижню щоб вправи ротувались
            int offset = ((weekNumber - 1) * count) % Math.max(filtered.size(), 1);
            for (int i = 0; i < count && i < filtered.size(); i++) {
                Exercise ex = filtered.get((offset + i) % filtered.size());
                result.add(WorkoutExercise.builder()
                        .exerciseId(ex.getId())
                        .exerciseName(ex.getName())
                        .sets(setsReps[0])
                        .reps(setsReps[0] + "-" + setsReps[1])
                        .restSeconds(restSeconds)
                        .plannedWeight(0.0)
                        .build());
            }
        }
        return result;
    }

    private int[] getSetsAndReps(IntensityType intensity) {
        return switch (intensity) {
            case HEAVY     -> new int[]{4, 6};   // 4×4-6
            case MEDIUM    -> new int[]{4, 10};  // 4×8-10
            case SETS      -> new int[]{3, 15};  // 3×12-15 суперсети
            case FULL_BODY -> new int[]{3, 12};  // 3×10-12
        };
    }

    private int getRestSeconds(IntensityType intensity) {
        return switch (intensity) {
            case HEAVY     -> 180;
            case MEDIUM    -> 90;
            case SETS      -> 45;
            case FULL_BODY -> 60;
        };
    }

    private boolean isLevelSuitable(Difficulty difficulty, FitnessLevel level) {
        return switch (level) {
            case BEGINNER, RETURNING -> difficulty == Difficulty.BEGINNER;
            case INTERMEDIATE        -> difficulty != Difficulty.ADVANCED;
            case ADVANCED            -> true;
        };
    }

    private String buildTitle(PlanType planType) {
        return switch (planType) {
            case HYPERTROPHY          -> "Hypertrophy Plan — 8 weeks";
            case STRENGTH             -> "Strength Plan — 10 weeks";
            case STRENGTH_HYPERTROPHY -> "Strength & Mass Plan — 9 weeks";
            case FAT_LOSS             -> "Fat Loss Plan — 8 weeks";
            case ENDURANCE            -> "Endurance Plan — 8 weeks";
        };
    }
}
