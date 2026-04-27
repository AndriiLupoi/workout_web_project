package org.lupoi.workoutapp.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.usecase.workout.GetExercisesUseCase;
import org.lupoi.workoutapp.domain.entity.Exercise;
import org.lupoi.workoutapp.domain.enums.Difficulty;
import org.lupoi.workoutapp.domain.enums.EquipmentType;
import org.lupoi.workoutapp.domain.enums.MuscleGroup;
import org.lupoi.workoutapp.presentation.dto.response.ExerciseResponse;
import org.lupoi.workoutapp.presentation.mapper.ExerciseDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/exercises")
@RequiredArgsConstructor
public class ExerciseController {

    private final GetExercisesUseCase getExercisesUseCase;
    private final ExerciseDtoMapper mapper;

    @GetMapping
    public ResponseEntity<List<ExerciseResponse>> getExercises(
            @RequestParam(required = false) MuscleGroup muscleGroup,
            @RequestParam(required = false) Difficulty difficulty,
            @RequestParam(required = false) EquipmentType equipment,
            @RequestParam(required = false) String sortBy) {

        List<Exercise> exercises = getExercisesUseCase
                .execute(muscleGroup, difficulty, equipment, sortBy);

        return ResponseEntity.ok(
                exercises.stream().map(mapper::toResponse).toList()
        );
    }
}