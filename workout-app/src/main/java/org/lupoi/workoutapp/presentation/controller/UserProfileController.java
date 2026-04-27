package org.lupoi.workoutapp.presentation.controller;/*
    @author Andrii
    @project workout
    @class UserProfileController
    @version 1.0.0
    @since 27.03.2026 - 21.05
*/

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.usecase.user.GetUserProfileUseCase;
import org.lupoi.workoutapp.application.usecase.user.SaveUserProfileUseCase;
import org.lupoi.workoutapp.presentation.dto.request.SaveProfileRequest;
import org.lupoi.workoutapp.presentation.dto.response.ProfileResponse;
import org.lupoi.workoutapp.presentation.mapper.ProfileDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final SaveUserProfileUseCase saveUseCase;
    private final GetUserProfileUseCase getUseCase;
    private final ProfileDtoMapper mapper;

    @PutMapping
    public ResponseEntity<ProfileResponse> save(
            @RequestBody @Valid SaveProfileRequest request,
            Principal principal) {
        return ResponseEntity.ok(
                mapper.toResponse(saveUseCase.execute(principal.getName(), mapper.toCommand(request)))
        );
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> get(Principal principal) {
        return ResponseEntity.ok(
                mapper.toResponse(getUseCase.execute(principal.getName()))
        );
    }
}