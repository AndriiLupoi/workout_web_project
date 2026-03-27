package org.lupoi.workoutapp.presentation.controller;/*
    @author Andrii
    @project workout
    @class WorkoutPlanController
    @version 1.0.0
    @since 27.03.2026 - 21.07
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.usecase.GenerateWorkoutPlanUseCase;
import org.lupoi.workoutapp.application.usecase.GetUserPlansUseCase;
import org.lupoi.workoutapp.presentation.dto.response.WorkoutPlanResponse;
import org.lupoi.workoutapp.presentation.mapper.WorkoutPlanDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/plans")
@RequiredArgsConstructor
public class WorkoutPlanController {

    private final GenerateWorkoutPlanUseCase generateUseCase;
    private final GetUserPlansUseCase getPlansUseCase;
    private final WorkoutPlanDtoMapper mapper;

    @PostMapping("/generate")
    public ResponseEntity<WorkoutPlanResponse> generate(Principal principal) {
        return ResponseEntity.status(201).body(
                mapper.toResponse(generateUseCase.execute(principal.getName()))
        );
    }

    @GetMapping
    public ResponseEntity<List<WorkoutPlanResponse>> getAll(Principal principal) {
        return ResponseEntity.ok(
                getPlansUseCase.execute(principal.getName())
                        .stream()
                        .map(mapper::toResponse)
                        .toList()
        );
    }
}