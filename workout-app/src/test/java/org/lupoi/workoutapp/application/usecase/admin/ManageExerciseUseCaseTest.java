package org.lupoi.workoutapp.application.usecase.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.application.command.ExerciseCommand;
import org.lupoi.workoutapp.application.service.AuditService;
import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.exception.EntityNotFoundException;
import org.lupoi.workoutapp.domain.repository.ExerciseRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
    @author Andrii
    @project workout
    @class ManageExerciseUseCaseTest
    @version 1.0.0
    @since 07.05.2026
*/

@ExtendWith(MockitoExtension.class)
class ManageExerciseUseCaseTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ManageExerciseUseCase manageExerciseUseCase;

    private ExerciseCommand command;
    private Exercise existingExercise;

    // Тестові дані актора — передаємо в кожен метод
    private static final String ACTOR_ID    = "actor-id";
    private static final String ACTOR_EMAIL = "admin@example.com";
    private static final String ACTOR_ROLE  = "ADMIN";

    @BeforeEach
    void setUp() {
        command = new ExerciseCommand(
                "Bench Press", "CHEST", "INTERMEDIATE", "BARBELL",
                "Classic chest exercise", null
        );
        existingExercise = Exercise.builder()
                .id("ex-id")
                .name("Old Name")
                .build();
    }

    @Test
    @DisplayName("Створює нову вправу")
    void create_shouldSaveAndReturnExercise() {
        // given
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Exercise result = manageExerciseUseCase.create(command, ACTOR_ID, ACTOR_EMAIL, ACTOR_ROLE);

        // then
        assertThat(result.getName()).isEqualTo("Bench Press");
        verify(exerciseRepository).save(any(Exercise.class));
        verify(auditService).log(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Оновлює існуючу вправу")
    void update_shouldUpdateExercise_whenExists() {
        // given
        when(exerciseRepository.findById("ex-id")).thenReturn(Optional.of(existingExercise));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Exercise result = manageExerciseUseCase.update("ex-id", command, ACTOR_ID, ACTOR_EMAIL, ACTOR_ROLE);

        // then
        assertThat(result.getName()).isEqualTo("Bench Press");
        assertThat(result.getId()).isEqualTo("ex-id");
        verify(exerciseRepository).save(any(Exercise.class));
        verify(auditService).log(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Кидає EntityNotFoundException при update якщо вправи немає")
    void update_shouldThrow_whenExerciseNotFound() {
        // given
        when(exerciseRepository.findById("not-exist")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> manageExerciseUseCase.update("not-exist", command, ACTOR_ID, ACTOR_EMAIL, ACTOR_ROLE))
                .isInstanceOf(EntityNotFoundException.class);

        verify(exerciseRepository, never()).save(any());
        verify(auditService, never()).log(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Видаляє вправу якщо вона існує")
    void delete_shouldDeleteExercise_whenExists() {
        // given
        when(exerciseRepository.findById("ex-id")).thenReturn(Optional.of(existingExercise));

        // when
        manageExerciseUseCase.delete("ex-id", ACTOR_ID, ACTOR_EMAIL, ACTOR_ROLE);

        // then
        verify(exerciseRepository).deleteById("ex-id");
        verify(auditService).log(any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Кидає EntityNotFoundException при delete якщо вправи немає")
    void delete_shouldThrow_whenExerciseNotFound() {
        // given
        when(exerciseRepository.findById("not-exist")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> manageExerciseUseCase.delete("not-exist", ACTOR_ID, ACTOR_EMAIL, ACTOR_ROLE))
                .isInstanceOf(EntityNotFoundException.class);

        verify(exerciseRepository, never()).deleteById(any());
        verify(auditService, never()).log(any(), any(), any(), any(), any(), any(), any());
    }
}