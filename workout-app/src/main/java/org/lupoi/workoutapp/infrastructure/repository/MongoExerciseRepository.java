package org.lupoi.workoutapp.infrastructure.repository;

import org.lupoi.workoutapp.infrastructure.document.ExerciseDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MongoExerciseRepository extends MongoRepository<ExerciseDocument, String> {
    List<ExerciseDocument> findByDifficulty(String difficulty);
    List<ExerciseDocument> findByMuscleGroup(String muscleGroup);
}