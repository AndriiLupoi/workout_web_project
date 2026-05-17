package org.lupoi.workoutapp.infrastructure.repoImplement;/*
    @author Andrii
    @project workout
    @class AuditLogRepositoryImpl
    @version 1.0.0
    @since 09.05.2026 - 15.01
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.AuditLog;
import org.lupoi.workoutapp.domain.enums.AuditAction;
import org.lupoi.workoutapp.domain.model.PageResult;
import org.lupoi.workoutapp.domain.repository.AuditLogRepository;
import org.lupoi.workoutapp.infrastructure.document.logs.AuditLogDocument;
import org.lupoi.workoutapp.infrastructure.repository.MongoAuditLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final MongoAuditLogRepository mongo;

    @Override
    public AuditLog save(AuditLog log) {
        AuditLogDocument doc = AuditLogDocument.builder()
                .id(log.getId())
                .actorId(log.getActorId())
                .actorEmail(log.getActorEmail())
                .actorRole(log.getActorRole())
                .action(log.getAction() != null ? log.getAction().name() : null)
                .targetId(log.getTargetId())
                .targetType(log.getTargetType())
                .details(log.getDetails())
                .createdAt(log.getCreatedAt())
                .build();
        return toDomain(mongo.save(doc));
    }

    @Override
    public PageResult<AuditLog> findAll(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> result = mongo.findAll(pageable).map(this::toDomain);
        return toPageResult(result);
    }

    @Override
    public PageResult<AuditLog> findByAction(String action, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> result = mongo.findByAction(action, pageable).map(this::toDomain);
        return toPageResult(result);
    }

    @Override
    public PageResult<AuditLog> findByDateRange(String from, String to, int page, int size) {
        LocalDateTime fromDt = LocalDate.parse(from).atStartOfDay();
        LocalDateTime toDt   = LocalDate.parse(to).atTime(23, 59, 59);
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<AuditLog> result = mongo.findByCreatedAtBetween(fromDt, toDt, pageable).map(this::toDomain);
        return toPageResult(result);
    }

    private AuditLog toDomain(AuditLogDocument doc) {
        return AuditLog.builder()
                .id(doc.getId())
                .actorId(doc.getActorId())
                .actorEmail(doc.getActorEmail())
                .actorRole(doc.getActorRole())
                .action(doc.getAction() != null ? AuditAction.valueOf(doc.getAction()) : null)
                .targetId(doc.getTargetId())
                .targetType(doc.getTargetType())
                .details(doc.getDetails())
                .createdAt(doc.getCreatedAt())
                .build();
    }

    private <T> PageResult<T> toPageResult(Page<T> page) {
        return new PageResult<>(
                page.getContent(),
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize()
        );
    }
}

