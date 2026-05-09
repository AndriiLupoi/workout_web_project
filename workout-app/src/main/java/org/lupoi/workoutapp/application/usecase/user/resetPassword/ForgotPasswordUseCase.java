package org.lupoi.workoutapp.application.usecase.user.resetPassword;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.port.EmailPort;
import org.lupoi.workoutapp.application.service.AuditService;
import org.lupoi.workoutapp.domain.entity.PasswordResetToken;
import org.lupoi.workoutapp.domain.enums.AuditAction;
import org.lupoi.workoutapp.domain.repository.PasswordResetTokenRepository;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/*
    @author Andrii
    @project workout
    @class ForgotPasswordUseCase
    @version 1.0.0
    @since 09.05.2026 - 12.10
*/
@Service
@RequiredArgsConstructor
public class ForgotPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailPort emailPort;
    private final AuditService auditService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void execute(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            tokenRepository.deleteByUserId(user.getId());

            String token = UUID.randomUUID().toString();

            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .userId(user.getId())
                    .token(token)
                    .expiresAt(LocalDateTime.now().plusHours(1))
                    .used(false)
                    .build();

            tokenRepository.save(resetToken);

            String resetLink = frontendUrl + "/auth/reset-password?token=" + token;
            emailPort.sendPasswordResetEmail(email, resetLink);

            auditService.log(
                    user.getId(),
                    email,
                    user.getRole() != null ? user.getRole().name() : "USER",
                    AuditAction.PASSWORD_RESET_REQUESTED,
                    user.getId(),
                    "User",
                    "Запит скидання пароля"
            );
        });
    }
}
