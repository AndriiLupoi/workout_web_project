package org.lupoi.workoutapp.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.usecase.GetExercisesUseCase;
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
    public ResponseEntity<List<ExerciseResponse>> getAll(
            @RequestParam(required = false) String muscleGroup,
            @RequestParam(required = false) String difficulty) {
        return ResponseEntity.ok(
                getExercisesUseCase.execute(muscleGroup, difficulty)
                        .stream()
                        .map(mapper::toResponse)
                        .toList()
        );
    }
}