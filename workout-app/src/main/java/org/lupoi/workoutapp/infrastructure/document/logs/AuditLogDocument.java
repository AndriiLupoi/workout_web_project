package org.lupoi.workoutapp.infrastructure.document.logs;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDocument {
    @Id
    private String id;

    @Indexed
    private String actorId;
    private String actorEmail;
    private String actorRole;

    @Indexed
    private String action;

    private String targetId;
    private String targetType;
    private String details;

    @Indexed
    private LocalDateTime createdAt;
}