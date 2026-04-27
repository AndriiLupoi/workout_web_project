package org.lupoi.workoutapp.infrastructure.repoImplement;/*
    @author Andrii
    @project workout
    @class WorkoutLogRepositoryImpl
    @version 1.0.0
    @since 27.04.2026 - 20.55
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.WorkoutLog;
import org.lupoi.workoutapp.domain.repository.WorkoutLogRepository;
import org.lupoi.workoutapp.infrastructure.mapper.WorkoutLogDocumentMapper;
import org.lupoi.workoutapp.infrastructure.repository.MongoWorkoutLogRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WorkoutLogRepositoryImpl implements WorkoutLogRepository {

    private final MongoWorkoutLogRepository mongo;
    private final WorkoutLogDocumentMapper mapper;

    @Override
    public WorkoutLog save(WorkoutLog log) {
        return mapper.toDomain(mongo.save(mapper.toDocument(log)));
    }

    @Override
    public List<WorkoutLog> findByUserIdAndPlanId(String userId, String planId) {
        return mongo.findByUserIdAndPlanId(userId, planId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<WorkoutLog> findByUserId(String userId) {
        return mongo.findByUserId(userId)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<WorkoutLog> findByUserIdAndPlanIdAndWeekAndDay(
            String userId, String planId, int weekNumber, int dayNumber) {
        return mongo.findByUserIdAndPlanIdAndWeekNumberAndDayNumber(
                        userId, planId, weekNumber, dayNumber)
                .map(mapper::toDomain);
    }
}
