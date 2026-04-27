package org.lupoi.workoutapp.presentation.controller;/*
    @author Andrii
    @project workout
    @class WorkoutLogController
    @version 1.0.0
    @since 27.04.2026 - 20.58
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.usecase.workout.GetWorkoutLogsUseCase;
import org.lupoi.workoutapp.application.usecase.workout.LogWorkoutUseCase;
import org.lupoi.workoutapp.presentation.dto.request.LogWorkoutRequest;
import org.lupoi.workoutapp.presentation.dto.response.WorkoutLogResponse;
import org.lupoi.workoutapp.presentation.mapper.WorkoutLogDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/logs")
@RequiredArgsConstructor
public class WorkoutLogController {

    private final LogWorkoutUseCase logWorkoutUseCase;
    private final GetWorkoutLogsUseCase getLogsUseCase;
    private final WorkoutLogDtoMapper mapper;

    // POST /api/v1/logs
    @PostMapping
    public ResponseEntity<WorkoutLogResponse> logWorkout(
            @RequestBody LogWorkoutRequest request,
            Principal principal) {
        var log = logWorkoutUseCase.execute(principal.getName(), mapper.toCommand(request));
        return ResponseEntity.status(201).body(mapper.toResponse(log));
    }

    // GET /api/v1/logs?planId=xxx
    @GetMapping
    public ResponseEntity<List<WorkoutLogResponse>> getLogs(
            @RequestParam String planId,
            Principal principal) {
        var logs = getLogsUseCase.executeByPlan(principal.getName(), planId);
        return ResponseEntity.ok(logs.stream().map(mapper::toResponse).toList());
    }

    // GET /api/v1/logs/day?planId=xxx&week=1&day=2
    @GetMapping("/day")
    public ResponseEntity<WorkoutLogResponse> getLogForDay(
            @RequestParam String planId,
            @RequestParam int week,
            @RequestParam int day,
            Principal principal) {
        return getLogsUseCase
                .executeForDay(principal.getName(), planId, week, day)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}

