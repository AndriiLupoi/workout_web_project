package org.lupoi.workoutapp.application.usecase.user.resetPassword;/*
    @author Andrii
    @project workout
    @class ResetPasswordUseCase
    @version 1.0.0
    @since 09.05.2026 - 12.11
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.port.PasswordHasher;
import org.lupoi.workoutapp.application.service.AuditService;
import org.lupoi.workoutapp.domain.entity.PasswordResetToken;
import org.lupoi.workoutapp.domain.entity.user.User;
import org.lupoi.workoutapp.domain.enums.AuditAction;
import org.lupoi.workoutapp.domain.exception.DomainException;
import org.lupoi.workoutapp.domain.exception.EntityNotFoundException;
import org.lupoi.workoutapp.domain.repository.PasswordResetTokenRepository;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ResetPasswordUseCase {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final AuditService auditService;

    public void execute(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new DomainException("Невалідний або прострочений токен"));

        if (resetToken.isUsed()) {
            throw new DomainException("Токен вже використано");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new DomainException("Токен прострочений");
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User", resetToken.getUserId()));

        User updated = User.builder()
                .id(user.getId())
                .email(user.getEmail())
                .passwordHash(passwordHasher.hash(newPassword))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .createdAt(user.getCreatedAt())
                .role(user.getRole())
                .build();

        userRepository.save(updated);

        PasswordResetToken usedToken = PasswordResetToken.builder()
                .id(resetToken.getId())
                .userId(resetToken.getUserId())
                .token(resetToken.getToken())
                .expiresAt(resetToken.getExpiresAt())
                .used(true)
                .build();

        tokenRepository.save(usedToken);

        auditService.log(
                user.getId(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : "USER",
                AuditAction.PASSWORD_RESET_COMPLETED,
                user.getId(),
                "User",
                "Пароль успішно змінено"
        );
    }
}
