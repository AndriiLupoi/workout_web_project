package org.lupoi.workoutapp.application.port;/*
    @author Andrii
    @project workout
    @class TokenProvider
    @version 1.0.0
    @since 25.03.2026 - 21.11
*/

public interface TokenProvider {
    String generateToken(String userId, String email);
}