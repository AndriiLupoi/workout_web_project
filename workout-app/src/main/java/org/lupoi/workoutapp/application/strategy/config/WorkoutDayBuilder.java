package org.lupoi.workoutapp.application.strategy.config;

import org.lupoi.workoutapp.domain.entity.workout.Exercise;
import org.lupoi.workoutapp.domain.entity.workout.WorkoutDay;
import org.lupoi.workoutapp.domain.entity.workout.WorkoutExercise;
import org.lupoi.workoutapp.domain.enums.IntensityType;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WorkoutDayBuilder {

    private final ExercisePool pool;

    public WorkoutDayBuilder(ExercisePool pool) {
        this.pool = pool;
    }

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
            List<Exercise> selected = pool.forMuscleRotated(muscle, weekNumber, count);
            for (var ex : selected) {
                exercises.add(WorkoutExercise.builder()
                        .exerciseId(ex.getId())
                        .exerciseName(ex.getName())
                        .sets(sr.sets())
                        .reps(sr.repsLabel())
                        .restSeconds(rest)
                        .plannedWeight(0.0)
                        // ── розширені поля ──
                        .muscleGroup(ex.getMuscleGroup() != null ? ex.getMuscleGroup().name() : null)
                        .equipmentType(ex.getEquipmentType() != null ? ex.getEquipmentType().name() : null)
                        .description(ex.getDescription())
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

    public WorkoutDay buildFullBodyDay(int weekNumber,
                                       int dayNumber,
                                       List<MuscleGroup> muscleGroups) {

        PlanConfig.SetsReps sr = PlanConfig.BASE_SETS_REPS.get(IntensityType.FULL_BODY);
        int rest = PlanConfig.REST_SECONDS.get(IntensityType.FULL_BODY);

        List<WorkoutExercise> exercises = new ArrayList<>();
        for (MuscleGroup muscle : muscleGroups) {
            List<Exercise> selected = pool.forFullBodyDay(muscle, weekNumber, dayNumber);
            for (var ex : selected) {
                exercises.add(WorkoutExercise.builder()
                        .exerciseId(ex.getId())
                        .exerciseName(ex.getName())
                        .sets(sr.sets())
                        .reps(sr.repsLabel())
                        .restSeconds(rest)
                        .plannedWeight(0.0)
                        // ── розширені поля ──
                        .muscleGroup(ex.getMuscleGroup() != null ? ex.getMuscleGroup().name() : null)
                        .equipmentType(ex.getEquipmentType() != null ? ex.getEquipmentType().name() : null)
                        .description(ex.getDescription())
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