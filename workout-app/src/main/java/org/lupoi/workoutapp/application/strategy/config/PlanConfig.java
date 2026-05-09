package org.lupoi.workoutapp.application.strategy.config;/*
    @author Andrii
    @project workout
    @class PlanConfig
    @version 1.0.0
    @since 09.05.2026 - 18.41
*/

import org.lupoi.workoutapp.domain.enums.FitnessLevel;
import org.lupoi.workoutapp.domain.enums.IntensityType;

import java.util.List;
import java.util.Map;

/**
 * Central knowledge base for plan generation.
 * Encodes "how a trainer thinks" about volume, intensity, and progression.
 */
public final class PlanConfig {

    private PlanConfig() {}

    // ─── Duration by plan type (weeks) ─────────────────────────────────────────
    public static final int WEEKS_HYPERTROPHY          = 8;
    public static final int WEEKS_STRENGTH             = 10;
    public static final int WEEKS_STRENGTH_HYPERTROPHY = 9;
    public static final int WEEKS_FAT_LOSS             = 8;
    public static final int WEEKS_ENDURANCE            = 8;

    // ─── Full Body intro weeks by level ────────────────────────────────────────
    public static final Map<FitnessLevel, Integer> FULL_BODY_INTRO_WEEKS = Map.of(
            FitnessLevel.BEGINNER,     3,  // beginners need more time learning movement patterns
            FitnessLevel.RETURNING,    2,  // muscle memory is there, but needs re-activation
            FitnessLevel.INTERMEDIATE, 0,
            FitnessLevel.ADVANCED,     0
    );

    // ─── Weekly sets per muscle group by level ─────────────────────────────────
    // Based on research: beginners 8-10, intermediate 10-16, advanced 14-22
    public static final Map<FitnessLevel, Integer> SETS_PER_MUSCLE_LARGE = Map.of(
            FitnessLevel.BEGINNER,     9,
            FitnessLevel.RETURNING,    10,
            FitnessLevel.INTERMEDIATE, 13,
            FitnessLevel.ADVANCED,     18
    );

    public static final Map<FitnessLevel, Integer> SETS_PER_MUSCLE_SMALL = Map.of(
            FitnessLevel.BEGINNER,     6,
            FitnessLevel.RETURNING,    7,
            FitnessLevel.INTERMEDIATE, 9,
            FitnessLevel.ADVANCED,     12
    );

    // ─── Periodization cycle: intensity phases per goal ────────────────────────
    // HYPERTROPHY / STRENGTH_HYPERTROPHY: focus on volume with occasional heavy
    public static final List<IntensityType> CYCLE_HYPERTROPHY = List.of(
            IntensityType.MEDIUM,   // week 1: volume focus
            IntensityType.MEDIUM,   // week 2: volume focus
            IntensityType.HEAVY,    // week 3: intensity spike
            IntensityType.SETS,     // week 4: accumulation / supersets
            IntensityType.MEDIUM,
            IntensityType.HEAVY,
            IntensityType.SETS,
            IntensityType.MEDIUM
    );

    // STRENGTH: heavy compounds, low rep, longer rest
    public static final List<IntensityType> CYCLE_STRENGTH = List.of(
            IntensityType.HEAVY,
            IntensityType.HEAVY,
            IntensityType.MEDIUM,   // deload-ish
            IntensityType.HEAVY,
            IntensityType.HEAVY,
            IntensityType.MEDIUM,
            IntensityType.HEAVY,
            IntensityType.HEAVY,
            IntensityType.MEDIUM,
            IntensityType.HEAVY
    );

    // FAT_LOSS: high rep, short rest, circuits
    public static final List<IntensityType> CYCLE_FAT_LOSS = List.of(
            IntensityType.SETS,
            IntensityType.SETS,
            IntensityType.MEDIUM,
            IntensityType.SETS,
            IntensityType.SETS,
            IntensityType.MEDIUM,
            IntensityType.SETS,
            IntensityType.SETS
    );

    // ENDURANCE: high rep, circuit, progressive overload via reps not weight
    public static final List<IntensityType> CYCLE_ENDURANCE = List.of(
            IntensityType.SETS,
            IntensityType.SETS,
            IntensityType.SETS,
            IntensityType.MEDIUM,
            IntensityType.SETS,
            IntensityType.SETS,
            IntensityType.SETS,
            IntensityType.MEDIUM
    );

    // STRENGTH_HYPERTROPHY: alternates heavy and volume
    public static final List<IntensityType> CYCLE_STRENGTH_HYPERTROPHY = List.of(
            IntensityType.HEAVY,
            IntensityType.MEDIUM,
            IntensityType.HEAVY,
            IntensityType.SETS,
            IntensityType.HEAVY,
            IntensityType.MEDIUM,
            IntensityType.HEAVY,
            IntensityType.SETS,
            IntensityType.HEAVY
    );

    // ─── Sets x Reps targets per intensity type ────────────────────────────────
    public record SetsReps(int sets, int repsLow, int repsHigh) {
        public String repsLabel() { return repsLow + "-" + repsHigh; }
    }

    // These represent the TARGET for that intensity. Goal further adjusts.
    public static final Map<IntensityType, SetsReps> BASE_SETS_REPS = Map.of(
            IntensityType.HEAVY,     new SetsReps(5, 3, 5),
            IntensityType.MEDIUM,    new SetsReps(4, 8, 12),
            IntensityType.SETS,      new SetsReps(3, 12, 20),
            IntensityType.FULL_BODY, new SetsReps(3, 10, 15)
    );

    // ─── Rest seconds per intensity type ──────────────────────────────────────
    public static final Map<IntensityType, Integer> REST_SECONDS = Map.of(
            IntensityType.HEAVY,     180,
            IntensityType.MEDIUM,    90,
            IntensityType.SETS,      45,
            IntensityType.FULL_BODY, 60
    );

    // ─── Number of exercises per muscle group per session ─────────────────────
    // This is EXERCISES per session, not weekly sets.
    // Large muscles get more exercises; small get fewer.
    public static final Map<String, Integer> EXERCISES_PER_MUSCLE = Map.of(
            "LARGE",  3,  // chest, back, legs
            "MEDIUM", 2,  // shoulders, biceps, triceps
            "SMALL",  1   // forearms, calves, traps, abs
    );
}
