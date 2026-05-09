package org.lupoi.workoutapp.application.usecase.admin;/*
    @author Andrii
    @project workout
    @class ManageExerciseUseCase
    @version 1.0.0
    @since 06.05.2026 - 11.28
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.command.ExerciseCommand;
import org.lupoi.workoutapp.domain.entity.workout.Exercise;
import org.lupoi.workoutapp.domain.enums.Difficulty;
import org.lupoi.workoutapp.domain.enums.EquipmentType;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;
import org.lupoi.workoutapp.domain.exception.EntityNotFoundException;
import org.lupoi.workoutapp.domain.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageExerciseUseCase {

    private final ExerciseRepository exerciseRepository;

    public Exercise create(ExerciseCommand cmd) {
        Exercise exercise = Exercise.builder()
                .name(cmd.name())
                .muscleGroup(cmd.muscleGroup() != null ? MuscleGroup.valueOf(cmd.muscleGroup()) : null)
                .difficulty(cmd.difficulty() != null ? Difficulty.valueOf(cmd.difficulty()) : null)
                .equipmentType(cmd.equipmentType() != null ? EquipmentType.valueOf(cmd.equipmentType()) : null)
                .description(cmd.description())
                .videoUrl(cmd.videoUrl())
                .build();
        return exerciseRepository.save(exercise);
    }

    public Exercise update(String id, ExerciseCommand cmd) {
        Exercise existing = exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exercise", id));
        Exercise updated = Exercise.builder()
                .id(existing.getId())
                .name(cmd.name())
                .muscleGroup(cmd.muscleGroup() != null ? MuscleGroup.valueOf(cmd.muscleGroup()) : null)
                .difficulty(cmd.difficulty() != null ? Difficulty.valueOf(cmd.difficulty()) : null)
                .equipmentType(cmd.equipmentType() != null ? EquipmentType.valueOf(cmd.equipmentType()) : null)
                .description(cmd.description())
                .videoUrl(cmd.videoUrl())
                .build();
        return exerciseRepository.save(updated);
    }

    public void delete(String id) {
        exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exercise", id));
        exerciseRepository.deleteById(id);
    }
}

