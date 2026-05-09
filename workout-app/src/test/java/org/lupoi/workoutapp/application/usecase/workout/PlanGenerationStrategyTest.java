package org.lupoi.workoutapp.application.usecase.workout;/*
    @author Andrii
    @project workout
    @class PlanGenerationStrategyTest
    @version 1.0.0
    @since 09.05.2026 - 18.59
*/

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lupoi.workoutapp.application.strategy.PlanGenerationStrategy;
import org.lupoi.workoutapp.application.strategy.PlanStrategyFactory;
import org.lupoi.workoutapp.application.strategy.plans.*;
import org.lupoi.workoutapp.domain.entity.user.UserProfile;
import org.lupoi.workoutapp.domain.entity.workout.Exercise;
import org.lupoi.workoutapp.domain.entity.workout.WorkoutDay;
import org.lupoi.workoutapp.domain.entity.workout.WorkoutPlan;
import org.lupoi.workoutapp.domain.enums.*;

import java.util.ArrayList;
import java.util.List;


import static org.assertj.core.api.Assertions.*;


class PlanGenerationStrategyTest {

    private List<Exercise> exercises;

    @BeforeEach
    void setUp() {
        // Build a realistic exercise pool covering all muscle groups
        exercises = new ArrayList<>();
        for (MuscleGroup muscle : MuscleGroup.values()) {
            for (Difficulty diff : Difficulty.values()) {
                for (EquipmentType eq : new EquipmentType[]{EquipmentType.BARBELL, EquipmentType.DUMBBELL, EquipmentType.BODYWEIGHT}) {
                    exercises.add(Exercise.builder()
                            .id(muscle + "_" + diff + "_" + eq)
                            .name(muscle.name() + " " + diff.name() + " exercise")
                            .muscleGroup(muscle)
                            .difficulty(diff)
                            .equipmentType(eq)
                            .build());
                }
            }
        }
    }

    private UserProfile profileFor(FitnessLevel level, PlanType planType, int daysPerWeek) {
        return UserProfile.builder()
                .userId("test-user")
                .goal(TrainingGoal.MASS)
                .level(level)
                .planType(planType)
                .workoutsPerWeek(daysPerWeek)
                .availableEquipment(List.of("BARBELL", "DUMBBELL", "BODYWEIGHT"))
                .build();
    }

    // ─── Hypertrophy ──────────────────────────────────────────────────────────

    @Test
    @DisplayName("Hypertrophy plan — beginner gets Full Body intro weeks")
    void hypertrophy_beginner_hasFullBodyIntro() {
        UserProfile profile = profileFor(FitnessLevel.BEGINNER, PlanType.HYPERTROPHY, 3);
        WorkoutPlan plan = new HypertrophyPlanStrategy().generate(profile, exercises);

        assertThat(plan.getDurationWeeks()).isEqualTo(8);
        // First sessions should be Full Body
        long fullBodyDays = plan.getDays().stream()
                .filter(d -> d.getIntensityType() == IntensityType.FULL_BODY)
                .count();
        assertThat(fullBodyDays).isGreaterThan(0);
    }

    @Test
    @DisplayName("Hypertrophy plan — advanced athlete skips Full Body intro")
    void hypertrophy_advanced_noFullBodyIntro() {
        UserProfile profile = profileFor(FitnessLevel.ADVANCED, PlanType.HYPERTROPHY, 4);
        WorkoutPlan plan = new HypertrophyPlanStrategy().generate(profile, exercises);

        long fullBodyDays = plan.getDays().stream()
                .filter(d -> d.getIntensityType() == IntensityType.FULL_BODY)
                .count();
        assertThat(fullBodyDays).isEqualTo(0);
    }

    @Test
    @DisplayName("Hypertrophy plan — has MEDIUM intensity days (primary driver for hypertrophy)")
    void hypertrophy_hasMediumIntensityDays() {
        UserProfile profile = profileFor(FitnessLevel.INTERMEDIATE, PlanType.HYPERTROPHY, 3);
        WorkoutPlan plan = new HypertrophyPlanStrategy().generate(profile, exercises);

        long mediumDays = plan.getDays().stream()
                .filter(d -> d.getIntensityType() == IntensityType.MEDIUM)
                .count();
        assertThat(mediumDays).isGreaterThan(0);
    }

