package org.lupoi.workoutapp.presentation.dto.request.password;/*
    @author Andrii
    @project workout
    @class ResetPasswordRequest
    @version 1.0.0
    @since 09.05.2026 - 12.12
*/

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank String token,
        @NotBlank @Size(min = 8) String newPassword
) {}
