package org.lupoi.workoutapp.application.usecase.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.domain.entity.user.UserProfile;
import org.lupoi.workoutapp.domain.exception.ProfileNotFoundException;
import org.lupoi.workoutapp.domain.repository.UserProfileRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
    @author Andrii
    @project workout
    @class GetUserProfileUseCaseTest
    @version 1.0.0
    @since 07.05.2026
*/

@ExtendWith(MockitoExtension.class)
class GetUserProfileUseCaseTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private GetUserProfileUseCase getUserProfileUseCase;

    private UserProfile profile;

    @BeforeEach
    void setUp() {
        profile = UserProfile.builder()
                .id("profile-id")
                .userId("user-id-123")
                .build();
    }

    @Test
    @DisplayName("Повертає профіль за userId")
    void execute_shouldReturnProfile_whenExists() {
        // given
        when(userProfileRepository.findByUserId("user-id-123")).thenReturn(Optional.of(profile));

        // when
        UserProfile result = getUserProfileUseCase.execute("user-id-123");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo("user-id-123");
        verify(userProfileRepository).findByUserId("user-id-123");
    }

    @Test
    @DisplayName("Кидає ProfileNotFoundException якщо профілю немає")
    void execute_shouldThrow_whenProfileNotFound() {
        // given
        when(userProfileRepository.findByUserId("no-user")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> getUserProfileUseCase.execute("no-user"))
                .isInstanceOf(ProfileNotFoundException.class);

        verify(userProfileRepository).findByUserId("no-user");
    }
}