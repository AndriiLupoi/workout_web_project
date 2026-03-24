package org.lupoi.workoutapp.domain.exception;/*
    @author Andrii
    @project workout
    @class ProfileNotFoundException
    @version 1.0.0
    @since 24.03.2026 - 14.51
*/

public class ProfileNotFoundException extends DomainException {
    public ProfileNotFoundException(String userId) {
        super("Profile not found for user: " + userId);
    }
}