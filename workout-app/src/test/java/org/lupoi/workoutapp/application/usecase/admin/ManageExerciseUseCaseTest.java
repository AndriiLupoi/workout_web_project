package org.lupoi.workoutapp.application.usecase.admin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.application.command.ExerciseCommand;
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

    @InjectMocks
    private ManageExerciseUseCase manageExerciseUseCase;

    private ExerciseCommand command;
    private Exercise existingExercise;

    @BeforeEach
    void setUp() {
        command = new ExerciseCommand("Bench Press", "CHEST", "INTERMEDIATE", "BARBELL", "Classic chest exercise", null);
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
        Exercise result = manageExerciseUseCase.create(command);

        // then
        assertThat(result.getName()).isEqualTo("Bench Press");
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    @DisplayName("Оновлює існуючу вправу")
    void update_shouldUpdateExercise_whenExists() {
        // given
        when(exerciseRepository.findById("ex-id")).thenReturn(Optional.of(existingExercise));
        when(exerciseRepository.save(any(Exercise.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        Exercise result = manageExerciseUseCase.update("ex-id", command);

        // then
        assertThat(result.getName()).isEqualTo("Bench Press");
        assertThat(result.getId()).isEqualTo("ex-id");
        verify(exerciseRepository).save(any(Exercise.class));
    }

    @Test
    @DisplayName("Кидає EntityNotFoundException при update якщо вправи немає")
    void update_shouldThrow_whenExerciseNotFound() {
        // given
        when(exerciseRepository.findById("not-exist")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> manageExerciseUseCase.update("not-exist", command))
                .isInstanceOf(EntityNotFoundException.class);

        verify(exerciseRepository, never()).save(any());
    }

    @Test
    @DisplayName("Видаляє вправу якщо вона існує")
    void delete_shouldDeleteExercise_whenExists() {
        // given
        when(exerciseRepository.findById("ex-id")).thenReturn(Optional.of(existingExercise));

        // when
        manageExerciseUseCase.delete("ex-id");

        // then
        verify(exerciseRepository).deleteById("ex-id");
    }

    @Test
    @DisplayName("Кидає EntityNotFoundException при delete якщо вправи немає")
    void delete_shouldThrow_whenExerciseNotFound() {
        // given
        when(exerciseRepository.findById("not-exist")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> manageExerciseUseCase.delete("not-exist"))
                .isInstanceOf(EntityNotFoundException.class);

        verify(exerciseRepository, never()).deleteById(any());
    }
}