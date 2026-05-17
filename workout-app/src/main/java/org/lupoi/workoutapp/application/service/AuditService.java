package org.lupoi.workoutapp.application.service;/*
    @author Andrii
    @project workout
    @class AuditService
    @version 1.0.0
    @since 09.05.2026 - 15.04
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.AuditLog;
import org.lupoi.workoutapp.domain.enums.AuditAction;
import org.lupoi.workoutapp.domain.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    public void log(String actorId,
                    String actorEmail,
                    String actorRole,
                    AuditAction action,
                    String targetId,
                    String targetType,
                    String details) {
        AuditLog entry = AuditLog.builder()
                .actorId(actorId)
                .actorEmail(actorEmail)
                .actorRole(actorRole)
                .action(action)
                .targetId(targetId)
                .targetType(targetType)
                .details(details)
                .createdAt(LocalDateTime.now())
                .build();
        auditLogRepository.save(entry);
    }
}

