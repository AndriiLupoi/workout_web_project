package org.lupoi.workoutapp.presentation.controller;/*
    @author Andrii
    @project workout
    @class ProgressController
    @version 1.0.0
    @since 03.05.2026 - 17.01
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.usecase.workout.GetProgressUseCase;
import org.lupoi.workoutapp.presentation.dto.response.ProgressResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final GetProgressUseCase getProgressUseCase;

    @GetMapping
    public ResponseEntity<ProgressResponse> getProgress(Principal principal) {
        return ResponseEntity.ok(getProgressUseCase.execute(principal.getName()));
    }
}