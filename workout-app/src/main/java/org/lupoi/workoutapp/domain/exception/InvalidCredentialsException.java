package org.lupoi.workoutapp.domain.exception;/*
    @author Andrii
    @project workout
    @class InvalidCredentialsException
    @version 1.0.0
    @since 24.03.2026 - 14.51
*/

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
