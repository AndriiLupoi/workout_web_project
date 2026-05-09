package org.lupoi.workoutapp.infrastructure.repository;/*
    @author Andrii
    @project workout
    @class MongoPasswordResetTokenRepository
    @version 1.0.0
    @since 09.05.2026 - 12.07
*/

import org.lupoi.workoutapp.infrastructure.document.PasswordResetTokenDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MongoPasswordResetTokenRepository
        extends MongoRepository<PasswordResetTokenDocument, String> {
    Optional<PasswordResetTokenDocument> findByToken(String token);
    void deleteByUserId(String userId);
}