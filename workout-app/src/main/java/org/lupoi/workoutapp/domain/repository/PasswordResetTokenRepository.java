package org.lupoi.workoutapp.domain.repository;/*
    @author Andrii
    @project workout
    @class PasswordResetTokenRepository
    @version 1.0.0
    @since 09.05.2026 - 12.07
*/

import org.lupoi.workoutapp.domain.entity.PasswordResetToken;

import java.util.Optional;

public interface PasswordResetTokenRepository {
    PasswordResetToken save(PasswordResetToken token);
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUserId(String userId);
}
