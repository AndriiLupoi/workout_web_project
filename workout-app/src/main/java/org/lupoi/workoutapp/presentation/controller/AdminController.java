package org.lupoi.workoutapp.presentation.controller;

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.command.ExerciseCommand;
import org.lupoi.workoutapp.application.service.AuditService;
import org.lupoi.workoutapp.application.usecase.admin.GetStatsUseCase;
import org.lupoi.workoutapp.application.usecase.admin.ManageExerciseUseCase;
import org.lupoi.workoutapp.application.usecase.workout.exercises.UpdateExerciseVideoUseCase;
import org.lupoi.workoutapp.domain.entity.AuditLog;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.enums.AuditAction;
import org.lupoi.workoutapp.domain.enums.Role;
import org.lupoi.workoutapp.domain.model.PageResult;
import org.lupoi.workoutapp.domain.repository.AuditLogRepository;
import org.lupoi.workoutapp.domain.repository.UserProfileRepository;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.lupoi.workoutapp.presentation.dto.request.ExerciseRequest;
import org.lupoi.workoutapp.presentation.dto.response.*;
import org.lupoi.workoutapp.presentation.dto.response.logs.AuditLogResponse;
import org.lupoi.workoutapp.presentation.dto.response.user.ProfileResponse;
import org.lupoi.workoutapp.presentation.dto.response.user.UserResponse;
import org.lupoi.workoutapp.presentation.dto.response.workout.ExerciseResponse;
import org.lupoi.workoutapp.presentation.dto.response.workout.StatsResponse;
import org.lupoi.workoutapp.presentation.dto.response.workout.WorkoutPlanResponse;
import org.lupoi.workoutapp.presentation.mapper.ExerciseDtoMapper;
import org.lupoi.workoutapp.presentation.mapper.ProfileDtoMapper;
import org.lupoi.workoutapp.presentation.mapper.UserDtoMapper;
import org.lupoi.workoutapp.presentation.mapper.WorkoutPlanDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final UserDtoMapper userDtoMapper;
    private final ProfileDtoMapper profileDtoMapper;
    private final WorkoutPlanDtoMapper workoutPlanDtoMapper;
    private final WorkoutPlanRepository workoutPlanRepository;
    private final GetStatsUseCase getStatsUseCase;
    private final UserProfileRepository userProfileRepository;
    private final UpdateExerciseVideoUseCase updateExerciseVideoUseCase;
    private final ExerciseDtoMapper exerciseDtoMapper;
    private final ManageExerciseUseCase manageExerciseUseCase;
    private final AuditLogRepository auditLogRepository;
    private final AuditService auditService;

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safeSize = Math.min(size, 100);
        PageResult<User> pageResult = userRepository.findAll(page, safeSize);
        List<UserResponse> content = pageResult.content().stream()
                .map(userDtoMapper::toResponse).toList();
        return ResponseEntity.ok(PageResponse.of(
                content, pageResult.currentPage(), pageResult.totalPages(),
                pageResult.totalElements(), pageResult.pageSize()
        ));
    }

    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<StatsResponse> getStats() {
        var result = getStatsUseCase.execute();
        return ResponseEntity.ok(new StatsResponse(result.totalUsers(), result.totalPlans(), result.totalAdmins()));
    }

    @GetMapping("/users/{userId}/profile")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ProfileResponse> getUserProfile(@PathVariable String userId) {
        return userProfileRepository.findByUserId(userId)
                .map(profileDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{userId}/plans")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<WorkoutPlanResponse>> getUserPlans(@PathVariable String userId) {
        var plans = workoutPlanRepository.findByUserId(userId).stream()
                .map(workoutPlanDtoMapper::toResponse).toList();
        return ResponseEntity.ok(plans);
    }

    // Аудит тут — немає окремого UseCase для зміни ролі
    @PutMapping("/roles/{userId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable String userId,
            @RequestParam Role role,
            Principal principal) {

        if (role == Role.OWNER) return ResponseEntity.badRequest().build();

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() == Role.OWNER) return ResponseEntity.status(403).build();

        var updated = userRepository.updateRole(userId, role);

        var actor = resolveActor(principal);
        auditService.log(
                actor.getId(), actor.getEmail(), actor.getRole().name(),
                AuditAction.ROLE_CHANGED, userId, "User",
                "Роль змінено на " + role.name() + " для " + user.getEmail()
        );

        return ResponseEntity.ok(userDtoMapper.toResponse(updated));
    }

    // Аудит тут — немає окремого UseCase для видалення юзера
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId, Principal principal) {
        var target = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.deleteById(userId);

        var actor = resolveActor(principal);
        auditService.log(
                actor.getId(), actor.getEmail(), actor.getRole().name(),
                AuditAction.USER_DELETED, userId, "User",
                "Видалено: " + target.getEmail()
        );

        return ResponseEntity.noContent().build();
    }

    // Аудит тут — немає окремого UseCase
    @DeleteMapping("/users/{userId}/plans/{planId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteUserPlan(
            @PathVariable String userId,
            @PathVariable String planId,
            Principal principal) {

        workoutPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        workoutPlanRepository.deleteById(planId);

        var actor = resolveActor(principal);
        auditService.log(
                actor.getId(), actor.getEmail(), actor.getRole().name(),
                AuditAction.PLAN_DELETED, planId, "WorkoutPlan",
                "Адмін видалив план користувача: " + userId
        );

        return ResponseEntity.noContent().build();
    }

    // Аудит в ManageExerciseUseCase
    @PatchMapping("/exercises/{exerciseId}/video")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ExerciseResponse> updateExerciseVideo(
            @PathVariable String exerciseId,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(exerciseDtoMapper.toResponse(
                updateExerciseVideoUseCase.execute(exerciseId, body.get("videoUrl"))
        ));
    }

    @PostMapping("/exercises")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ExerciseResponse> createExercise(
            @RequestBody ExerciseRequest req,
            Principal principal) {
        var actor = resolveActor(principal);
        var cmd = new ExerciseCommand(req.name(), req.muscleGroup(), req.difficulty(),
                req.equipmentType(), req.description(), req.videoUrl());
        return ResponseEntity.status(201).body(exerciseDtoMapper.toResponse(
                manageExerciseUseCase.create(cmd, actor.getId(), actor.getEmail(), actor.getRole().name())
        ));
    }

    @PutMapping("/exercises/{exerciseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ExerciseResponse> updateExercise(
            @PathVariable String exerciseId,
            @RequestBody ExerciseRequest req,
            Principal principal) {
        var actor = resolveActor(principal);
        var cmd = new ExerciseCommand(req.name(), req.muscleGroup(), req.difficulty(),
                req.equipmentType(), req.description(), req.videoUrl());
        return ResponseEntity.ok(exerciseDtoMapper.toResponse(
                manageExerciseUseCase.update(exerciseId, cmd, actor.getId(), actor.getEmail(), actor.getRole().name())
        ));
    }

    @DeleteMapping("/exercises/{exerciseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<Void> deleteExercise(
            @PathVariable String exerciseId,
            Principal principal) {
        var actor = resolveActor(principal);
        manageExerciseUseCase.delete(exerciseId, actor.getId(), actor.getEmail(), actor.getRole().name());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/audit")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PageResponse<AuditLogResponse>> getAuditLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {

        int safeSize = Math.min(size, 100);
        PageResult<AuditLog> result;
        if (action != null && !action.isBlank()) {
            result = auditLogRepository.findByAction(action, page, safeSize);
        } else if (from != null && to != null) {
            result = auditLogRepository.findByDateRange(from, to, page, safeSize);
        } else {
            result = auditLogRepository.findAll(page, safeSize);
        }

        List<AuditLogResponse> content = result.content().stream()
                .map(l -> new AuditLogResponse(
                        l.getId(), l.getActorId(), l.getActorEmail(), l.getActorRole(),
                        l.getAction() != null ? l.getAction().name() : null,
                        l.getTargetId(), l.getTargetType(), l.getDetails(),
                        l.getCreatedAt() != null ? l.getCreatedAt().toString() : null
                )).toList();

        return ResponseEntity.ok(PageResponse.of(
                content, result.currentPage(), result.totalPages(),
                result.totalElements(), result.pageSize()
        ));
    }

    private User resolveActor(Principal principal) {
        return userRepository.findById(principal.getName())
                .orElseThrow(() -> new RuntimeException("Actor not found"));
    }
}