package org.lupoi.workoutapp.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.lupoi.workoutapp.application.usecase.user.LoginUserUseCase;
import org.lupoi.workoutapp.application.usecase.user.RegisterUserUseCase;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.presentation.dto.request.LoginRequest;
import org.lupoi.workoutapp.presentation.dto.request.RegisterRequest;
import org.lupoi.workoutapp.presentation.dto.response.JwtResponse;
import org.lupoi.workoutapp.presentation.dto.response.UserResponse;
import org.lupoi.workoutapp.presentation.mapper.UserDtoMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegisterUserUseCase registerUseCase;
    private final LoginUserUseCase loginUseCase;
    private final UserDtoMapper mapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody @Valid RegisterRequest request) {
        User user = registerUseCase.execute(mapper.toCommand(request));
        return ResponseEntity.status(201).body(mapper.toResponse(user));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(
            @RequestBody @Valid LoginRequest request) {
        String token = loginUseCase.execute(mapper.toLoginCommand(request));
        return ResponseEntity.ok(new JwtResponse(token));
    }
}