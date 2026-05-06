package org.lupoi.workoutapp.application.usecase.admin;/*
    @author Andrii
    @project workout
    @class ManageExerciseUseCase
    @version 1.0.0
    @since 06.05.2026 - 11.28
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.enums.Difficulty;
import org.lupoi.workoutapp.domain.enums.EquipmentType;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;
import org.lupoi.workoutapp.domain.exception.EntityNotFoundException;
import org.lupoi.workoutapp.domain.repository.ExerciseRepository;
import org.lupoi.workoutapp.presentation.dto.request.ExerciseRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManageExerciseUseCase {

    private final ExerciseRepository exerciseRepository;

    public Exercise create(ExerciseRequest req) {
        Exercise exercise = Exercise.builder()
                .name(req.name())
                .muscleGroup(req.muscleGroup() != null ? MuscleGroup.valueOf(req.muscleGroup()) : null)
                .difficulty(req.difficulty() != null ? Difficulty.valueOf(req.difficulty()) : null)
                .equipmentType(req.equipmentType() != null ? EquipmentType.valueOf(req.equipmentType()) : null)
                .description(req.description())
                .videoUrl(req.videoUrl())
                .build();
        return exerciseRepository.save(exercise);
    }

    public Exercise update(String id, ExerciseRequest req) {
        Exercise existing = exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exercise", id));
        Exercise updated = Exercise.builder()
                .id(existing.getId())
                .name(req.name())
                .muscleGroup(req.muscleGroup() != null ? MuscleGroup.valueOf(req.muscleGroup()) : null)
                .difficulty(req.difficulty() != null ? Difficulty.valueOf(req.difficulty()) : null)
                .equipmentType(req.equipmentType() != null ? EquipmentType.valueOf(req.equipmentType()) : null)
                .description(req.description())
                .videoUrl(req.videoUrl())
                .build();
        return exerciseRepository.save(updated);
    }

    public void delete(String id) {
        exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exercise", id));
        exerciseRepository.deleteById(id);
    }
}
