package org.lupoi.workoutapp.domain.entity;/*
    @author Andrii
    @project workout
    @class AuditLog
    @version 1.0.0
    @since 09.05.2026 - 14.57
*/

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.lupoi.workoutapp.domain.enums.AuditAction;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    private String id;
    private String actorId;
    private String actorEmail;
    private String actorRole;
    private AuditAction action;
    private String targetId;
    private String targetType;
    private String details;
    private LocalDateTime createdAt;
}

