package org.lupoi.workoutapp.application.usecase;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.enums.Difficulty;
import org.lupoi.workoutapp.domain.enums.EquipmentType;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;
import org.lupoi.workoutapp.domain.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetExercisesUseCase {

    private final ExerciseRepository exerciseRepository;

    public List<Exercise> execute(
            MuscleGroup muscleGroup,
            Difficulty difficulty,
            EquipmentType equipment,
            String sortBy
    ) {
        return exerciseRepository.findByFilters(
                muscleGroup,
                difficulty,
                equipment,
                sortBy
        );
    }
}
