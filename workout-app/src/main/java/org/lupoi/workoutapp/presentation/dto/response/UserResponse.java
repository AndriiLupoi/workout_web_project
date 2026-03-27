package org.lupoi.workoutapp.presentation.dto.response;


public record UserResponse(
        String id,
        String email,
        String firstName,
        String lastName
) {}