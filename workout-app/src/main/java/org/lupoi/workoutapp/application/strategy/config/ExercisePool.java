package org.lupoi.workoutapp.application.strategy.config;

import org.lupoi.workoutapp.domain.entity.user.UserProfile;
import org.lupoi.workoutapp.domain.entity.workout.Exercise;
import org.lupoi.workoutapp.domain.enums.Difficulty;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Responsible for selecting appropriate exercises given a profile and available pool.
 * Trainers think about exercises in terms of:
 *   1. Can the person DO this movement safely? (level filter)
 *   2. Do they have the equipment? (equipment filter)
 *   3. Does it target the right muscle/pattern? (muscle filter)
 *   4. Is there variety week over week? (rotation)
 */
public class ExercisePool {

    private final List<Exercise> all;
    private final UserProfile profile;

    // Maps muscle group name → "size" for exercise count decisions
    private static final Map<String, String> MUSCLE_SIZE = Map.ofEntries(
            Map.entry("CHEST",     "LARGE"),
            Map.entry("BACK",      "LARGE"),
            Map.entry("LEGS",      "LARGE"),
            Map.entry("SHOULDERS", "MEDIUM"),
            Map.entry("BICEPS",    "MEDIUM"),
            Map.entry("TRICEPS",   "MEDIUM"),
            Map.entry("FOREARMS",  "SMALL"),
            Map.entry("CALVES",    "SMALL"),
            Map.entry("TRAPS",     "SMALL"),
            Map.entry("ABS",       "SMALL")
    );

    public ExercisePool(List<Exercise> all, UserProfile profile) {
        this.all = all;
        this.profile = profile;
    }

    /**
     * Returns filtered exercises for a given muscle group.
     * Applies level and equipment filters.
     */
    public List<Exercise> forMuscle(MuscleGroup muscle) {
        Set<String> equipment = availableEquipment();

        return all.stream()
                .filter(e -> e.getMuscleGroup() == muscle)
                .filter(e -> isLevelSuitable(e.getDifficulty()))
                .filter(e -> e.getEquipmentType() == null
                        || equipment.isEmpty()
                        || equipment.contains(e.getEquipmentType().name()))
                .collect(Collectors.toList());
    }

    /**
     * Returns exercises for a muscle rotated by week number.
     * Ensures different exercises appear in different weeks — critical for adaptation.
     */
    public List<Exercise> forMuscleRotated(MuscleGroup muscle, int weekNumber, int count) {
        List<Exercise> pool = forMuscle(muscle);
        if (pool.isEmpty()) return List.of();

        // Deterministic shuffle per week+muscle — different weeks get different exercises
        List<Exercise> shuffled = new ArrayList<>(pool);
        Collections.shuffle(shuffled, new Random((long) weekNumber * 31 + muscle.ordinal()));

        // Rotate offset so early-week and late-week exercises differ
        int offset = ((weekNumber - 1) * count) % Math.max(shuffled.size(), 1);
        List<Exercise> result = new ArrayList<>();
        for (int i = 0; i < count && i < shuffled.size(); i++) {
            result.add(shuffled.get((offset + i) % shuffled.size()));
        }
        return result;
    }

    /**
     * Returns exercises for Full Body days — one per muscle group, beginner-friendly.
     * Rotated by day to ensure variety within the same week.
     */
    public List<Exercise> forFullBodyDay(MuscleGroup muscle, int weekNumber, int dayNumber) {
        List<Exercise> pool = all.stream()
                .filter(e -> e.getMuscleGroup() == muscle)
                .filter(e -> e.getDifficulty() == Difficulty.BEGINNER)
                .filter(e -> {
                    Set<String> equipment = availableEquipment();
                    return equipment.isEmpty()
                            || e.getEquipmentType() == null
                            || equipment.contains(e.getEquipmentType().name());
                })
                .collect(Collectors.toCollection(ArrayList::new));

        if (pool.isEmpty()) return List.of();

        Collections.shuffle(pool, new Random((long) weekNumber * 37 + muscle.ordinal()));
        int idx = (dayNumber - 1) % pool.size();
        return List.of(pool.get(idx));
    }

    public int exerciseCountFor(MuscleGroup muscle) {
        String size = MUSCLE_SIZE.getOrDefault(muscle.name(), "MEDIUM");
        return switch (size) {
            case "LARGE"  -> PlanConfig.EXERCISES_PER_MUSCLE.get("LARGE");
            case "SMALL"  -> PlanConfig.EXERCISES_PER_MUSCLE.get("SMALL");
            default       -> PlanConfig.EXERCISES_PER_MUSCLE.get("MEDIUM");
        };
    }

    // ─── Private helpers ───────────────────────────────────────────────────────

    private boolean isLevelSuitable(Difficulty difficulty) {
        if (difficulty == null) return true;
        return switch (profile.getLevel()) {
            case BEGINNER, RETURNING -> difficulty == Difficulty.BEGINNER;
            case INTERMEDIATE        -> difficulty != Difficulty.ADVANCED;
            case ADVANCED            -> true;
        };
    }

    private Set<String> availableEquipment() {
        if (profile.getAvailableEquipment() == null) return Set.of();
        return new HashSet<>(profile.getAvailableEquipment());
    }
}