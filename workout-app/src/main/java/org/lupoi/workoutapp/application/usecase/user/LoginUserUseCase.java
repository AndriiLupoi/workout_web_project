package org.lupoi.workoutapp.application.usecase.user;/*
    @author Andrii
    @project workout
    @class LoginUserUseCase
    @version 1.0.0
    @since 25.03.2026 - 20.50
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.command.LoginUserCommand;
import org.lupoi.workoutapp.application.port.PasswordHasher;
import org.lupoi.workoutapp.application.port.TokenProvider;
import org.lupoi.workoutapp.domain.exception.EntityNotFoundException;
import org.lupoi.workoutapp.domain.exception.InvalidCredentialsException;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenProvider tokenProvider;

    public String execute(LoginUserCommand command) {
        var user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new EntityNotFoundException("User", command.email()));

        if (!passwordHasher.matches(command.password(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return tokenProvider.generateToken(user.getId(), user.getEmail());
    }
}
