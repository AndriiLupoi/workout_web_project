package org.lupoi.workoutapp.application.usecase.admin;/*
    @author Andrii
    @project workout
    @class ManageExerciseUseCase
    @version 1.0.0
    @since 06.05.2026 - 11.28
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.command.ExerciseCommand;
import org.lupoi.workoutapp.application.service.AuditService;
import org.lupoi.workoutapp.domain.entity.workout.Exercise;
import org.lupoi.workoutapp.domain.enums.AuditAction;
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
    private final AuditService auditService;

    public Exercise create(ExerciseCommand cmd, String actorId, String actorEmail, String actorRole) {
        Exercise exercise = Exercise.builder()
                .name(cmd.name())
                .muscleGroup(cmd.muscleGroup() != null ? MuscleGroup.valueOf(cmd.muscleGroup()) : null)
                .difficulty(cmd.difficulty() != null ? Difficulty.valueOf(cmd.difficulty()) : null)
                .equipmentType(cmd.equipmentType() != null ? EquipmentType.valueOf(cmd.equipmentType()) : null)
                .description(cmd.description())
                .videoUrl(cmd.videoUrl())
                .build();

        Exercise saved = exerciseRepository.save(exercise);

        auditService.log(
                actorId,
                actorEmail,
                actorRole,
                AuditAction.EXERCISE_CREATED,
                saved.getId(),
                "Exercise",
                "Створено вправу: " + cmd.name()
        );

        return saved;
    }

    public Exercise update(String id, ExerciseCommand cmd, String actorId, String actorEmail, String actorRole) {
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

        Exercise saved = exerciseRepository.save(updated);

        auditService.log(
                actorId,
                actorEmail,
                actorRole,
                AuditAction.EXERCISE_UPDATED,
                id,
                "Exercise",
                "Оновлено вправу: " + cmd.name()
        );

        return saved;
    }

    public void delete(String id, String actorId, String actorEmail, String actorRole) {
        Exercise existing = exerciseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Exercise", id));

        exerciseRepository.deleteById(id);

        auditService.log(
                actorId,
                actorEmail,
                actorRole,
                AuditAction.EXERCISE_DELETED,
                id,
                "Exercise",
                "Видалено вправу: " + existing.getName()
        );
    }
}
