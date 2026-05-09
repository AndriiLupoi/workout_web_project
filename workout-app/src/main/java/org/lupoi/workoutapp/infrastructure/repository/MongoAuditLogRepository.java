package org.lupoi.workoutapp.infrastructure.repository;/*
    @author Andrii
    @project workout
    @class MongoAuditLogRepository
    @version 1.0.0
    @since 09.05.2026 - 15.01
*/

import org.lupoi.workoutapp.infrastructure.document.logs.AuditLogDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;

public interface MongoAuditLogRepository extends MongoRepository<AuditLogDocument, String> {
    Page<AuditLogDocument> findByAction(String action, Pageable pageable);
    Page<AuditLogDocument> findByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}

