package org.lupoi.workoutapp.domain.repository;

import org.lupoi.workoutapp.domain.entity.BodyWeightLog;

import java.util.List;
import java.util.Optional;

public interface BodyWeightLogRepository {
    BodyWeightLog save(BodyWeightLog log);
    List<BodyWeightLog> findByUserId(String userId);
    Optional<BodyWeightLog> findByUserIdAndDate(String userId, java.time.LocalDate date);
}