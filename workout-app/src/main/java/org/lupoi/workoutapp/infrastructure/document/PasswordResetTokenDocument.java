package org.lupoi.workoutapp.infrastructure.document;/*
    @author Andrii
    @project workout
    @class PasswordResetTokenDocument
    @version 1.0.0
    @since 09.05.2026 - 12.06
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "password_reset_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetTokenDocument {
    @Id
    private String id;
    private String userId;
    private String token;
    private LocalDateTime expiresAt;
    private boolean used;
}
