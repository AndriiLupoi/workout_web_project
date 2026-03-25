package org.lupoi.workoutapp.application.command;

public record RegisterUserCommand(
        String email,
        String password,
        String firstName,
        String lastName
) {}