package org.lupoi.workoutapp.domain.exception;/*
    @author Andrii
    @project workout
    @class DomainExeption
    @version 1.0.0
    @since 24.03.2026 - 14.40
*/

public class DomainException extends RuntimeException {
    public DomainException(String message) {
        super(message);
    }
}
