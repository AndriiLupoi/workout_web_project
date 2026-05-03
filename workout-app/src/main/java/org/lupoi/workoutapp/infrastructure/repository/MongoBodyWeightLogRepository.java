package org.lupoi.workoutapp.infrastructure.repository;

import org.lupoi.workoutapp.infrastructure.document.BodyWeightLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MongoBodyWeightLogRepository extends MongoRepository<BodyWeightLogDocument, String> {
    List<BodyWeightLogDocument> findByUserIdOrderByDateAsc(String userId);
    Optional<BodyWeightLogDocument> findByUserIdAndDate(String userId, LocalDate date);
}