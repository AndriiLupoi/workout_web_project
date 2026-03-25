package org.lupoi.workoutapp.application.command;

public record LoginUserCommand(
        String email,
        String password
) {}