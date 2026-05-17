package org.lupoi.workoutapp.application.strategy;/*
    @author Andrii
    @project workout
    @class BasePlanStrategy
    @version 1.0.0
    @since 09.05.2026 - 18.42
*/

import org.lupoi.workoutapp.application.strategy.config.ExercisePool;
import org.lupoi.workoutapp.application.strategy.config.PlanConfig;
import org.lupoi.workoutapp.application.strategy.config.SplitProvider;
import org.lupoi.workoutapp.application.strategy.config.WorkoutDayBuilder;
import org.lupoi.workoutapp.domain.entity.UserProfile;
import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.entity.WorkoutDay;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;
import org.lupoi.workoutapp.domain.enums.IntensityType;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;
import org.lupoi.workoutapp.domain.enums.PlanStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class BasePlanStrategy implements PlanGenerationStrategy {

    @Override
    public final WorkoutPlan generate(UserProfile profile, List<Exercise> exercises) {
        ExercisePool pool       = new ExercisePool(exercises, profile);
        WorkoutDayBuilder builder = new WorkoutDayBuilder(pool);

        int totalWeeks      = totalWeeks();
        int fullBodyWeeks   = PlanConfig.FULL_BODY_INTRO_WEEKS.getOrDefault(profile.getLevel(), 0);
        int splitWeeks      = totalWeeks - fullBodyWeeks;

        List<WorkoutDay> allDays = new ArrayList<>();

        // ── Phase 1: Full Body intro ───────────────────────────────────────────
        for (int week = 1; week <= fullBodyWeeks; week++) {
            int sessions = fullBodySessionsPerWeek(profile.getWorkoutsPerWeek());
            for (int day = 1; day <= sessions; day++) {
                allDays.add(builder.buildFullBodyDay(week, day, SplitProvider.FULL_BODY_MUSCLES));
            }
        }

        // ── Phase 2: Split training with periodized intensity ─────────────────
        List<List<MuscleGroup>> split = SplitProvider.forDaysPerWeek(
                profile.getWorkoutsPerWeek(), profile.getLevel()
        );
        int sessionsPerWeek = SplitProvider.splitSessionsPerWeek(profile.getWorkoutsPerWeek());
        List<IntensityType> cycle = intensityCycle();

        for (int week = 1; week <= splitWeeks; week++) {
            int absoluteWeek = fullBodyWeeks + week;
            IntensityType intensity = cycle.get((week - 1) % cycle.size());

            for (int dayIdx = 0; dayIdx < sessionsPerWeek; dayIdx++) {
                List<MuscleGroup> muscles = split.get(dayIdx % split.size());
                allDays.add(builder.buildSplitDay(absoluteWeek, dayIdx + 1, muscles, intensity));
            }
        }

        return WorkoutPlan.builder()
                .userId(profile.getUserId())
                .title(planTitle())
                .goal(profile.getGoal())
                .planType(profile.getPlanType())
                .durationWeeks(totalWeeks)
                .status(PlanStatus.ACTIVE)
                .days(allDays)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // ─── Template methods ──────────────────────────────────────────────────────

    protected abstract int totalWeeks();
    protected abstract String planTitle();
    protected abstract List<IntensityType> intensityCycle();

    // ─── Default: Full Body sessions capped at 3 for beginners ───────────────
    protected int fullBodySessionsPerWeek(int requested) {
        return Math.min(requested, 3);
    }
}

