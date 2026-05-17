package org.lupoi.workoutapp.application.usecase.workout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.application.usecase.workout.exercises.GetExercisesUseCase;
import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.enums.Difficulty;
import org.lupoi.workoutapp.domain.enums.EquipmentType;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;
import org.lupoi.workoutapp.domain.repository.ExerciseRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
    @author Andrii
    @project workout
    @class GetExercisesUseCaseTest
    @version 1.0.0
    @since 07.05.2026
*/

@ExtendWith(MockitoExtension.class)
class GetExercisesUseCaseTest {

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private GetExercisesUseCase getExercisesUseCase;

    @Test
    @DisplayName("Повертає вправи з усіма фільтрами")
    void execute_shouldReturnExercises_withFilters() {
        // given
        List<Exercise> exercises = List.of(
                Exercise.builder().id("ex-1").name("Bench Press")
                        .muscleGroup(MuscleGroup.CHEST).difficulty(Difficulty.INTERMEDIATE)
                        .equipmentType(EquipmentType.BARBELL).build()
        );
        when(exerciseRepository.findByFilters(MuscleGroup.CHEST, Difficulty.INTERMEDIATE, EquipmentType.BARBELL, "name"))
                .thenReturn(exercises);

        // when
        List<Exercise> result = getExercisesUseCase.execute(MuscleGroup.CHEST, Difficulty.INTERMEDIATE, EquipmentType.BARBELL, "name");

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Bench Press");
        verify(exerciseRepository).findByFilters(MuscleGroup.CHEST, Difficulty.INTERMEDIATE, EquipmentType.BARBELL, "name");
    }

    @Test
    @DisplayName("Передає null фільтри до репозиторію")
    void execute_shouldPassNullFilters_whenNoFiltersProvided() {
        // given
        when(exerciseRepository.findByFilters(null, null, null, null)).thenReturn(List.of());

        // when
        List<Exercise> result = getExercisesUseCase.execute(null, null, null, null);

        // then
        assertThat(result).isEmpty();
        verify(exerciseRepository).findByFilters(null, null, null, null);
    }
}