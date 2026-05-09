package org.lupoi.workoutapp.application.usecase.user.resetPassword;/*
    @author Andrii
    @project workout
    @class ForgotPasswordUseCase
    @version 1.0.0
    @since 09.05.2026 - 12.10
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.PasswordResetToken;
import org.lupoi.workoutapp.domain.repository.PasswordResetTokenRepository;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.lupoi.workoutapp.infrastructure.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForgotPasswordUseCase {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void execute(String email) {
        // Якщо email не знайдено — не кидаємо помилку (безпека)
        userRepository.findByEmail(email).ifPresent(user -> {
            // Видаляємо старі токени
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
            emailService.sendPasswordResetEmail(email, resetLink);
        });
    }
}
