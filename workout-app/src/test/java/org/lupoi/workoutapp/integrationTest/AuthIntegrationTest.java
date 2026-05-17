package org.lupoi.workoutapp.integrationTest;/*
    @author Andrii
    @project workout
    @class AuthIntegrationTest
    @version 1.0.0
    @since 17.05.2026 - 23.46
*/

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.lupoi.workoutapp.application.command.RegisterUserCommand;
import org.lupoi.workoutapp.application.command.LoginUserCommand;
import org.lupoi.workoutapp.application.usecase.user.RegisterUserUseCase;
import org.lupoi.workoutapp.application.usecase.user.LoginUserUseCase;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.exception.EmailAlreadyExistsException;
import org.lupoi.workoutapp.domain.exception.InvalidCredentialsException;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private LoginUserUseCase loginUserUseCase;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanUp() {
        userRepository.findByEmail("integration@test.com")
                .ifPresent(u -> userRepository.deleteById(u.getId()));
    }

    @Test
    @DisplayName("Реєстрація і логін — повний цикл")
    void register_thenLogin_shouldReturnToken() {
        // register
        RegisterUserCommand reg = new RegisterUserCommand(
                "integration@test.com", "Password123!", "John", "Doe"
        );
        User user = registerUserUseCase.execute(reg);
        assertThat(user.getId()).isNotNull();
        assertThat(user.getEmail()).isEqualTo("integration@test.com");

        // login
        LoginUserCommand login = new LoginUserCommand("integration@test.com", "Password123!");
        String token = loginUserUseCase.execute(login);
        assertThat(token).isNotBlank();
    }

    @Test
    @DisplayName("Повторна реєстрація з тим самим email кидає виняток")
    void register_twice_shouldThrow() {
        RegisterUserCommand reg = new RegisterUserCommand(
                "integration@test.com", "Password123!", "John", "Doe"
        );
        registerUserUseCase.execute(reg);

        assertThatThrownBy(() -> registerUserUseCase.execute(reg))
                .isInstanceOf(EmailAlreadyExistsException.class);
    }

    @Test
    @DisplayName("Логін з невірним паролем кидає виняток")
    void login_wrongPassword_shouldThrow() {
        RegisterUserCommand reg = new RegisterUserCommand(
                "integration@test.com", "Password123!", "John", "Doe"
        );
        registerUserUseCase.execute(reg);

        LoginUserCommand login = new LoginUserCommand("integration@test.com", "WrongPass!");
        assertThatThrownBy(() -> loginUserUseCase.execute(login))
                .isInstanceOf(InvalidCredentialsException.class);
    }
}