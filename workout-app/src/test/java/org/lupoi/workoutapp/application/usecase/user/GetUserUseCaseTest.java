package org.lupoi.workoutapp.application.usecase.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.enums.Role;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/*
    @author Andrii
    @project workout
    @class GetUserUseCaseTest
    @version 1.0.0
    @since 07.05.2026
*/

@ExtendWith(MockitoExtension.class)
class GetUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private GetUserUseCase getUserUseCase;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("user-id-123")
                .email("test@example.com")
                .passwordHash("hashed")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Повертає користувача за існуючим ID")
    void execute_shouldReturnUser_whenIdExists() {
        // given
        when(userRepository.findById("user-id-123")).thenReturn(Optional.of(user));

        // when
        User result = getUserUseCase.execute("user-id-123");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("user-id-123");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository).findById("user-id-123");
    }

    @Test
    @DisplayName("Кидає виняток якщо користувача не знайдено")
    void execute_shouldThrow_whenUserNotFound() {
        // given
        when(userRepository.findById("not-exist")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> getUserUseCase.execute("not-exist"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findById("not-exist");
    }
}