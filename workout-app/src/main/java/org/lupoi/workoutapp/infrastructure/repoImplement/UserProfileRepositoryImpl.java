package org.lupoi.workoutapp.infrastructure.repoImplement;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.enums.Difficulty;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;
import org.lupoi.workoutapp.domain.repository.ExerciseRepository;
import org.lupoi.workoutapp.infrastructure.mapper.ExerciseDocumentMapper;
import org.lupoi.workoutapp.infrastructure.repository.MongoExerciseRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UserProfileRepositoryImpl implements ExerciseRepository {

    private final MongoExerciseRepository mongoRepository;
    private final ExerciseDocumentMapper mapper;

    @Override
    public List<Exercise> findAll() {
        return mongoRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Exercise> findByDifficulty(Difficulty difficulty) {
        return mongoRepository.findByDifficulty(difficulty.name())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Exercise> findByMuscleGroup(MuscleGroup muscleGroup) {
        return mongoRepository.findByMuscleGroup(muscleGroup.name())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Exercise save(Exercise exercise) {
        return mapper.toDomain(mongoRepository.save(mapper.toDocument(exercise)));
    }

    @Override
    public long count() {
        return mongoRepository.count();
    }
}