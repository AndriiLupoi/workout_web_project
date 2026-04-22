package org.lupoi.workoutapp.domain.repository;/*
    @author Andrii
    @project workout
    @class ExerciseRepository
    @version 1.0.0
    @since 24.03.2026 - 14.49
*/

import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.enums.Difficulty;
import org.lupoi.workoutapp.domain.enums.EquipmentType;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;

import java.util.List;

public interface ExerciseRepository {
    List<Exercise> findAll();
    List<Exercise> findByDifficulty(Difficulty difficulty);
    List<Exercise> findByMuscleGroup(MuscleGroup muscleGroup);
    Exercise save(Exercise exercise);
    long count();
    List<Exercise> findByFilters(
            MuscleGroup muscleGroup,
            Difficulty difficulty,
            EquipmentType equipment,
            String sortBy  // "name", "difficulty", "muscleGroup"
    );
}