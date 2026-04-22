package org.lupoi.workoutapp.presentation.controller;/*
    @author Andrii
    @project workout
    @class UserController
    @version 1.0.0
    @since 22.04.2026 - 21.15
*/

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.usecase.GetUserUseCase;
import org.lupoi.workoutapp.application.usecase.UpdateUserUseCase;
import org.lupoi.workoutapp.presentation.dto.request.UpdateUserRequest;
import org.lupoi.workoutapp.presentation.dto.response.UserResponse;
import org.lupoi.workoutapp.presentation.mapper.UserDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final GetUserUseCase getUseCase;
    private final UpdateUserUseCase updateUseCase;
    private final UserDtoMapper mapper;

    @GetMapping
    public ResponseEntity<UserResponse> get(Principal principal) {
        return ResponseEntity.ok(
                mapper.toResponse(getUseCase.execute(principal.getName()))
        );
    }

    @PutMapping
    public ResponseEntity<UserResponse> update(
            @RequestBody @Valid UpdateUserRequest request,
            Principal principal) {

        return ResponseEntity.ok(
                mapper.toResponse(
                        updateUseCase.execute(
                                principal.getName(),
                                request.firstName(),
                                request.lastName()
                        )
                )
        );
    }
}
