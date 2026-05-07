package org.lupoi.workoutapp.application.usecase.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.application.command.RegisterUserCommand;
import org.lupoi.workoutapp.application.port.PasswordHasher;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.exception.EmailAlreadyExistsException;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/*
    @author Andrii
    @project workout
    @class RegisterUserUseCaseTest
    @version 1.0.0
    @since 07.05.2026 - 18.35
*/

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private RegisterUserCommand command;

    @BeforeEach
    void setUp() {
        command = new RegisterUserCommand(
                "test@example.com",
                "password123",
                "John",
                "Doe"
        );
    }

    @Test
    @DisplayName("Успішна реєстрація нового користувача")
    void execute_shouldRegisterUser_whenEmailNotExists() {
        // given
        when(userRepository.existsByEmail(command.email())).thenReturn(false);
        when(passwordHasher.hash(command.password())).thenReturn("hashed_password");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        User result = registerUserUseCase.execute(command);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getPasswordHash()).isEqualTo("hashed_password");
        assertThat(result.getCreatedAt()).isNotNull();

        verify(userRepository).existsByEmail("test@example.com");
        verify(passwordHasher).hash("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Кидає виняток якщо email вже існує")
    void execute_shouldThrow_whenEmailAlreadyExists() {
        // given
        when(userRepository.existsByEmail(command.email())).thenReturn(true);

        // when / then
        assertThatThrownBy(() -> registerUserUseCase.execute(command))
                .isInstanceOf(EmailAlreadyExistsException.class)
                .hasMessageContaining("test@example.com");

        verify(userRepository).existsByEmail("test@example.com");
        verify(userRepository, never()).save(any());
        verify(passwordHasher, never()).hash(any());
    }

    @Test
    @DisplayName("Пароль хешується перед збереженням")
    void execute_shouldHashPassword_beforeSaving() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordHasher.hash("password123")).thenReturn("$2a$10$hashedvalue");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        User result = registerUserUseCase.execute(command);

        // then
        assertThat(result.getPasswordHash()).isEqualTo("$2a$10$hashedvalue");
        assertThat(result.getPasswordHash()).isNotEqualTo("password123");
    }

}