package org.lupoi.workoutapp.application.usecase;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.enums.Difficulty;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;
import org.lupoi.workoutapp.domain.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetExercisesUseCase {

    private final ExerciseRepository exerciseRepository;

    public List<Exercise> execute(String muscleGroup, String difficulty) {
        if (muscleGroup != null) {
            return exerciseRepository.findByMuscleGroup(
                    MuscleGroup.valueOf(muscleGroup.toUpperCase())
            );
        }
        if (difficulty != null) {
            return exerciseRepository.findByDifficulty(
                    Difficulty.valueOf(difficulty.toUpperCase())
            );
        }
        return exerciseRepository.findAll();
    }
}
