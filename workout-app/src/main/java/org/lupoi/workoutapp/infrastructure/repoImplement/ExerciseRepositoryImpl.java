package org.lupoi.workoutapp.infrastructure.repoImplement;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.enums.Difficulty;
import org.lupoi.workoutapp.domain.enums.EquipmentType;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;
import org.lupoi.workoutapp.domain.repository.ExerciseRepository;
import org.lupoi.workoutapp.infrastructure.document.ExerciseDocument;
import org.lupoi.workoutapp.infrastructure.mapper.ExerciseDocumentMapper;
import org.lupoi.workoutapp.infrastructure.repository.MongoExerciseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ExerciseRepositoryImpl implements ExerciseRepository {

    private final MongoExerciseRepository mongoRepository;
    private final ExerciseDocumentMapper mapper;
    private final MongoTemplate mongoTemplate;
    private final ExerciseDocumentMapper exerciseDocumentMapper;

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

    @Override
    public List<Exercise> findByFilters(MuscleGroup muscleGroup,
                                        Difficulty difficulty,
                                        EquipmentType equipment,
                                        String sortBy) {
        Query query = new Query();

        if (muscleGroup != null)
            query.addCriteria(Criteria.where("muscleGroup").is(muscleGroup.name()));

        if (difficulty != null)
            query.addCriteria(Criteria.where("difficulty").is(difficulty.name()));

        if (equipment != null)
            query.addCriteria(Criteria.where("equipmentType").is(equipment.name()));

        Sort sort = switch (sortBy != null ? sortBy : "name") {
            case "difficulty" -> Sort.by("difficulty");
            case "muscleGroup" -> Sort.by("muscleGroup");
            default -> Sort.by("name");
        };
        query.with(sort);

        return mongoTemplate.find(query, ExerciseDocument.class)
                .stream()
                .map(exerciseDocumentMapper::toDomain)
                .toList();
    }
}