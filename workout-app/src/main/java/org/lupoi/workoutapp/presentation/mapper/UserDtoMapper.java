package org.lupoi.workoutapp.presentation.mapper;

import org.lupoi.workoutapp.application.command.LoginUserCommand;
import org.lupoi.workoutapp.application.command.RegisterUserCommand;
import org.lupoi.workoutapp.domain.entity.User;
import org.lupoi.workoutapp.presentation.dto.request.LoginRequest;
import org.lupoi.workoutapp.presentation.dto.request.RegisterRequest;
import org.lupoi.workoutapp.presentation.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserDtoMapper {

    default RegisterUserCommand toCommand(RegisterRequest request) {
        return new RegisterUserCommand(
                request.email(),
                request.password(),
                request.firstName(),
                request.lastName()
        );
    }

    default LoginUserCommand toLoginCommand(LoginRequest request) {
        return new LoginUserCommand(
                request.email(),
                request.password()
        );
    }

    @Mapping(
            target = "role",
            expression = "java(user.getRole() != null ? user.getRole().name() : \"USER\")"
    )
    @Mapping(
            target = "createdAt",
            expression = "java(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)"
    )
    UserResponse toResponse(User user);
}