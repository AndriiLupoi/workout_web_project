package org.lupoi.workoutapp.presentation.dto.response;

public record ErrorResponse(
        String code,
        String message
) {}
