package org.lupoi.workoutapp.presentation.dto.response.logs;/*
    @author Andrii
    @project workout
    @class AuditLogResponse
    @version 1.0.0
    @since 09.05.2026 - 15.04
*/

public record AuditLogResponse(
        String id,
        String actorId,
        String actorEmail,
        String actorRole,
        String action,
        String targetId,
        String targetType,
        String details,
        String createdAt
) {}

