package org.lupoi.workoutapp.infrastructure.repoImplement;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.BodyWeightLog;
import org.lupoi.workoutapp.domain.repository.BodyWeightLogRepository;
import org.lupoi.workoutapp.infrastructure.mapper.BodyWeightLogDocumentMapper;
import org.lupoi.workoutapp.infrastructure.repository.MongoBodyWeightLogRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BodyWeightLogRepositoryImpl implements BodyWeightLogRepository {

    private final MongoBodyWeightLogRepository mongo;
    private final BodyWeightLogDocumentMapper mapper;

    @Override
    public BodyWeightLog save(BodyWeightLog log) {
        return mapper.toDomain(mongo.save(mapper.toDocument(log)));
    }

    @Override
    public List<BodyWeightLog> findByUserId(String userId) {
        return mongo.findByUserIdOrderByDateAsc(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<BodyWeightLog> findByUserIdAndDate(String userId, LocalDate date) {
        return mongo.findByUserIdAndDate(userId, date)
                .map(mapper::toDomain);
    }
}