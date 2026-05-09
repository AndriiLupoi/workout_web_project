package org.lupoi.workoutapp.application.usecase.workout.exercises;/*
    @author Andrii
    @project workout
    @class UpdateExerciseVideoUseCase
    @version 1.0.0
    @since 05.05.2026 - 21.18
*/


import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.domain.entity.workout.Exercise;
import org.lupoi.workoutapp.domain.repository.ExerciseRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateExerciseVideoUseCase {

    private final ExerciseRepository exerciseRepository;

    public Exercise execute(String exerciseId, String videoUrl) {
        return exerciseRepository.updateVideoUrl(exerciseId, videoUrl);
    }
}