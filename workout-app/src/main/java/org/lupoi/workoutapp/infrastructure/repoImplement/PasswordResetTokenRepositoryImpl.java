package org.lupoi.workoutapp.infrastructure.repoImplement;/*
    @author Andrii
    @project workout
    @class PasswordResetTokenRepositoryImpl
    @version 1.0.0
    @since 09.05.2026 - 12.07
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.PasswordResetToken;
import org.lupoi.workoutapp.domain.repository.PasswordResetTokenRepository;
import org.lupoi.workoutapp.infrastructure.document.PasswordResetTokenDocument;
import org.lupoi.workoutapp.infrastructure.repository.MongoPasswordResetTokenRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository {

    private final MongoPasswordResetTokenRepository mongo;

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenDocument doc = PasswordResetTokenDocument.builder()
                .id(token.getId())
                .userId(token.getUserId())
                .token(token.getToken())
                .expiresAt(token.getExpiresAt())
                .used(token.isUsed())
                .build();
        PasswordResetTokenDocument saved = mongo.save(doc);
        return toDomain(saved);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return mongo.findByToken(token).map(this::toDomain);
    }

    @Override
    public void deleteByUserId(String userId) {
        mongo.deleteByUserId(userId);
    }

    private PasswordResetToken toDomain(PasswordResetTokenDocument doc) {
        return PasswordResetToken.builder()
                .id(doc.getId())
                .userId(doc.getUserId())
                .token(doc.getToken())
                .expiresAt(doc.getExpiresAt())
                .used(doc.isUsed())
                .build();
    }
}
