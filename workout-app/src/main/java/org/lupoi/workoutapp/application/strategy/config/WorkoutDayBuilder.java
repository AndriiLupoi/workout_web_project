package org.lupoi.workoutapp.application.strategy.config;/*
    @author Andrii
    @project workout
    @class WorkoutDayBuilder
    @version 1.0.0
    @since 09.05.2026 - 18.46
*/

import org.lupoi.workoutapp.domain.entity.workout.Exercise;
import org.lupoi.workoutapp.domain.entity.workout.WorkoutDay;
import org.lupoi.workoutapp.domain.entity.workout.WorkoutExercise;
import org.lupoi.workoutapp.domain.enums.IntensityType;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
Builds a WorkoutDay given a list of target muscles and intensity.
Separated from strategy classes so each strategy can reuse it.
*/
public class WorkoutDayBuilder {

    private final ExercisePool pool;

    public WorkoutDayBuilder(ExercisePool pool) {
        this.pool = pool;
    }

    /**
     * Builds a standard split day.
     *
     * @param weekNumber  current week (used for exercise rotation)
     * @param dayNumber   day within the week
     * @param muscles     target muscles for this day
     * @param intensity   intensity type (HEAVY / MEDIUM / SETS)
     * @return a fully populated WorkoutDay
     */
    public WorkoutDay buildSplitDay(int weekNumber,
                                    int dayNumber,
                                    List<MuscleGroup> muscles,
                                    IntensityType intensity) {

        String focus = muscles.stream()
                .map(MuscleGroup::name)
                .collect(Collectors.joining(" + "));

        PlanConfig.SetsReps sr = PlanConfig.BASE_SETS_REPS.get(intensity);
        int rest = PlanConfig.REST_SECONDS.get(intensity);

        List<WorkoutExercise> exercises = new ArrayList<>();
        for (MuscleGroup muscle : muscles) {
            int count = pool.exerciseCountFor(muscle);
            List<Exercise> selected =
                    pool.forMuscleRotated(muscle, weekNumber, count);
            for (var ex : selected) {
                exercises.add(WorkoutExercise.builder()
                        .exerciseId(ex.getId())
                        .exerciseName(ex.getName())
                        .sets(sr.sets())
                        .reps(sr.repsLabel())
                        .restSeconds(rest)
                        .plannedWeight(0.0)
                        .build());
            }
        }

        return WorkoutDay.builder()
                .weekNumber(weekNumber)
                .dayNumber(dayNumber)
                .focus(focus)
                .intensityType(intensity)
                .exercises(exercises)
                .build();
    }

    /**
     * Builds a Full Body day — one exercise per major muscle group.
     * Used for beginners and returning athletes in their intro phase.
     */
    public WorkoutDay buildFullBodyDay(int weekNumber,
                                       int dayNumber,
                                       List<MuscleGroup> muscleGroups) {

        PlanConfig.SetsReps sr = PlanConfig.BASE_SETS_REPS.get(IntensityType.FULL_BODY);
        int rest = PlanConfig.REST_SECONDS.get(IntensityType.FULL_BODY);

        List<WorkoutExercise> exercises = new ArrayList<>();
        for (MuscleGroup muscle : muscleGroups) {
            List<Exercise> selected =
                    pool.forFullBodyDay(muscle, weekNumber, dayNumber);
            for (var ex : selected) {
                exercises.add(WorkoutExercise.builder()
                        .exerciseId(ex.getId())
                        .exerciseName(ex.getName())
                        .sets(sr.sets())
                        .reps(sr.repsLabel())
                        .restSeconds(rest)
                        .plannedWeight(0.0)
                        .build());
            }
        }

        return WorkoutDay.builder()
                .weekNumber(weekNumber)
                .dayNumber(dayNumber)
                .focus("FULL BODY")
                .intensityType(IntensityType.FULL_BODY)
                .exercises(exercises)
                .build();
    }
}

