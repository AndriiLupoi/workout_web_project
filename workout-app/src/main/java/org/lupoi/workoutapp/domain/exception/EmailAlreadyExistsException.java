package org.lupoi.workoutapp.domain.exception;/*
    @author Andrii
    @project workout
    @class EmailAlreadyExistsException
    @version 1.0.0
    @since 24.03.2026 - 14.41
*/

public class EmailAlreadyExistsException extends DomainException {
    public EmailAlreadyExistsException(String email) {
        super("Email already registered: " + email);
    }
}