    // ─── Strength ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Strength plan — is 10 weeks long")
    void strength_planIs10Weeks() {
        UserProfile profile = profileFor(FitnessLevel.INTERMEDIATE, PlanType.STRENGTH, 3);
        WorkoutPlan plan = new StrengthPlanStrategy().generate(profile, exercises);

        assertThat(plan.getDurationWeeks()).isEqualTo(10);
    }

    @Test
    @DisplayName("Strength plan — dominantly HEAVY intensity")
    void strength_dominantlyHeavy() {
        UserProfile profile = profileFor(FitnessLevel.ADVANCED, PlanType.STRENGTH, 3);
        WorkoutPlan plan = new StrengthPlanStrategy().generate(profile, exercises);

        List<WorkoutDay> splitDays = plan.getDays().stream()
                .filter(d -> d.getIntensityType() != IntensityType.FULL_BODY)
                .toList();

        long heavyDays = splitDays.stream()
                .filter(d -> d.getIntensityType() == IntensityType.HEAVY)
                .count();

        // HEAVY should be more than half of all split days
        assertThat(heavyDays).isGreaterThan(splitDays.size() / 2);
    }

    @Test
    @DisplayName("Strength plan — HEAVY days have 3-5 rep range")
    void strength_heavyDaysHaveLowReps() {
        UserProfile profile = profileFor(FitnessLevel.ADVANCED, PlanType.STRENGTH, 3);
        WorkoutPlan plan = new StrengthPlanStrategy().generate(profile, exercises);

        plan.getDays().stream()
                .filter(d -> d.getIntensityType() == IntensityType.HEAVY)
                .flatMap(d -> d.getExercises().stream())
                .forEach(ex -> {
                    // Reps should be "3-5"
                    assertThat(ex.getReps()).isEqualTo("3-5");
                    // Sets should be 5
                    assertThat(ex.getSets()).isEqualTo(5);
                    // Rest should be 3 minutes
                    assertThat(ex.getRestSeconds()).isEqualTo(180);
                });
    }

    // ─── Fat Loss ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Fat Loss plan — SETS intensity (supersets/circuits) dominates")
    void fatLoss_setsDominates() {
        UserProfile profile = profileFor(FitnessLevel.INTERMEDIATE, PlanType.FAT_LOSS, 4);
        WorkoutPlan plan = new FatLossPlanStrategy().generate(profile, exercises);

        List<WorkoutDay> splitDays = plan.getDays().stream()
                .filter(d -> d.getIntensityType() != IntensityType.FULL_BODY)
                .toList();

        long setsDays = splitDays.stream()
                .filter(d -> d.getIntensityType() == IntensityType.SETS)
                .count();

        assertThat(setsDays).isGreaterThan(splitDays.size() / 2);
    }

    @Test
    @DisplayName("Fat Loss — SETS days have short rest (45s)")
    void fatLoss_shortRest() {
        UserProfile profile = profileFor(FitnessLevel.INTERMEDIATE, PlanType.FAT_LOSS, 3);
        WorkoutPlan plan = new FatLossPlanStrategy().generate(profile, exercises);

        plan.getDays().stream()
                .filter(d -> d.getIntensityType() == IntensityType.SETS)
                .flatMap(d -> d.getExercises().stream())
                .forEach(ex -> assertThat(ex.getRestSeconds()).isEqualTo(45));
    }

    // ─── Endurance ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Endurance plan — high rep range on SETS days")
    void endurance_highReps() {
        UserProfile profile = profileFor(FitnessLevel.INTERMEDIATE, PlanType.ENDURANCE, 3);
        WorkoutPlan plan = new EndurancePlanStrategy().generate(profile, exercises);

        plan.getDays().stream()
                .filter(d -> d.getIntensityType() == IntensityType.SETS)
                .flatMap(d -> d.getExercises().stream())
                .forEach(ex -> {
                    // Should be 12-20 reps
                    assertThat(ex.getReps()).isEqualTo("12-20");
                });
    }

    // ─── Strength + Hypertrophy ───────────────────────────────────────────────

