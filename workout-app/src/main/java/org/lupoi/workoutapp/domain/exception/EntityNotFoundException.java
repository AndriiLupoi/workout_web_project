package org.lupoi.workoutapp.domain.exception;/*
    @author Andrii
    @project workout
    @class EntityNotFoundException
    @version 1.0.0
    @since 24.03.2026 - 14.41
*/

public class EntityNotFoundException extends DomainException {
    public EntityNotFoundException(String entity, String id) {
        super(entity + " not found: " + id);
    }
}