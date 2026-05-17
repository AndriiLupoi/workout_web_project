package org.lupoi.workoutapp.application.usecase.workout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.application.service.AuditService;
import org.lupoi.workoutapp.application.usecase.workout.plan.DeleteWorkoutPlanUseCase;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;
import org.lupoi.workoutapp.domain.exception.EntityNotFoundException;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
    @author Andrii
    @project workout
    @class DeleteWorkoutPlanUseCaseTest
    @version 1.0.0
    @since 07.05.2026
*/

@ExtendWith(MockitoExtension.class)
class DeleteWorkoutPlanUseCaseTest {

    @Mock
    private WorkoutPlanRepository workoutPlanRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private DeleteWorkoutPlanUseCase deleteWorkoutPlanUseCase;

    @Test
    @DisplayName("Успішно видаляє план якщо він належить користувачу")
    void execute_shouldDeletePlan_whenPlanBelongsToUser() {
        // given
        WorkoutPlan plan = WorkoutPlan.builder().id("plan-id").userId("user-id").build();
        when(workoutPlanRepository.findByIdAndUserId("plan-id", "user-id")).thenReturn(Optional.of(plan));

        // when
        deleteWorkoutPlanUseCase.execute("user-id", "plan-id");

        // then
        verify(workoutPlanRepository).deleteById("plan-id");
    }

    @Test
    @DisplayName("Кидає EntityNotFoundException якщо план не знайдено")
    void execute_shouldThrow_whenPlanNotFound() {
        // given
        when(workoutPlanRepository.findByIdAndUserId("plan-id", "user-id")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> deleteWorkoutPlanUseCase.execute("user-id", "plan-id"))
                .isInstanceOf(EntityNotFoundException.class);

        verify(workoutPlanRepository, never()).deleteById(any());
    }
}