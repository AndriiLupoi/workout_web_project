package org.lupoi.workoutapp.application.usecase.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.application.command.SaveUserProfileCommand;
import org.lupoi.workoutapp.domain.entity.UserProfile;
import org.lupoi.workoutapp.domain.enums.FitnessLevel;
import org.lupoi.workoutapp.domain.enums.PlanType;
import org.lupoi.workoutapp.domain.enums.TrainingGoal;
import org.lupoi.workoutapp.domain.repository.BodyWeightLogRepository;
import org.lupoi.workoutapp.domain.repository.UserProfileRepository;
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
    @class SaveUserProfileUseCaseTest
    @version 1.0.0
    @since 07.05.2026
*/

@ExtendWith(MockitoExtension.class)
class SaveUserProfileUseCaseTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private BodyWeightLogRepository bodyWeightLogRepository;

    @InjectMocks
    private SaveUserProfileUseCase saveUserProfileUseCase;

    private SaveUserProfileCommand command;

    @BeforeEach
    void setUp() {
        command = new SaveUserProfileCommand(
                TrainingGoal.MASS,
                FitnessLevel.INTERMEDIATE,
                PlanType.HYPERTROPHY,
                3,
                80.0,
                75.0,
                80.0,
                180.0,
                25,
                List.of("BARBELL", "DUMBBELL")
        );
    }

    @Test
    @DisplayName("Створює новий профіль якщо не існує")
    void execute_shouldCreateProfile_whenNotExists() {
        // given
        when(userProfileRepository.findByUserId("user-id")).thenReturn(Optional.empty());
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        UserProfile result = saveUserProfileUseCase.execute("user-id", command);

        // then
        assertThat(result.getUserId()).isEqualTo("user-id");
        assertThat(result.getGoal()).isEqualTo(TrainingGoal.MASS);
        assertThat(result.getId()).isNull(); // новий профіль без id
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("Оновлює існуючий профіль зберігаючи id")
    void execute_shouldUpdateProfile_whenExists() {
        // given
        UserProfile existing = UserProfile.builder().id("existing-profile-id").userId("user-id").build();
        when(userProfileRepository.findByUserId("user-id")).thenReturn(Optional.of(existing));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        UserProfile result = saveUserProfileUseCase.execute("user-id", command);

        // then
        assertThat(result.getId()).isEqualTo("existing-profile-id");
        verify(userProfileRepository).save(any(UserProfile.class));
    }

    @Test
    @DisplayName("Зберігає лог ваги якщо currentWeight > 0")
    void execute_shouldSaveBodyWeightLog_whenWeightProvided() {
        // given
        when(userProfileRepository.findByUserId("user-id")).thenReturn(Optional.empty());
        when(userProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        saveUserProfileUseCase.execute("user-id", command);

        // then
        verify(bodyWeightLogRepository).save(any());
    }

    @Test
    @DisplayName("Не зберігає лог ваги якщо currentWeight = null")
    void execute_shouldNotSaveBodyWeightLog_whenWeightIsNull() {
        // given
        SaveUserProfileCommand noWeightCmd = new SaveUserProfileCommand(
                TrainingGoal.MASS, FitnessLevel.BEGINNER, PlanType.HYPERTROPHY,
                3, null, null, null, null, null, List.of()
        );
        when(userProfileRepository.findByUserId("user-id")).thenReturn(Optional.empty());
        when(userProfileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        saveUserProfileUseCase.execute("user-id", noWeightCmd);

        // then
        verify(bodyWeightLogRepository, never()).save(any());
    }
}