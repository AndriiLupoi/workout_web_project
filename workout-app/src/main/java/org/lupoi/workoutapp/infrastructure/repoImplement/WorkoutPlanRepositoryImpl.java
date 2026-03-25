package org.lupoi.workoutapp.infrastructure.repoImplement;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.WorkoutPlan;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.lupoi.workoutapp.infrastructure.mapper.WorkoutPlanDocumentMapper;
import org.lupoi.workoutapp.infrastructure.repository.MongoWorkoutPlanRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;


@Component
@RequiredArgsConstructor
public class WorkoutPlanRepositoryImpl implements WorkoutPlanRepository {

    private final MongoWorkoutPlanRepository mongoRepository;
    private final WorkoutPlanDocumentMapper mapper;

    @Override
    public WorkoutPlan save(WorkoutPlan plan) {
        return mapper.toDomain(mongoRepository.save(mapper.toDocument(plan)));
    }

    @Override
    public List<WorkoutPlan> findByUserId(String userId) {
        return mongoRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<WorkoutPlan> findByIdAndUserId(String id, String userId) {
        return mongoRepository.findByIdAndUserId(id, userId)
                .map(mapper::toDomain);
    }
}