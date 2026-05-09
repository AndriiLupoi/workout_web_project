package org.lupoi.workoutapp.presentation.dto.request.password;/*
    @author Andrii
    @project workout
    @class ForgotPasswordRequest
    @version 1.0.0
    @since 09.05.2026 - 12.12
*/

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(
        @NotBlank @Email String email
) {}
