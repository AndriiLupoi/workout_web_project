package org.lupoi.workoutapp.infrastructure.repository;

import org.lupoi.workoutapp.infrastructure.document.WorkoutPlanDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MongoWorkoutPlanRepository extends MongoRepository<WorkoutPlanDocument, String> {
    List<WorkoutPlanDocument> findByUserId(String userId);
    Optional<WorkoutPlanDocument> findByIdAndUserId(String id, String userId);
}
