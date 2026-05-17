package org.lupoi.workoutapp.application.usecase.workout;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.application.strategy.PlanGenerationStrategy;
import org.lupoi.workoutapp.application.usecase.workout.plan.GenerateWorkoutPlanUseCase;
import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.entity.UserProfile;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;
import org.lupoi.workoutapp.domain.exception.ProfileNotFoundException;
import org.lupoi.workoutapp.domain.repository.ExerciseRepository;
import org.lupoi.workoutapp.domain.repository.UserProfileRepository;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
    @author Andrii
    @project workout
    @class GenerateWorkoutPlanUseCaseTest
    @version 1.0.0
    @since 07.05.2026
*/

@ExtendWith(MockitoExtension.class)
class GenerateWorkoutPlanUseCaseTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @Mock
    private WorkoutPlanRepository workoutPlanRepository;

    @Mock
    private PlanGenerationStrategy planGenerationStrategy;

    @InjectMocks
    private GenerateWorkoutPlanUseCase generateWorkoutPlanUseCase;

    private UserProfile profile;
    private WorkoutPlan generatedPlan;

    @BeforeEach
    void setUp() {
        profile = UserProfile.builder()
                .id("profile-id")
                .userId("user-id")
                .build();

        generatedPlan = WorkoutPlan.builder()
                .id("plan-id")
                .userId("user-id")
                .title("Hypertrophy Plan — 8 weeks")
                .build();
    }

    @Test
    @DisplayName("Успішно генерує і зберігає план тренувань")
    void execute_shouldGenerateAndSavePlan_whenProfileExists() {
        // given
        List<Exercise> exercises = List.of(Exercise.builder().id("ex-1").name("Bench Press").build());
        when(userProfileRepository.findByUserId("user-id")).thenReturn(Optional.of(profile));
        when(exerciseRepository.findAll()).thenReturn(exercises);
        when(planGenerationStrategy.generate(profile, exercises)).thenReturn(generatedPlan);
        when(workoutPlanRepository.save(generatedPlan)).thenReturn(generatedPlan);

        // when
        WorkoutPlan result = generateWorkoutPlanUseCase.execute("user-id");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("plan-id");
        verify(planGenerationStrategy).generate(profile, exercises);
        verify(workoutPlanRepository).save(generatedPlan);
    }

    @Test
    @DisplayName("Кидає ProfileNotFoundException якщо профілю немає")
    void execute_shouldThrow_whenProfileNotFound() {
        // given
        when(userProfileRepository.findByUserId("no-user")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> generateWorkoutPlanUseCase.execute("no-user"))
                .isInstanceOf(ProfileNotFoundException.class);

        verify(planGenerationStrategy, never()).generate(any(), any());
        verify(workoutPlanRepository, never()).save(any());
    }

    @Test
    @DisplayName("Передає всі вправи у стратегію")
    void execute_shouldPassAllExercisesToStrategy() {
        // given
        List<Exercise> exercises = List.of(
                Exercise.builder().id("ex-1").name("Squat").build(),
                Exercise.builder().id("ex-2").name("Deadlift").build()
        );
        when(userProfileRepository.findByUserId("user-id")).thenReturn(Optional.of(profile));
        when(exerciseRepository.findAll()).thenReturn(exercises);
        when(planGenerationStrategy.generate(any(), any())).thenReturn(generatedPlan);
        when(workoutPlanRepository.save(any())).thenReturn(generatedPlan);

        // when
        generateWorkoutPlanUseCase.execute("user-id");

        // then
        verify(planGenerationStrategy).generate(profile, exercises);
    }
}