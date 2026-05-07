package org.lupoi.workoutapp.presentation.controller;/*
    @author Andrii
    @project workout
    @class ProgressController
    @version 1.0.0
    @since 03.05.2026 - 17.01
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.usecase.workout.GetProgressUseCase;
import org.lupoi.workoutapp.domain.model.ProgressResult;
import org.lupoi.workoutapp.presentation.dto.response.ProgressResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final GetProgressUseCase getProgressUseCase;

    @GetMapping
    public ResponseEntity<ProgressResponse> getProgress(Principal principal) {
        ProgressResult result = getProgressUseCase.execute(principal.getName());
        return ResponseEntity.ok(toResponse(result));
    }

    private ProgressResponse toResponse(ProgressResult r) {
        List<ProgressResponse.ExerciseProgressItem> exerciseProgress = r.exerciseProgress().stream()
                .map(e -> new ProgressResponse.ExerciseProgressItem(
                        e.exerciseId(), e.exerciseName(),
                        e.entries().stream()
                                .map(w -> new ProgressResponse.WeightEntry(w.date(), w.weight(), w.weekNumber(), w.dayNumber()))
                                .toList()
                ))
                .toList();

        List<ProgressResponse.BodyWeightItem> bodyWeight = r.bodyWeightHistory().stream()
                .map(b -> new ProgressResponse.BodyWeightItem(b.date(), b.weight()))
                .toList();

        List<ProgressResponse.PrItem> prs = r.personalRecords().stream()
                .map(p -> new ProgressResponse.PrItem(p.exerciseId(), p.exerciseName(), p.maxWeight(), p.date()))
                .toList();

        return new ProgressResponse(exerciseProgress, bodyWeight, prs);
    }
}
