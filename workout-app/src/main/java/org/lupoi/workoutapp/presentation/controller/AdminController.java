package org.lupoi.workoutapp.presentation.controller;/*
    @author Andrii
    @project workout
    @class AdminController
    @version 1.0.0
    @since 03.05.2026 - 11.08
*/

import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.command.ExerciseCommand;
import org.lupoi.workoutapp.application.usecase.admin.GetStatsUseCase;
import org.lupoi.workoutapp.application.usecase.admin.ManageExerciseUseCase;
import org.lupoi.workoutapp.application.usecase.workout.exercises.UpdateExerciseVideoUseCase;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.domain.enums.Role;
import org.lupoi.workoutapp.domain.model.PageResult;
import org.lupoi.workoutapp.domain.repository.UserProfileRepository;
import org.lupoi.workoutapp.domain.repository.UserRepository;
import org.lupoi.workoutapp.domain.repository.WorkoutPlanRepository;
import org.lupoi.workoutapp.presentation.dto.request.ExerciseRequest;
import org.lupoi.workoutapp.presentation.dto.response.*;
import org.lupoi.workoutapp.presentation.mapper.ExerciseDtoMapper;
import org.lupoi.workoutapp.presentation.mapper.ProfileDtoMapper;
import org.lupoi.workoutapp.presentation.mapper.UserDtoMapper;
import org.lupoi.workoutapp.presentation.mapper.WorkoutPlanDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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


    // GET /api/v1/admin/users — список всіх юзерів (ADMIN + OWNER)
    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        // Захист від занадто великих розмірів сторінок
        int safeSize = Math.min(size, 100);

        PageResult<User> pageResult =
                userRepository.findAll(page, safeSize);

        List<UserResponse> content = pageResult.content().stream()
                .map(userDtoMapper::toResponse)
                .toList();

        return ResponseEntity.ok(PageResponse.of(
                content,
                pageResult.currentPage(),
                pageResult.totalPages(),
                pageResult.totalElements(),
                pageResult.pageSize()
        ));
    }


    @GetMapping("/stats")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<StatsResponse> getStats() {
        var result = getStatsUseCase.execute(); // повертає StatsResult
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

    // GET /api/v1/admin/users/{userId}/plans — плани юзера (тільки OWNER)
    @GetMapping("/users/{userId}/plans")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<List<WorkoutPlanResponse>> getUserPlans(@PathVariable String userId) {
        var plans = workoutPlanRepository.findByUserId(userId)
                .stream()
                .map(workoutPlanDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(plans);
    }



    // PUT /api/v1/admin/roles/{userId}?role=ADMIN — призначити роль (тільки OWNER)
    @PutMapping("/roles/{userId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable String userId,
            @RequestParam Role role) {

        // OWNER не можна понизити через API — захист
        if (role == Role.OWNER) {
            return ResponseEntity.badRequest().build();
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Не можна змінити роль OWNER
        if (user.getRole() == Role.OWNER) {
            return ResponseEntity.status(403).build();
        }

        var updated = userRepository.updateRole(userId, role);
        return ResponseEntity.ok(userDtoMapper.toResponse(updated));
    }

    // DELETE /api/v1/admin/users/{userId} — видалити юзера (тільки OWNER)
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userRepository.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/{userId}/plans/{planId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deleteUserPlan(
            @PathVariable String userId,
            @PathVariable String planId) {
        workoutPlanRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new RuntimeException("Plan not found"));
        workoutPlanRepository.deleteById(planId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/exercises/{exerciseId}/video")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ExerciseResponse> updateExerciseVideo(
            @PathVariable String exerciseId,
            @RequestBody Map<String, String> body) {
        String videoUrl = body.get("videoUrl");
        return ResponseEntity.ok(
                exerciseDtoMapper.toResponse(
                        updateExerciseVideoUseCase.execute(exerciseId, videoUrl)
                )
        );
    }

    @PostMapping("/exercises")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ExerciseResponse> createExercise(@RequestBody ExerciseRequest req) {
        var cmd = new ExerciseCommand(req.name(), req.muscleGroup(), req.difficulty(),
                req.equipmentType(), req.description(), req.videoUrl());
        return ResponseEntity.status(201).body(exerciseDtoMapper.toResponse(manageExerciseUseCase.create(cmd)));
    }


    @PutMapping("/exercises/{exerciseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<ExerciseResponse> updateExercise(@PathVariable String exerciseId,
                                                           @RequestBody ExerciseRequest req) {
        var cmd = new ExerciseCommand(req.name(), req.muscleGroup(), req.difficulty(),
                req.equipmentType(), req.description(), req.videoUrl());
        return ResponseEntity.ok(exerciseDtoMapper.toResponse(manageExerciseUseCase.update(exerciseId, cmd)));
    }


    @DeleteMapping("/exercises/{exerciseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'OWNER')")
    public ResponseEntity<Void> deleteExercise(@PathVariable String exerciseId) {
        manageExerciseUseCase.delete(exerciseId);
        return ResponseEntity.noContent().build();
    }

}

