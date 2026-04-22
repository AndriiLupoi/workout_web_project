package org.lupoi.workoutapp.presentation.dto.request;/*
    @author Andrii
    @project workout
    @class UpdateUserRequest
    @version 1.0.0
    @since 22.04.2026 - 21.07
*/

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
        @NotBlank String firstName,
        @NotBlank String lastName
) {}