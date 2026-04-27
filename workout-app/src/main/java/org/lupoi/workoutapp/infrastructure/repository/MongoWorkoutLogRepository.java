package org.lupoi.workoutapp.infrastructure.repository;/*
    @author Andrii
    @project workout
    @class MongoWorkoutLogRepository
    @version 1.0.0
    @since 27.04.2026 - 20.43
*/

import org.lupoi.workoutapp.infrastructure.document.WorkoutLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MongoWorkoutLogRepository extends MongoRepository<WorkoutLogDocument, String> {
    List<WorkoutLogDocument> findByUserIdAndPlanId(String userId, String planId);
    List<WorkoutLogDocument> findByUserId(String userId);
    Optional<WorkoutLogDocument> findByUserIdAndPlanIdAndWeekNumberAndDayNumber(
            String userId, String planId, int weekNumber, int dayNumber);
}

