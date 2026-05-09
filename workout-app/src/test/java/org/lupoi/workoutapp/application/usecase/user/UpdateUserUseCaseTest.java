package org.lupoi.workoutapp.application.usecase.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.domain.entity.user.User;
import org.lupoi.workoutapp.domain.enums.Role;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
    @author Andrii
    @project workout
    @class UpdateUserUseCaseTest
    @version 1.0.0
    @since 07.05.2026
*/

@ExtendWith(MockitoExtension.class)
class UpdateUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UpdateUserUseCase updateUserUseCase;

    private User existingUser;

    @BeforeEach
    void setUp() {
        existingUser = User.builder()
                .id("user-id-123")
                .email("test@example.com")
                .passwordHash("hashed")
                .firstName("OldFirst")
                .lastName("OldLast")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Успішно оновлює ім'я та прізвище")
    void execute_shouldUpdateUser_whenUserExists() {
        // given
        when(userRepository.findById("user-id-123")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        User result = updateUserUseCase.execute("user-id-123", "NewFirst", "NewLast");

        // then
        assertThat(result.getFirstName()).isEqualTo("NewFirst");
        assertThat(result.getLastName()).isEqualTo("NewLast");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getId()).isEqualTo("user-id-123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Зберігає роль та email без змін")
    void execute_shouldPreserveRoleAndEmail() {
        // given
        when(userRepository.findById("user-id-123")).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        User result = updateUserUseCase.execute("user-id-123", "A", "B");

        // then
        assertThat(result.getRole()).isEqualTo(Role.USER);
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getPasswordHash()).isEqualTo("hashed");
    }

    @Test
    @DisplayName("Кидає виняток якщо користувача не знайдено")
    void execute_shouldThrow_whenUserNotFound() {
        // given
        when(userRepository.findById("not-exist")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> updateUserUseCase.execute("not-exist", "A", "B"))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");

        verify(userRepository, never()).save(any());
    }
}