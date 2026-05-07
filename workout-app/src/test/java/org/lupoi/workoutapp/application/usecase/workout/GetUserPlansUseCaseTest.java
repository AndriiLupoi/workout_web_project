package org.lupoi.workoutapp.application.usecase.workout;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
    @author Andrii
    @project workout
    @class GetUserPlansUseCaseTest
    @version 1.0.0
    @since 07.05.2026
*/

@ExtendWith(MockitoExtension.class)
class GetUserPlansUseCaseTest {

    @Mock
    private WorkoutPlanRepository workoutPlanRepository;

    @InjectMocks
    private GetUserPlansUseCase getUserPlansUseCase;

    @Test
    @DisplayName("Повертає список планів для userId")
    void execute_shouldReturnPlans_forUser() {
        // given
        List<WorkoutPlan> plans = List.of(
                WorkoutPlan.builder().id("plan-1").userId("user-id").build(),
                WorkoutPlan.builder().id("plan-2").userId("user-id").build()
        );
        when(workoutPlanRepository.findByUserId("user-id")).thenReturn(plans);

        // when
        List<WorkoutPlan> result = getUserPlansUseCase.execute("user-id");

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(WorkoutPlan::getId).containsExactly("plan-1", "plan-2");
        verify(workoutPlanRepository).findByUserId("user-id");
    }

    @Test
    @DisplayName("Повертає порожній список якщо планів немає")
    void execute_shouldReturnEmptyList_whenNoPlans() {
        // given
        when(workoutPlanRepository.findByUserId("user-id")).thenReturn(List.of());

        // when
        List<WorkoutPlan> result = getUserPlansUseCase.execute("user-id");

        // then
        assertThat(result).isEmpty();
    }
}