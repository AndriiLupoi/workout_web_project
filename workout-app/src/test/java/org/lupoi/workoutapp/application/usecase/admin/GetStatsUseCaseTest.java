package org.lupoi.workoutapp.application.usecase.admin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.domain.entity.user.User;
import org.lupoi.workoutapp.domain.enums.Role;
import org.lupoi.workoutapp.domain.model.StatsResult;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
    @author Andrii
    @project workout
    @class GetStatsUseCaseTest
    @version 1.0.0
    @since 07.05.2026
*/

@ExtendWith(MockitoExtension.class)
class GetStatsUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private WorkoutPlanRepository workoutPlanRepository;

    @InjectMocks
    private GetStatsUseCase getStatsUseCase;

    @Test
    @DisplayName("Повертає правильну статистику")
    void execute_shouldReturnCorrectStats() {
        // given
        List<User> users = List.of(
                buildUser("1", Role.USER),
                buildUser("2", Role.USER),
                buildUser("3", Role.ADMIN)
        );
        when(userRepository.findAll()).thenReturn(users);
        when(workoutPlanRepository.countAll()).thenReturn(10L);

        // when
        StatsResult result = getStatsUseCase.execute();

        // then
        assertThat(result.totalUsers()).isEqualTo(3);
        assertThat(result.totalAdmins()).isEqualTo(1);
        assertThat(result.totalPlans()).isEqualTo(10);
    }

    @Test
    @DisplayName("Повертає нулі якщо немає даних")
    void execute_shouldReturnZeros_whenNoData() {
        // given
        when(userRepository.findAll()).thenReturn(List.of());
        when(workoutPlanRepository.countAll()).thenReturn(0L);

        // when
        StatsResult result = getStatsUseCase.execute();

        // then
        assertThat(result.totalUsers()).isEqualTo(0);
        assertThat(result.totalAdmins()).isEqualTo(0);
        assertThat(result.totalPlans()).isEqualTo(0);
    }

    private User buildUser(String id, Role role) {
        return User.builder()
                .id(id)
                .email(id + "@test.com")
                .role(role)
                .createdAt(LocalDateTime.now())
                .build();
    }
}