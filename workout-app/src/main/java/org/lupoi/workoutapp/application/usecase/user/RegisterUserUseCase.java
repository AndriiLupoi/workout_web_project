package org.lupoi.workoutapp.application.usecase.user;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.command.RegisterUserCommand;
import org.lupoi.workoutapp.application.port.PasswordHasher;
import org.lupoi.workoutapp.application.service.AuditService;
import org.lupoi.workoutapp.domain.entity.user.User;
import org.lupoi.workoutapp.domain.enums.AuditAction;
import org.lupoi.workoutapp.domain.exception.EmailAlreadyExistsException;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final AuditService auditService;

    public User execute(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new EmailAlreadyExistsException(command.email());
        }

        User user = User.builder()
                .email(command.email())
                .passwordHash(passwordHasher.hash(command.password()))
                .firstName(command.firstName())
                .lastName(command.lastName())
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);

        auditService.log(
                saved.getId(),
                saved.getEmail(),
                saved.getRole() != null ? saved.getRole().name() : "USER",
                AuditAction.USER_CREATED,
                saved.getId(),
                "User",
                "Реєстрація нового користувача: " + saved.getEmail()
        );

        return saved;
    }
}
