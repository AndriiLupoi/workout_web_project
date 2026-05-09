package org.lupoi.workoutapp.application.strategy.config;/*
    @author Andrii
    @project workout
    @class SplitProvider
    @version 1.0.0
    @since 09.05.2026 - 18.46
*/

import org.lupoi.workoutapp.domain.enums.FitnessLevel;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;

import java.util.List;

public final class SplitProvider {

    private SplitProvider() {}

    // Push / Pull / Legs — 3 sessions
    public static final List<List<MuscleGroup>> PPL_3 = List.of(
            List.of(MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),   // Push
            List.of(MuscleGroup.BACK,  MuscleGroup.BICEPS,    MuscleGroup.FOREARMS),  // Pull
            List.of(MuscleGroup.LEGS,  MuscleGroup.CALVES)                            // Legs
    );

    // Upper / Lower — 4 sessions
    public static final List<List<MuscleGroup>> UPPER_LOWER_4 = List.of(
            List.of(MuscleGroup.CHEST, MuscleGroup.BACK,      MuscleGroup.SHOULDERS), // Upper A
            List.of(MuscleGroup.LEGS,  MuscleGroup.CALVES,    MuscleGroup.ABS),       // Lower A
            List.of(MuscleGroup.CHEST, MuscleGroup.BACK,      MuscleGroup.BICEPS, MuscleGroup.TRICEPS), // Upper B
            List.of(MuscleGroup.LEGS,  MuscleGroup.CALVES,    MuscleGroup.TRAPS)      // Lower B
    );

    // PPL + Shoulders specialization — 5 sessions
    public static final List<List<MuscleGroup>> PPL_5 = List.of(
            List.of(MuscleGroup.CHEST, MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS),   // Push
            List.of(MuscleGroup.BACK,  MuscleGroup.BICEPS,    MuscleGroup.FOREARMS),  // Pull
            List.of(MuscleGroup.LEGS,  MuscleGroup.CALVES),                           // Legs
            List.of(MuscleGroup.SHOULDERS, MuscleGroup.TRAPS, MuscleGroup.ABS),       // Shoulders / Core
            List.of(MuscleGroup.CHEST, MuscleGroup.BACK,      MuscleGroup.BICEPS, MuscleGroup.TRICEPS) // Full Upper
    );

    // Full-Body muscles for beginner/returning intro phase
    public static final List<MuscleGroup> FULL_BODY_MUSCLES = List.of(
            MuscleGroup.CHEST,
            MuscleGroup.BACK,
            MuscleGroup.LEGS,
            MuscleGroup.SHOULDERS
    );

    /**
     * Returns the best split for the given days/week.
     * Caps at 5 days max (recovery is king).
     */
    public static List<List<MuscleGroup>> forDaysPerWeek(int daysPerWeek, FitnessLevel level) {
        int capped = Math.min(daysPerWeek, 5);
        return switch (capped) {
            case 1, 2, 3 -> PPL_3;
            case 4        -> UPPER_LOWER_4;
            default       -> level == FitnessLevel.ADVANCED ? PPL_5 : UPPER_LOWER_4;
        };
    }

    /**
     * Number of split sessions to use per week.
     * Can be less than daysPerWeek if we're still in Full Body phase.
     */
    public static int splitSessionsPerWeek(int daysPerWeek) {
        int capped = Math.min(daysPerWeek, 5);
        return switch (capped) {
            case 1, 2, 3 -> 3;
            case 4        -> 4;
            default       -> 5;
        };
    }
}

