package org.lupoi.workoutapp.presentation.controller;/*
    @author Andrii
    @project workout
    @class WorkoutLogController
    @version 1.0.0
    @since 27.04.2026 - 20.58
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.usecase.workout.plan.GetWeightRecommendationUseCase;
import org.lupoi.workoutapp.application.usecase.workout.progress.GetPlanProgressUseCase;
import org.lupoi.workoutapp.application.usecase.workout.logs.GetWorkoutLogsUseCase;
import org.lupoi.workoutapp.application.usecase.workout.logs.LogWorkoutUseCase;
import org.lupoi.workoutapp.domain.model.WorkoutLogResult;
import org.lupoi.workoutapp.presentation.dto.request.LogWorkoutRequest;
import org.lupoi.workoutapp.presentation.dto.response.PlanProgressResponse;
import org.lupoi.workoutapp.presentation.dto.response.WeightRecommendationResponse;
import org.lupoi.workoutapp.presentation.dto.response.WorkoutLogResponse;
import org.lupoi.workoutapp.presentation.dto.response.WorkoutLogResultResponse;
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
    private final GetPlanProgressUseCase getPlanProgressUseCase;
    private final GetWeightRecommendationUseCase getWeightRecommendationUseCase;
    private final WorkoutLogDtoMapper mapper;

    // POST /api/v1/logs — зберегти тренування, отримати PR у відповідь
    @PostMapping
    public ResponseEntity<WorkoutLogResultResponse> logWorkout(
            @RequestBody LogWorkoutRequest request,
            Principal principal) {
        WorkoutLogResult result = logWorkoutUseCase.execute(principal.getName(), mapper.toCommand(request));
        return ResponseEntity.status(201).body(toResultResponse(result));
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

    // GET /api/v1/logs/stats?planId=xxx
    @GetMapping("/stats")
    public ResponseEntity<PlanProgressResponse> getPlanStats(
            @RequestParam String planId,
            Principal principal) {
        var result = getPlanProgressUseCase.execute(principal.getName(), planId);
        return ResponseEntity.ok(new PlanProgressResponse(
                result.totalDays(), result.completedDays(), result.currentStreak()));
    }

    // GET /api/v1/logs/recommendation?planId=xxx&exerciseId=yyy&plannedWeight=50
    @GetMapping("/recommendation")
    public ResponseEntity<WeightRecommendationResponse> getWeightRecommendation(
            @RequestParam String planId,
            @RequestParam String exerciseId,
            @RequestParam(required = false) Double plannedWeight,
            Principal principal) {
        return getWeightRecommendationUseCase
                .execute(principal.getName(), planId, exerciseId, plannedWeight)
                .map(r -> ResponseEntity.ok(new WeightRecommendationResponse(r.weight(), r.label(), r.hint())))
                .orElse(ResponseEntity.noContent().build());
    }

    private WorkoutLogResultResponse toResultResponse(WorkoutLogResult result) {
        List<WorkoutLogResultResponse.ExercisePrResponse> prs = result.personalRecords().stream()
                .map(pr -> new WorkoutLogResultResponse.ExercisePrResponse(
                        pr.exerciseId(), pr.exerciseName(),
                        pr.newWeight(), pr.previousBest(), pr.delta()))
                .toList();
        return new WorkoutLogResultResponse(result.logId(), result.prCount(), prs);
    }
}
