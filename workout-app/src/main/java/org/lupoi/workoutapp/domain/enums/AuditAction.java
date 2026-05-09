package org.lupoi.workoutapp.domain.enums;/*
    @author Andrii
    @project workout
    @class AuditAction
    @version 1.0.0
    @since 09.05.2026 - 14.58
*/

public enum AuditAction {
    USER_CREATED,
    USER_DELETED,
    ROLE_CHANGED,
    PLAN_GENERATED,
    PLAN_DELETED,
    EXERCISE_CREATED,
    EXERCISE_UPDATED,
    EXERCISE_DELETED,
    WORKOUT_LOGGED,
    PASSWORD_RESET_REQUESTED,
    PASSWORD_RESET_COMPLETED,
    PROFILE_UPDATED
}

