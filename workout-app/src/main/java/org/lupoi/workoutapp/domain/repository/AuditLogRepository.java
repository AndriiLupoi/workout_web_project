package org.lupoi.workoutapp.domain.repository;/*
    @author Andrii
    @project workout
    @class AuditLogRepository
    @version 1.0.0
    @since 09.05.2026 - 14.58
*/

import org.lupoi.workoutapp.domain.entity.logs.AuditLog;
import org.lupoi.workoutapp.domain.model.PageResult;

public interface AuditLogRepository {
    AuditLog save(AuditLog log);
    PageResult<AuditLog> findAll(int page, int size);
    PageResult<AuditLog> findByAction(String action, int page, int size);
    PageResult<AuditLog> findByDateRange(String from, String to, int page, int size);
}

