package org.lupoi.workoutapp.presentation.dto.response.user;


public record UserResponse(
        String id,
        String email,
        String firstName,
        String lastName,
        String role,
        String createdAt

) {}