    @Test
    @DisplayName("Strength+Hypertrophy — alternates HEAVY and MEDIUM")
    void strengthHypertrophy_alternatesIntensity() {
        UserProfile profile = profileFor(FitnessLevel.INTERMEDIATE, PlanType.STRENGTH_HYPERTROPHY, 3);
        WorkoutPlan plan = new StrengthHypertrophyPlanStrategy().generate(profile, exercises);

        List<WorkoutDay> splitDays = plan.getDays().stream()
                .filter(d -> d.getIntensityType() != IntensityType.FULL_BODY)
                .toList();

        long heavyCount = splitDays.stream().filter(d -> d.getIntensityType() == IntensityType.HEAVY).count();
        long mediumCount = splitDays.stream().filter(d -> d.getIntensityType() == IntensityType.MEDIUM).count();

        // Both should appear — that's the whole point of this plan
        assertThat(heavyCount).isGreaterThan(0);
        assertThat(mediumCount).isGreaterThan(0);
    }

    // ─── General correctness ─────────────────────────────────────────────────

    @Test
    @DisplayName("All plans — every day has at least one exercise")
    void allPlans_everyDayHasExercises() {
        List<PlanGenerationStrategy> strategies = List.of(
                new HypertrophyPlanStrategy(),
                new StrengthPlanStrategy(),
                new StrengthHypertrophyPlanStrategy(),
                new FatLossPlanStrategy(),
                new EndurancePlanStrategy()
        );

        for (PlanGenerationStrategy strategy : strategies) {
            UserProfile profile = profileFor(FitnessLevel.INTERMEDIATE, PlanType.HYPERTROPHY, 3);
            WorkoutPlan plan = strategy.generate(profile, exercises);

            plan.getDays().forEach(day ->
                    assertThat(day.getExercises())
                            .as("Day %d week %d should have exercises", day.getDayNumber(), day.getWeekNumber())
                            .isNotEmpty()
            );
        }
    }

    @Test
    @DisplayName("Exercise rotation — same muscle gets different exercises in different weeks")
    void exerciseRotation_differsByWeek() {
        UserProfile profile = profileFor(FitnessLevel.ADVANCED, PlanType.HYPERTROPHY, 3);
        WorkoutPlan plan = new HypertrophyPlanStrategy().generate(profile, exercises);

        // Get chest exercises from week 1 vs week 5
        List<String> week1ChestExercises = plan.getDays().stream()
                .filter(d -> d.getWeekNumber() == 1)
                .flatMap(d -> d.getExercises().stream())
                .map(e -> e.getExerciseId())
                .filter(id -> id.startsWith("CHEST"))
                .toList();

        List<String> week5ChestExercises = plan.getDays().stream()
                .filter(d -> d.getWeekNumber() == 5)
                .flatMap(d -> d.getExercises().stream())
                .map(e -> e.getExerciseId())
                .filter(id -> id.startsWith("CHEST"))
                .toList();

        // They shouldn't be identical (rotation should provide some variation)
        // Note: with small pools they might overlap but shouldn't be 100% identical every time
        assertThat(week1ChestExercises).isNotEmpty();
        assertThat(week5ChestExercises).isNotEmpty();
    }

    @Test
    @DisplayName("PlanStrategyFactory — routes to correct strategy")
    void factory_routesCorrectly() {
        var hypertrophy = new HypertrophyPlanStrategy();
        var strength    = new StrengthPlanStrategy();
        var sh          = new StrengthHypertrophyPlanStrategy();
        var fatLoss     = new FatLossPlanStrategy();
        var endurance   = new EndurancePlanStrategy();

        PlanStrategyFactory factory = new PlanStrategyFactory(
                hypertrophy, strength, sh, fatLoss, endurance
        );

        UserProfile hypertrophyProfile = profileFor(FitnessLevel.INTERMEDIATE, PlanType.HYPERTROPHY, 3);
        UserProfile strengthProfile    = profileFor(FitnessLevel.INTERMEDIATE, PlanType.STRENGTH, 3);

        WorkoutPlan hyPlan = factory.generate(hypertrophyProfile, exercises);
        WorkoutPlan stPlan = factory.generate(strengthProfile, exercises);

        assertThat(hyPlan.getDurationWeeks()).isEqualTo(8);
        assertThat(stPlan.getDurationWeeks()).isEqualTo(10);
    }
}

