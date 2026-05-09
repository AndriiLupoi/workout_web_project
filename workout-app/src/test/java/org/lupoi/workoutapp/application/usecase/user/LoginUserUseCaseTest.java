package org.lupoi.workoutapp.application.usecase.user;

/*
    @author Andrii
    @project workout
    @class LoginUserUseCaseTest
    @version 1.0.0
    @since 07.05.2026 - 18.38
*/

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.lupoi.workoutapp.application.command.LoginUserCommand;
import org.lupoi.workoutapp.application.port.PasswordHasher;
import org.lupoi.workoutapp.application.port.TokenProvider;
import org.lupoi.workoutapp.domain.entity.user.User;
import org.lupoi.workoutapp.domain.enums.Role;
import org.lupoi.workoutapp.domain.exception.EntityNotFoundException;
import org.lupoi.workoutapp.domain.exception.InvalidCredentialsException;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LoginUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenProvider tokenProvider;

    @InjectMocks
    private LoginUserUseCase loginUserUseCase;

    private User user;
    private LoginUserCommand command;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("user-id-123")
                .email("test@example.com")
                .passwordHash("hashed_password")
                .firstName("John")
                .lastName("Doe")
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        command = new LoginUserCommand("test@example.com", "password123");
    }

    @Test
    @DisplayName("Успішний логін — повертає токен")
    void execute_shouldReturnToken_whenCredentialsValid() {
        // given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("password123", "hashed_password")).thenReturn(true);
        when(tokenProvider.generateToken("user-id-123", "test@example.com", "USER"))
                .thenReturn("jwt.token.here");

        // when
        String token = loginUserUseCase.execute(command);

        // then
        assertThat(token).isEqualTo("jwt.token.here");
        verify(tokenProvider).generateToken("user-id-123", "test@example.com", "USER");
    }

    @Test
    @DisplayName("Кидає виняток якщо email не знайдено")
    void execute_shouldThrow_whenEmailNotFound() {
        // given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> loginUserUseCase.execute(command))
                .isInstanceOf(EntityNotFoundException.class);

        verify(passwordHasher, never()).matches(any(), any());
        verify(tokenProvider, never()).generateToken(any(), any(), any());
    }

    @Test
    @DisplayName("Кидає виняток якщо пароль невірний")
    void execute_shouldThrow_whenPasswordInvalid() {
        // given
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("password123", "hashed_password")).thenReturn(false);

        // when / then
        assertThatThrownBy(() -> loginUserUseCase.execute(command))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(tokenProvider, never()).generateToken(any(), any(), any());
    }

    @Test
    @DisplayName("Токен генерується з правильною роллю ADMIN")
    void execute_shouldGenerateToken_withCorrectAdminRole() {
        // given
        User adminUser = User.builder()
                .id("admin-id")
                .email("admin@example.com")
                .passwordHash("hashed")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .build();

        LoginUserCommand adminCommand = new LoginUserCommand("admin@example.com", "pass");

        when(userRepository.findByEmail("admin@example.com")).thenReturn(Optional.of(adminUser));
        when(passwordHasher.matches("pass", "hashed")).thenReturn(true);
        when(tokenProvider.generateToken("admin-id", "admin@example.com", "ADMIN"))
                .thenReturn("admin.token");

        // when
        String token = loginUserUseCase.execute(adminCommand);

        // then
        assertThat(token).isEqualTo("admin.token");
        verify(tokenProvider).generateToken("admin-id", "admin@example.com", "ADMIN");
    }
}
